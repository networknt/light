/*
 * Copyright 2015 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.networknt.light.rule.rule;

import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by steve on 30/12/14.
 */
public class ImpRuleEvRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(ImpRuleEvRule.class);

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        impRule(data);
        return true;
    }

    private String impRule(Map<String, Object> data) throws Exception {
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            // remove the existing rule if there is.
            OIndex<?> ruleClassIdx = db.getMetadata().getIndexManager().getIndex("Rule.ruleClass");
            OIdentifiable oid = (OIdentifiable) ruleClassIdx.get(data.get("ruleClass"));
            if (oid != null) {
                logger.info("Rule {} exists in db. Removing...", data.get("ruleClass"));
                db.delete((ODocument) oid.getRecord());
            }
            // create a new rule
            ODocument rule = new ODocument(schema.getClass("Rule"));
            rule.field("ruleClass", data.get("ruleClass"));
            if(data.get("host") != null) rule.field("host", data.get("host"));
            rule.field("sourceCode", data.get("sourceCode"));
            rule.field("createDate", data.get("createDate"));
            rule.field("createUserId", data.get("createUserId"));
            rule.save();
            db.commit();
            json  = rule.toJSON();
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        return json;
    }
}
