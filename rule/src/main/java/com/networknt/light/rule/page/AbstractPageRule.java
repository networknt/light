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

package com.networknt.light.rule.page;

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
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by husteve on 10/24/2014.
 */
public abstract class AbstractPageRule extends AbstractRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractPageRule.class);

    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();

    public abstract boolean execute (Object ...objects) throws Exception;

    protected String getPageById(String id) {
        String json = null;
        Map<String, Object> pageMap = ServiceLocator.getInstance().getMemoryImage("pageMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)pageMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(100)
                    .build();
            pageMap.put("cache", cache);
        } else {
            json = (String)cache.get(id);
        }
        if(json == null) {
            ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
            try {
                OIndex<?> pageIdIdx = db.getMetadata().getIndexManager().getIndex("Page.id");
                // this is a unique index, so it retrieves a OIdentifiable
                OIdentifiable oid = (OIdentifiable) pageIdIdx.get(id);
                if (oid != null && oid.getRecord() != null) {
                    json = ((ODocument) oid.getRecord()).toJSON();
                    cache.put(id, json);
                }
            } catch (Exception e) {
                logger.error("Exception:", e);
            } finally {
                db.close();
            }
        }
        return json;
    }

    protected String addPage(Map<String, Object> data) throws Exception {
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            ODocument page = new ODocument(schema.getClass("Page"));
            page.field("host", data.get("host"));
            page.field("id", data.get("id"));
            page.field("content", data.get("content"));
            page.field("createDate", data.get("updateDate"));
            page.field("createUserId", data.get("updateUserId"));
            page.save();
            db.commit();
            json = page.toJSON();
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        Map<String, Object> pageMap = ServiceLocator.getInstance().getMemoryImage("pageMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)pageMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(100)
                    .build();
            pageMap.put("cache", cache);
        }
        cache.put(data.get("id"), json);
        return json;
    }

    protected void delPage(String id) {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> pageIdIdx = db.getMetadata().getIndexManager().getIndex("Page.id");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable oid = (OIdentifiable) pageIdIdx.get(id);
            if (oid != null && oid.getRecord() != null) {
                oid.getRecord().delete();
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
        } finally {
            db.close();
        }
        Map<String, Object> pageMap = ServiceLocator.getInstance().getMemoryImage("pageMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)pageMap.get("cache");
        if(cache != null) {
            cache.remove(id);
        }
    }

    protected String updPage(Map<String, Object> data) {
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> pageIdIdx = db.getMetadata().getIndexManager().getIndex("Page.id");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable oid = (OIdentifiable) pageIdIdx.get(data.get("id"));
            if (oid != null) {
                ODocument doc = oid.getRecord();
                doc.field("content", data.get("content"));
                doc.field("updateDate", data.get("updateDate"));
                doc.field("updateUserId", data.get("updateUserId"));
                doc.save();
                json = doc.toJSON();
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
        } finally {
            db.close();
        }
        Map<String, Object> pageMap = ServiceLocator.getInstance().getMemoryImage("pageMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)pageMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(100)
                    .build();
            pageMap.put("cache", cache);
        }
        cache.put(data.get("id"), json);
        return json;
    }

    protected String impPage(Map<String, Object> data) throws Exception {
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            OIndex<?> pageIdIdx = db.getMetadata().getIndexManager().getIndex("Page.id");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable oid = (OIdentifiable) pageIdIdx.get(data.get("id"));
            if (oid != null && oid.getRecord() != null) {
                oid.getRecord().delete();
            }
            ODocument page = new ODocument(schema.getClass("Page"));
            if(data.get("host") != null) page.field("host", data.get("host"));
            page.field("id", data.get("id"));
            page.field("content", data.get("content"));
            page.field("createDate", data.get("updateDate"));
            page.field("createUserId", data.get("updateUserId"));
            page.save();
            db.commit();
            json = page.toJSON();
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        Map<String, Object> pageMap = ServiceLocator.getInstance().getMemoryImage("pageMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)pageMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(100)
                    .build();
            pageMap.put("cache", cache);
        }
        cache.put(data.get("id"), json);
        return json;
    }

    protected List<ODocument> getAllPage(String host) {
        List<ODocument> pages = null;
        String sql = "SELECT FROM Page";
        if(host != null) {
            sql = sql + " WHERE host = '" + host + "' OR host IS NULL";
        }
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
            pages = db.command(query).execute();
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            db.close();
        }
        if(pages != null && pages.size() > 0) {
            Map<String, Object> pageMap = ServiceLocator.getInstance().getMemoryImage("pageMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)pageMap.get("cache");
            if(cache == null) {
                cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                        .maximumWeightedCapacity(100)
                        .build();
                pageMap.put("cache", cache);
            }
            for(ODocument page: pages) {
                cache.put(page.field("id"), page.toJSON());
            }
        }
        return pages;
    }

}
