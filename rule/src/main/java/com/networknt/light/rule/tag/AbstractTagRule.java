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

package com.networknt.light.rule.tag;

import com.fasterxml.jackson.core.type.TypeReference;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.db.AbstractDbRule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.db.record.ridbag.ORidBag;
import com.orientechnologies.orient.core.index.OCompositeKey;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientElementIterable;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by husteve on 10/21/2014.
 */
public abstract class AbstractTagRule extends AbstractBfnRule implements Rule {

    static final Logger logger = LoggerFactory.getLogger(AbstractTagRule.class);

    public abstract boolean execute(Object... objects) throws Exception;

    public boolean getTagDropdown(Object... objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String host = (String) data.get("host");
        String docs = getTagDropdownDb(host);
        if (docs != null) {
            inputMap.put("result", docs);
            return true;
        } else {
            inputMap.put("result", "No record found");
            inputMap.put("responseCode", 404);
            return false;
        }
    }

    protected String getTagDropdownDb(String host) {
        String json = null;
        String sql = "SELECT FROM Tag WHERE host = ? ORDER BY tagId";
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> docs = graph.getRawGraph().command(query).execute(host);
            if (docs.size() > 0) {
                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                for (ODocument doc : docs) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("label", (String) doc.field("tagId"));
                    map.put("value", (String) doc.field("tagId"));
                    list.add(map);
                }
                json = mapper.writeValueAsString(list);
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return json;
    }

    public boolean getTagEntity(Object... objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String tagId = (String) data.get("tagId");
        String host = (String) data.get("host");
        if(tagId == null) {
            inputMap.put("result", "tagId is required");
            inputMap.put("responseCode", 400);
            return false;
        }
        Integer pageSize = (Integer)data.get("pageSize");
        Integer pageNo = (Integer)data.get("pageNo");
        if(pageSize == null) {
            inputMap.put("result", "pageSize is required");
            inputMap.put("responseCode", 400);
            return false;
        }
        if(pageNo == null) {
            inputMap.put("result", "pageNo is required");
            inputMap.put("responseCode", 400);
            return false;
        }
        String sortDir = (String)data.get("sortDir");
        String sortedBy = (String)data.get("sortedBy");
        if(sortDir == null) {
            sortDir = "desc";
        }
        if(sortedBy == null) {
            sortedBy = "createDate";
        }

        // Get the entity list from cache.
        List<String> list = getTagEntityList(host, tagId);

        if(list != null && list.size() > 0) {
            long total = list.size();
            List<Map<String, Object>> entities = new ArrayList<Map<String, Object>>();
            for(int i = pageSize*(pageNo - 1); i < Math.min(pageSize*pageNo, list.size()); i++) {
                String entityRid = list.get(i);
                Map<String, Object> entity = getTagEntity(entityRid);
                if(entity != null) {
                    entities.add(entity);
                } else {
                    logger.warn("Could not find entity {} from List {}", entityRid, host + tagId);
                }
            }
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", total);
            result.put("entities", entities);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        } else {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", 0);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        }
    }

    protected Map<String, Object> getTagEntity(String entityRid) {
        Map<String, Object> entity = null;
        Map<String, Object> categoryMap = ServiceLocator.getInstance().getMemoryImage("categoryMap");
        ConcurrentMap<Object, Object> entityCache = (ConcurrentMap<Object, Object>)categoryMap.get("entityCache");
        if(entityCache == null) {
            entityCache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(10000)
                    .build();
            categoryMap.put("entityCache", entityCache);
        } else {
            entity = (Map<String, Object>)entityCache.get(entityRid);
        }
        if(entity == null) {
            entity = getCategoryEntityDb(entityRid);
            if(entity != null) {
                entityCache.put(entityRid, entity);
            }
        }
        return entity;
    }

    protected List<String> getTagEntityList(String host, String tagId) {
        List<String> list = null;
        Map<String, Object> categoryMap = ServiceLocator.getInstance().getMemoryImage("categoryMap");
        ConcurrentMap<Object, Object> listCache = (ConcurrentMap<Object, Object>)categoryMap.get("listCache");
        if(listCache == null) {
            listCache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(200)
                    .build();
            categoryMap.put("listCache", listCache);
        } else {
            list = (List<String>)listCache.get(host + tagId);
        }
        if(list == null) {
            list = getTagEntityListDb(host, tagId);
            if(list != null) {
                listCache.put(host + tagId, list);
            }
        }
        return list;
    }

    protected List<String> getTagEntityListDb(String host, String tagId) {
        List<String> entityList = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OIndex<?> tagHostIdIdx = graph.getRawGraph().getMetadata().getIndexManager().getIndex("tagHostIdIdx");
            OCompositeKey key = new OCompositeKey(host, tagId);
            OIdentifiable oid = (OIdentifiable) tagHostIdIdx.get(key);
            if (oid != null) {
                ODocument doc = (ODocument)oid.getRecord();
                entityList = new ArrayList<String>();
                ORidBag entities = doc.field("in_HasTag");
                Iterator<OIdentifiable> iterator = entities.iterator();
                while (iterator.hasNext()) {
                    OIdentifiable identifiable = iterator.next();
                    entityList.add(identifiable.getIdentity().toString());
                }
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return entityList;
    }

}
