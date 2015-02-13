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

package com.networknt.light.rule.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.index.OCompositeKey;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by husteve on 10/14/2014.
 */
public abstract class AbstractCategoryRule extends AbstractRule implements Rule {
    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
    public abstract boolean execute (Object ...objects) throws Exception;

    protected ODocument getCategoryByHostId(String host, String id) {
        ODocument category = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OIndex<?> hostTitleIdx = db.getMetadata().getIndexManager().getIndex("hostIdIdx");
            // this is a unique index, so it retrieves a OIdentifiable
            OCompositeKey key = new OCompositeKey(host, id);
            OIdentifiable oid = (OIdentifiable) hostTitleIdx.get(key);
            if (oid != null) {
                category = (ODocument)oid.getRecord();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return category;
    }

    protected ODocument addCategory(Map<String, Object> data, String userId) throws Exception {
        ODocument category = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        Map<String, Object> categoryMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("categoryMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)categoryMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            categoryMap.put("cache", cache);
        }

        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            category = new ODocument(schema.getClass("Category"));
            category.field("host", data.get("host"));
            category.field("id", data.get("id"));
            category.field("desc", data.get("desc"));
            java.util.Date d = new java.util.Date();
            category.field("createDate", d);
            category.field("updateDate", d);
            category.field("createUser", userId);
            category.field("attributes", data.get("attributes")); // attributes should be a map.
            category.save();
            cache.put(category.field("@rid").toString(), category);
            String parentRid = (String)data.get("parentRid");
            if(parentRid != null) {
                // get the parent and update children list
                ODocument parent = (ODocument)cache.get("parentRid");
                if(parent == null) {
                    // not in cache
                    parent = db.load(new ORecordId(parentRid));
                    cache.put(parent.field("@rid").toString(), parent);
                }
                List children = parent.field("children");
                children.add(category);
                parent.save();
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return category;
    }

    protected void delCategory(String categoryRid) throws Exception {
        ODocument category = DbService.delODocumentByRid(categoryRid);
        // rebuild cache in memory.
        Map<String, Object> categoryMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("categoryMap");
        // update central cache
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)categoryMap.get("cache");
        if(cache != null) {
            cache.remove(categoryRid);
        }
    }


    protected void updCategory(String categoryRid, Map<String, Object> data) throws Exception {
        ODocument category = getCategoryByRid(categoryRid);
        if(category != null) {
            category.field("desc", data.get("desc"));
            category.field("attributes", data.get("attributes"));
            category.field("updateDate", new java.util.Date());
            category.save();
        }
    }

    protected void addChild(String categoryRid, String childRid) throws Exception {
        ODocument category = getCategoryByRid(categoryRid);
        if(category != null) {
            List children = category.field("children");
            children.add(new ORecordId(childRid));
            category.save();
        }
    }

    protected void delChild(String categoryRid, String childRid) throws Exception {
        ODocument category = getCategoryByRid(categoryRid);
        if(category != null) {
            List children = category.field("children");
            children.remove(new ORecordId(childRid));
            category.save();
        }
    }

    protected void addEntity(String categoryRid, String entityRid) throws Exception {
        ODocument category = getCategoryByRid(categoryRid);
        if(category != null) {
            List entities = category.field("entities");
            entities.add(new ORecordId(entityRid));
            category.save();
        }
    }

    protected void delEntity(String categoryRid, String entityRid) throws Exception {
        ODocument category = getCategoryByRid(categoryRid);
        if(category != null) {
            List entities = category.field("entities");
            entities.remove(new ORecordId(entityRid));
            category.save();
        }
    }

}
