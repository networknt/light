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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/8/2014.
 */
public abstract class AbstractRuleRule extends AbstractRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractRuleRule.class);

    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();

    public abstract boolean execute (Object ...objects) throws Exception;

    protected String getRuleByRuleClass(String ruleClass) {
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OIndex<?> ruleClassIdx = db.getMetadata().getIndexManager().getIndex("Rule.ruleClass");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable rule = (OIdentifiable) ruleClassIdx.get(ruleClass);
            if (rule != null && rule.getRecord() != null) {
                json = ((ODocument) rule.getRecord()).toJSON();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return json;
    }

    protected String addRule(Map<String, Object> data) throws Exception {
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
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

    protected void updRule(Map<String, Object> data) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> ruleClassIdx = db.getMetadata().getIndexManager().getIndex("Rule.ruleClass");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable oid = (OIdentifiable) ruleClassIdx.get(data.get("ruleClass"));
            if (oid != null && oid.getRecord() != null) {
                ODocument rule = (ODocument) oid.getRecord();
                String sourceCode = (String)data.get("sourceCode");
                if(sourceCode != null && !sourceCode.equals(rule.field("sourceCode"))) {
                    rule.field("sourceCode", sourceCode);
                }
                rule.field("updateDate", data.get("updateDate"));
                rule.field("updateUserId", data.get("updateUserId"));
                rule.save();
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
    }

    protected void delRule(Map<String, Object> data) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();

            OIndex<?> ruleClassIdx = db.getMetadata().getIndexManager().getIndex("Rule.ruleClass");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable oid = (OIdentifiable) ruleClassIdx.get(data.get("ruleClass"));
            if (oid != null && oid.getRecord() != null) {
                ODocument rule = (ODocument) oid.getRecord();
                rule.delete();
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
    }

    protected String getRules(String host) {
        String sql = "SELECT FROM Rule";
        if(host != null) {
            sql = sql + " WHERE host = '" + host;
        }
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
            List<ODocument> roles = db.command(query).execute();
            json = OJSONWriter.listToJSON(roles, null);
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        return json;
    }

}
