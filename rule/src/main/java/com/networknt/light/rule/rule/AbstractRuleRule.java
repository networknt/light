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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

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
        ODocument access = null;
        String ruleClass = (String)data.get("ruleClass");
        String createUserId = (String)data.get("createUserId");
        Date createDate = (Date)data.get("createDate");
        String host = (String)data.get("host");
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            ODocument rule = new ODocument(schema.getClass("Rule"));
            rule.field("ruleClass", ruleClass);
            if(host != null) rule.field("host", host);
            rule.field("sourceCode", data.get("sourceCode"));
            rule.field("createDate", createDate);
            rule.field("createUserId", createUserId);
            rule.save();

            // For all the newly added rules, the default security access is role based and only
            // owner can access. For some of the rules, like getForm, getMenu, they are granted
            // to anyone in the db script. Don't overwrite if access exists for these rules.

            // check if access exists for the ruleClass and add access if not.
            Map<String, Object> accessMap = getAccessByRuleClass(ruleClass);
            if(accessMap == null) {
                access = new ODocument(schema.getClass("Access"));
                access.field("ruleClass", ruleClass);
                if(ruleClass.contains("Abstract") || ruleClass.contains("_")) {
                    access.field("accessLevel", "N"); // abstract rule and internal beta tester rule
                } else if(ruleClass.endsWith("EvRule")) {
                    access.field("accessLevel", "A"); // event rule can be only called internally.
                } else {
                    access.field("accessLevel", "R"); // role level access
                    List roles = new ArrayList();
                    roles.add("owner");  // give owner access for the rule by default.
                    access.field("roles", roles);
                }
                access.field("createDate", data.get("createDate"));
                access.field("createUserId", createUserId);
                access.save();
            }
            db.commit();
            json  = rule.toJSON();
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        if(access != null) {
            Map<String, Object> accessMap = ServiceLocator.getInstance().getMemoryImage("accessMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)accessMap.get("cache");
            if(cache == null) {
                cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                        .maximumWeightedCapacity(1000)
                        .build();
                accessMap.put("cache", cache);
            }
            cache.put(ruleClass, mapper.readValue(access.toJSON(),
                    new TypeReference<HashMap<String, Object>>() {
                    }));
        }
        return json;
    }

    protected String impRule(Map<String, Object> data) throws Exception {
        String json = null;
        ODocument access = null;
        String ruleClass = (String)data.get("ruleClass");
        String createUserId = (String)data.get("createUserId");
        String host = (String)data.get("host");
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            // remove the existing rule if there is.
            OIndex<?> ruleClassIdx = db.getMetadata().getIndexManager().getIndex("Rule.ruleClass");
            OIdentifiable oid = (OIdentifiable) ruleClassIdx.get(data.get("ruleClass"));
            if (oid != null) {
                ((ODocument) oid.getRecord()).delete();
            }
            // create a new rule
            ODocument rule = new ODocument(schema.getClass("Rule"));
            rule.field("ruleClass", ruleClass);
            if(host != null) rule.field("host", host);
            rule.field("sourceCode", data.get("sourceCode"));
            rule.field("createDate", data.get("createDate"));
            rule.field("createUserId", createUserId);
            rule.save();
            // For all the newly added rules, the default security access is role based and only
            // owner can access. For some of the rules, like getForm, getMenu, they are granted
            // to anyone in the db script. Don't overwrite if access exists for these rules.
            // Also, if ruleClass contains "Abstract" then its access level should be N.

            // check if access exists for the ruleClass and add access if not.
            Map<String, Object> accessMap = getAccessByRuleClass(ruleClass);
            if(accessMap == null) {
                access = new ODocument(schema.getClass("Access"));
                access.field("ruleClass", ruleClass);
                if(ruleClass.contains("Abstract") || ruleClass.contains("_")) {
                    access.field("accessLevel", "N"); // abstract and internal beta tester rule
                } else if(ruleClass.endsWith("EvRule")) {
                    access.field("accessLevel", "A"); // event rule can be only called internally.
                } else {
                    access.field("accessLevel", "R"); // role level access
                    List roles = new ArrayList();
                    roles.add("owner");  // give owner access for the rule by default.
                    access.field("roles", roles);
                }
                access.field("createDate", data.get("createDate"));
                access.field("createUserId", createUserId);
                access.save();
            }
            db.commit();
            json  = rule.toJSON();
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        if(access != null) {
            Map<String, Object> accessMap = ServiceLocator.getInstance().getMemoryImage("accessMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)accessMap.get("cache");
            if(cache == null) {
                cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                        .maximumWeightedCapacity(1000)
                        .build();
                accessMap.put("cache", cache);
            }
            cache.put(ruleClass, mapper.readValue(access.toJSON(),
                    new TypeReference<HashMap<String, Object>>() {
                    }));
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
            List<ODocument> rules = db.command(query).execute();
            json = OJSONWriter.listToJSON(rules, null);
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        return json;
    }

    protected String getRuleMap(String host) throws Exception {
        String sql = "SELECT FROM Rule";
        if(host != null) {
            sql = sql + " WHERE host = '" + host;
        }
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
            List<ODocument> rules = db.command(query).execute();
            if(rules != null && rules.size() > 0) {
                // covert list to map
                Map<String, String> ruleMap = new HashMap<String, String> ();
                for(ODocument rule: rules) {
                    ruleMap.put((String)rule.field("ruleClass"), (String)rule.field("sourceCode"));
                }
                json = mapper.writeValueAsString(ruleMap);
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        return json;
    }

    protected String getRuleDropdown(String host) {
        String sql = "SELECT FROM Rule";
        if(host != null) {
            sql = sql + " WHERE host = '" + host + "' OR host IS NULL";
        }
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
            List<ODocument> rules = db.command(query).execute();
            if(rules.size() > 0) {
                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                for(ODocument doc: rules) {
                    Map<String, String> map = new HashMap<String, String>();
                    String ruleClass = doc.field("ruleClass");
                    map.put("label", ruleClass);
                    map.put("value", ruleClass);
                    list.add(map);
                }
                json = mapper.writeValueAsString(list);
            }

        } catch (Exception e) {
            logger.error("Exception:", e);
            //throw e;
        } finally {
            db.close();
        }
        return json;
    }

}
