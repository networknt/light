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

package com.networknt.light.rule.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.networknt.light.util.Util;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OCompositeKey;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.sun.org.apache.bcel.internal.generic.RET;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 21/02/15.
 */
public abstract class AbstractValidationRule extends AbstractRule implements Rule {
    static final Logger logger = LoggerFactory.getLogger(AbstractValidationRule.class);
    static final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

    public abstract boolean execute (Object ...objects) throws Exception;

    public static JsonSchema getSchema(String ruleClass) throws Exception {
        JsonSchema schema = null;
        Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage("ruleMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)ruleMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            ruleMap.put("cache", cache);
        } else {
            Map<String, Object> rule = (Map<String, Object>)cache.get(ruleClass);
            if(rule != null) {
                schema = (JsonSchema)rule.get("schema");
            }
        }
        if(schema == null) {
            OrientGraph graph = ServiceLocator.getInstance().getGraph();
            try {
                OIndex<?> validationRuleClassIdx = graph.getRawGraph().getMetadata().getIndexManager().getIndex("Validation.ruleClass");
                OIdentifiable oid = (OIdentifiable) validationRuleClassIdx.get(ruleClass);
                if (oid != null) {
                    ODocument validation = (ODocument) oid.getRecord();
                    String json = validation.toJSON();
                    JsonNode validationNode = ServiceLocator.getInstance().getMapper().readTree(json);
                    JsonNode schemaNode = validationNode.get("schema");
                    schema = factory.getJsonSchema(schemaNode);
                    Map<String, Object> rule = (Map<String, Object>)cache.get(ruleClass);
                    if(rule != null) {
                        rule.put("schema", schema);
                    } else {
                        rule = new HashMap<String, Object> ();
                        rule.put("schema", schema);
                        cache.put(ruleClass, rule);
                    }
                } else {
                    // could not find the rule validation schema from db. put null in cache so that
                    // the next operation won't check db again.
                    Map<String, Object> rule = (Map<String, Object>)cache.get(ruleClass);
                    if(rule != null) {
                        rule.put("schema", null);
                    } else {
                        rule = new HashMap<String, Object> ();
                        rule.put("schema", null);
                        cache.put(ruleClass, rule);
                    }
                }
            } catch (Exception e) {
                logger.error("Exception:", e);
                throw e;
            } finally {
                graph.shutdown();
            }
        }
        return schema;
    }

    protected String getValidation(String ruleClass) throws Exception {
        String json = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OIndex<?> validationRuleClassIdx = graph.getRawGraph().getMetadata().getIndexManager().getIndex("Validation.ruleClass");
            OIdentifiable oid = (OIdentifiable) validationRuleClassIdx.get(ruleClass);
            if (oid != null) {
                ODocument validation = (ODocument) oid.getRecord();
                json = validation.toJSON();
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        return json;
    }

    protected void addValidation(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            String createUserId = (String)data.remove("createUserId");
            OrientVertex validation = graph.addVertex("class:Validation", data);
            Vertex user = graph.getVertexByKey("User.userId", createUserId);
            user.addEdge("Create", validation);
            graph.commit();
        } catch (Exception e) {
            graph.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();;
        }
        // remove the cached list if in order to reload it
        Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage("ruleMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)ruleMap.get("cache");
        if(cache != null) {
            cache.remove(data.get("ruleClass"));
        }
    }

    protected void delValidation(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex validation = graph.getVertexByKey("Validation.ruleClass", data.get("ruleClass"));
            if(validation != null) {
                graph.removeVertex(validation);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
        // remove the cached list if in order to reload it
        Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage("ruleMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)ruleMap.get("cache");
        if(cache != null) {
            cache.remove(data.get("ruleClass"));
        }
    }

    protected void updValidation(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex validation = graph.getVertexByKey("Validation.ruleClass", data.get("ruleClass"));
            if(validation != null) {
                validation.setProperty("schema", data.get("schema"));
                validation.setProperty("updateDate", data.get("updateDate"));
            }
            Vertex user = graph.getVertexByKey("User.userId", data.get("updateUserId"));
            user.addEdge("Update", validation);
            graph.commit();
        } catch (Exception e) {
            graph.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        // remove the cached list if in order to reload it
        Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage("ruleMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)ruleMap.get("cache");
        if(cache != null) {
            cache.remove(data.get("ruleClass"));
        }
    }
}
