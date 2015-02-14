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

package com.networknt.light.rule.access;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 30/01/15.
 */
public abstract class AbstractAccessRule extends AbstractRule implements Rule {
    static final Logger logger = LoggerFactory.getLogger(AbstractAccessRule.class);

    public abstract boolean execute (Object ...objects) throws Exception;

    protected void updAccess(Map<String, Object> data) throws Exception {
        ODocument access = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> ruleClassIdx = db.getMetadata().getIndexManager().getIndex("Access.ruleClass");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable oid = (OIdentifiable) ruleClassIdx.get(data.get("ruleClass"));
            if (oid != null && oid.getRecord() != null) {
                access = (ODocument) oid.getRecord();
                access.field("accessLevel", data.get("accessLevel"));
                List<String> clients = (List)data.get("clients");
                if(clients != null && clients.size() > 0) {
                    access.field("clients", clients);
                } else {
                    access.removeField("clients");
                }

                List<String> roles = (List)data.get("roles");
                if(roles != null && roles.size() > 0) {
                    access.field("roles", roles);
                } else {
                    access.removeField("roles");
                }

                List<String> users = (List)data.get("users");
                if(users != null && users.size() > 0) {
                    access.field("users", users);
                } else {
                    access.removeField("users");
                }
                access.field("updateDate", data.get("updateDate"));
                access.field("updateUserId", data.get("updateUserId"));
                access.save();
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        Map<String, Object> accessMap = ServiceLocator.getInstance().getMemoryImage("accessMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)accessMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            accessMap.put("cache", cache);
        }
        String json = access.toJSON();
        cache.put(data.get("ruleClass"), mapper.readValue(json,
                new TypeReference<HashMap<String, Object>>() {
                }));
    }

    protected void delAccess(Map<String, Object> data) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> ruleClassIdx = db.getMetadata().getIndexManager().getIndex("Access.ruleClass");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable oid = (OIdentifiable) ruleClassIdx.get(data.get("ruleClass"));
            if (oid != null && oid.getRecord() != null) {
                ODocument access = (ODocument) oid.getRecord();
                access.delete();
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        Map<String, Object> accessMap = ServiceLocator.getInstance().getMemoryImage("accessMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)accessMap.get("cache");
        if(cache != null) {
            cache.remove(data.get("ruleClass"));
        }
    }

    protected String getAccesses(String host) {
        String sql = "SELECT FROM Access";
        if(host != null) {
            sql = sql + " WHERE host = '" + host;
        }
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
            List<ODocument> accesses = db.command(query).execute();
            json = OJSONWriter.listToJSON(accesses, null);
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        return json;
    }

}
