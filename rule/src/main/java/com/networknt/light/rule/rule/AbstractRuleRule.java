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
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by husteve on 10/8/2014.
 */
public abstract class AbstractRuleRule extends AbstractRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractRuleRule.class);

    public abstract boolean execute (Object ...objects) throws Exception;

    protected String getRuleByRuleClass(String ruleClass) {
        String json = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex rule = (OrientVertex)graph.getVertexByKey("Rule.ruleClass", ruleClass);
            if(rule != null) {
                json = rule.getRecord().toJSON();
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        return json;
    }

    protected void addRule(OrientGraph graph, Map<String, Object> data) throws Exception {
        OrientVertex access = null;
        String ruleClass = (String)data.get("ruleClass");
        Vertex createUser = graph.getVertexByKey("User.userId", data.remove("createUserId"));
        OrientVertex rule = graph.addVertex("class:Rule", data);
        createUser.addEdge("Create", rule);

        // For all the newly added rules, the default security access is role based and only
        // owner can access. For some of the rules, like getForm, getMenu, they are granted
        // to anyone in the db script. Don't overwrite if access exists for these rules.

        // check if access exists for the ruleClass and add access if not.
        if(getAccessByRuleClass(ruleClass) == null) {
            access = graph.addVertex("class:Access");
            access.setProperty("ruleClass", ruleClass);
            if(ruleClass.contains("Abstract") || ruleClass.contains("_")) {
                access.setProperty("accessLevel", "N"); // abstract rule and internal beta tester rule
            } else if(ruleClass.endsWith("EvRule")) {
                access.setProperty("accessLevel", "A"); // event rule can be only called internally.
            } else {
                access.setProperty("accessLevel", "R"); // role level access
                List roles = new ArrayList();
                roles.add("owner");  // give owner access for the rule by default.
                access.setProperty("roles", roles);
            }
            access.setProperty("createDate", data.get("createDate"));
            createUser.addEdge("Create", access);
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
            cache.put(ruleClass, mapper.readValue(access.getRecord().toJSON(),
                    new TypeReference<HashMap<String, Object>>() {
                    }));
        }
    }

    protected void impRule(OrientGraph graph, Map<String, Object> data) throws Exception {
        String ruleClass = (String)data.get("ruleClass");
        Vertex rule = graph.getVertexByKey("Rule.ruleClass", ruleClass);
        // remove the existing rule if there is.
        if(rule != null) {
            graph.removeVertex(rule);
        }
        // create a new rule
        Vertex createUser = graph.getVertexByKey("User.userId", data.remove("createUserId"));
        rule = graph.addVertex("class:Rule", data);
        createUser.addEdge("Create", rule);
        // For all the newly added rules, the default security access is role based and only
        // owner can access. For some of the rules, like getForm, getMenu, they are granted
        // to anyone in the db script. Don't overwrite if access exists for these rules.
        // Also, if ruleClass contains "Abstract" then its access level should be N.

        // check if access exists for the ruleClass and add access if not.
        OrientVertex access = null;
        if(getAccessByRuleClass(ruleClass) == null) {
            access = graph.addVertex("class:Access");
            access.setProperty("ruleClass", ruleClass);
            if(ruleClass.contains("Abstract") || ruleClass.contains("_")) {
                access.setProperty("accessLevel", "N"); // abstract and internal beta tester rule
            } else if(ruleClass.endsWith("EvRule")) {
                access.setProperty("accessLevel", "A"); // event rule can be only called internally.
            } else {
                access.setProperty("accessLevel", "R"); // role level access
                List roles = new ArrayList();
                roles.add("owner");  // give owner access for the rule by default.
                access.setProperty("roles", roles);
            }
            access.setProperty("createDate", data.get("createDate"));
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
            cache.put(ruleClass, mapper.readValue(access.getRecord().toJSON(),
                    new TypeReference<HashMap<String, Object>>() {
                    }));
        }
    }

    protected void updRule(OrientGraph graph, Map<String, Object> data) throws Exception {
        Vertex rule = graph.getVertexByKey("Rule.ruleClass", data.get("ruleClass"));
        if(rule != null) {
            String sourceCode = (String)data.get("sourceCode");
            if(sourceCode != null && !sourceCode.equals(rule.getProperty("sourceCode"))) {
                rule.setProperty("sourceCode", sourceCode);
            }
            rule.setProperty("updateDate", data.get("updateDate"));
            Vertex updateUser = graph.getVertexByKey("User.userId", data.get("updateUserId"));
            if(updateUser != null) {
                updateUser.addEdge("Update", rule);
            }
        }
    }

    protected void delRule(OrientGraph graph, Map<String, Object> data) throws Exception {
        Vertex rule = graph.getVertexByKey("Rule.ruleClass", data.get("ruleClass"));
        if(rule != null) {
            graph.removeVertex(rule);
        }
    }

    protected String getRules(String host) {
        String sql = "SELECT FROM Rule";
        if(host != null) {
            sql = sql + " WHERE host = '" + host;
        }
        String json = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
            List<ODocument> rules = graph.getRawGraph().command(query).execute();
            json = OJSONWriter.listToJSON(rules, null);
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        return json;
    }

    protected String getRuleMap(OrientGraphNoTx graph, String host) throws Exception {
        String sql = "SELECT FROM Rule";
        if(host != null) {
            sql = sql + " WHERE host = '" + host;
        }
        String json = null;
        try {
            Map<String, String> ruleMap = new HashMap<String, String> ();
            for (Vertex rule : (Iterable<Vertex>) graph.command(new OCommandSQL(sql)).execute()) {
                ruleMap.put((String) rule.getProperty("ruleClass"), (String) rule.getProperty("sourceCode"));
            }
            json = mapper.writeValueAsString(ruleMap);
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        return json;
    }

    protected String getRuleDropdown(String host) {
        String sql = "SELECT FROM Rule";
        if(host != null) {
            sql = sql + " WHERE host = '" + host + "' OR host IS NULL";
        }
        String json = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
            List<ODocument> rules = graph.getRawGraph().command(query).execute();
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
            graph.shutdown();
        }
        return json;
    }

}
