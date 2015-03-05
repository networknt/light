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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OCompositeKey;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by husteve on 10/14/2014.
 */
public abstract class AbstractRule implements Rule {
    static final Logger logger = LoggerFactory.getLogger(AbstractRule.class);

    protected ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
    public abstract boolean execute (Object ...objects) throws Exception;

    /*
    protected ODocument getCategoryByRid(String categoryRid) {
        Map<String, Object> categoryMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("categoryMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)categoryMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            categoryMap.put("cache", cache);
        }
        ODocument category = (ODocument)cache.get("categoryRid");
        if(category == null) {
            // TODO warning to increase cache if this happens.
            category = DbService.getVertexByRid(categoryRid);
            // put it into the category cache.
            if(category != null) {
                cache.put(categoryRid, category);
            }
        }
        return category;
    }

    protected ODocument getProductByRid(String productRid) {
        Map<String, Object> productMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("productMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)productMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            productMap.put("cache", cache);
        }
        ODocument product = (ODocument)cache.get("productRid");
        if(product == null) {
            // TODO warning to increase cache if this happens.
            product = DbService.getODocumentByRid(productRid);
            if(product != null) {
                cache.put(productRid, product);
            }
        }
        return product;
    }
    */
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

}
