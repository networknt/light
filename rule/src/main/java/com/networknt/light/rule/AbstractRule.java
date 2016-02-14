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

package com.networknt.light.rule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.model.CacheObject;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OCompositeKey;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import net.engio.mbassy.bus.MBassador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by husteve on 10/14/2014.
 */
public abstract class AbstractRule implements Rule {
    static final Logger logger = LoggerFactory.getLogger(AbstractRule.class);
    static final JsonSchemaFactory schemaFactory = JsonSchemaFactory.byDefault();

    protected ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
    public abstract boolean execute (Object ...objects) throws Exception;

    protected void publishEvent(Map<String, Object> eventMap) throws Exception {
        // get class name
        //System.out.println(this.getClass().getPackage());
        //System.out.println(this.getClass().getName());
        // check if publisher is enabled.
        Map map = getRuleByRuleClass(this.getClass().getName());
        Object isPublisher = map.get("isPublisher");
        if(isPublisher != null && (boolean)isPublisher) {
            //System.out.println("isPublisher");
            MBassador<Map<String, Object>> eventBus = ServiceLocator.getInstance().getEventBus((String)eventMap.get("category"));
            eventBus.publish(eventMap);
        }
    }

    protected boolean matchEtag(Map<String, Object> inputMap, CacheObject co) {
        HttpServerExchange exchange = (HttpServerExchange)inputMap.get("exchange");
        if(exchange != null) {
            String requestETag = exchange.getRequestHeaders().getFirst(Headers.IF_NONE_MATCH);
            if (co.getEtag().equals(requestETag)) {
                exchange.setResponseCode(304); // no change
                return true;
            } else {
                exchange.getResponseHeaders().add(Headers.ETAG, co.getEtag());
                return false;
            }
        } else {
            // Exchange is always available in runtime but not available in unit test cases.
            return false;
        }
    }

    public static Map<String, Object> getRuleByRuleClass(String ruleClass) throws Exception {
        Map<String, Object> map = null;
        Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage("ruleMap");
        ConcurrentMap<String, Map<String, Object>> cache = (ConcurrentMap<String, Map<String, Object>>)ruleMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<String, Map<String, Object>>()
                    .maximumWeightedCapacity(1000)
                    .build();
            ruleMap.put("cache", cache);
        } else {
            map = cache.get(ruleClass);
        }
        if(map == null) {
            OrientGraph graph = ServiceLocator.getInstance().getGraph();
            try {
                OrientVertex rule = (OrientVertex)graph.getVertexByKey("Rule.ruleClass", ruleClass);
                if(rule != null) {
                    map = rule.getRecord().toMap();
                    // remove sourceCode as we don't need it and it is big
                    map.remove("sourceCode");

                    // convert schema to JsonSchema in order to speed up validation.
                    if(map.get("schema") != null) {
                        JsonNode schemaNode = ServiceLocator.getInstance().getMapper().valueToTree(map.get("schema"));
                        JsonSchema schema = schemaFactory.getJsonSchema(schemaNode);
                        map.put("schema", schema);
                    }

                    logger.debug("map = " + map);
                    cache.put(ruleClass, map);
                }
            } catch (Exception e) {
                logger.error("Exception:", e);
                throw e;
            } finally {
                graph.shutdown();
            }
        }
        return map;
    }

    protected Map<String, Object> getEventMap(Map<String, Object> inputMap) {
        Map<String, Object> eventMap = new HashMap<String, Object>();
        Map<String, Object> payload = (Map<String, Object>)inputMap.get("payload");
        if(payload != null) {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            if(user != null)  eventMap.put("createUserId", user.get("userId"));
        }
        // IP address is used to identify event owner if user is not logged in.
        if(inputMap.get("ipAddress") != null) {
            eventMap.put("ipAddress", inputMap.get("ipAddress"));
        }
        if(inputMap.get("host") != null) {
            eventMap.put("host", inputMap.get("host"));
        }
        if(inputMap.get("app") != null) {
            eventMap.put("app", inputMap.get("app"));
        }
        eventMap.put("category", inputMap.get("category"));
        eventMap.put("name", inputMap.get("name"));
        eventMap.put("createDate", new java.util.Date());
        eventMap.put("data", new HashMap<String, Object>());
        return eventMap;
    }


    protected ODocument getODocumentByHostId(OrientGraph graph, String index, String host, String id) {
        ODocument doc = null;
        OIndex<?> hostIdIdx = graph.getRawGraph().getMetadata().getIndexManager().getIndex(index);
        // this is a unique index, so it retrieves a OIdentifiable
        OCompositeKey key = new OCompositeKey(host, id);
        OIdentifiable oid = (OIdentifiable) hostIdIdx.get(key);
        if (oid != null) {
            doc = (ODocument)oid.getRecord();
        }
        return doc;
    }

    public Map<String, Object> getAccessByRuleClass(String ruleClass) throws Exception {
        Map<String, Object> access = null;
        Map<String, Object> accessMap = ServiceLocator.getInstance().getMemoryImage("accessMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)accessMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            accessMap.put("cache", cache);
        } else {
            access = (Map<String, Object>)cache.get(ruleClass);
        }
        if(access == null) {
            OrientGraph graph = ServiceLocator.getInstance().getGraph();
            try {
                OrientVertex accessVertex = (OrientVertex)graph.getVertexByKey("Access.ruleClass", ruleClass);
                if(accessVertex != null) {
                    String json = accessVertex.getRecord().toJSON();
                    access = mapper.readValue(json,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    cache.put(ruleClass, access);
                }
            } catch (Exception e) {
                logger.error("Exception:", e);
                throw e;
            } finally {
                graph.shutdown();
            }
        }
        return access;
    }

    public Map<String, Object> getAccessByRuleClass(OrientGraph graph, String ruleClass) throws Exception {
        Map<String, Object> access = null;
        Map<String, Object> accessMap = ServiceLocator.getInstance().getMemoryImage("accessMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)accessMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            accessMap.put("cache", cache);
        } else {
            access = (Map<String, Object>)cache.get(ruleClass);
        }
        if(access == null) {
            OrientVertex accessVertex = (OrientVertex)graph.getVertexByKey("Access.ruleClass", ruleClass);
            if(accessVertex != null) {
                String json = accessVertex.getRecord().toJSON();
                access = mapper.readValue(json,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                cache.put(ruleClass, access);
            }
        }
        return access;
    }

}
