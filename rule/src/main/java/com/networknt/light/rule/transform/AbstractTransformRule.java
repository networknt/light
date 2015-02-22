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

package com.networknt.light.rule.transform;

import com.fasterxml.jackson.core.type.TypeReference;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OCompositeKey;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 16/02/15.
 */
public abstract class AbstractTransformRule extends AbstractRule implements Rule {
    static final Logger logger = LoggerFactory.getLogger(AbstractTransformRule.class);

    public abstract boolean execute (Object ...objects) throws Exception;

    protected void addTransformRequest(Map<String, Object> data) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            ODocument transform = new ODocument(schema.getClass("TransformRequest"));
            transform.field("ruleClass", data.get("ruleClass"));
            transform.field("sequence", data.get("sequence"));
            transform.field("transformRule", data.get("transformRule"));
            // transformData is a json string, convert it to map.
            Object transformData = data.get("transformData");
            if(transformData != null) {
                Map<String, Object> map = mapper.readValue((String)transformData,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                transform.field("transformData", map);
            }
            transform.field("createDate", data.get("createDate"));
            transform.field("createUserId", data.get("createUserId"));
            transform.save();
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        // remove the cached list if in order to reload it
        Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage("ruleMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)ruleMap.get("cache");
        if(cache != null) {
            cache.remove(data.get("ruleClass"));
        }
    }

    protected void addTransformResponse(Map<String, Object> data) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            ODocument transform = new ODocument(schema.getClass("TransformResponse"));
            transform.field("ruleClass", data.get("ruleClass"));
            transform.field("sequence", data.get("sequence"));
            transform.field("transformRule", data.get("transformRule"));
            // transformData is a json string, convert it to map.
            Object transformData = data.get("transformData");
            if(transformData != null) {
                Map<String, Object> map = mapper.readValue((String)transformData,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                transform.field("transformData", map);
            }
            transform.field("createDate", data.get("createDate"));
            transform.field("createUserId", data.get("createUserId"));
            transform.save();
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        // remove the cached list if in order to reload it
        Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage("ruleMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)ruleMap.get("cache");
        if(cache != null) {
            cache.remove(data.get("ruleClass"));
        }
    }

    protected void updTransformRequest(Map<String, Object> data) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> reqRuleSequenceIdx = db.getMetadata().getIndexManager().getIndex("ReqRuleSequenceIdx");
            OCompositeKey key = new OCompositeKey(data.get("ruleClass"), data.get("sequence"));
            OIdentifiable oid = (OIdentifiable) reqRuleSequenceIdx.get(key);
            if (oid != null) {
                ODocument transform = (ODocument) oid.getRecord();
                transform.field("transformRule", data.get("transformRule"));
                // transformData is a json string, convert it to map.
                Object transformData = data.get("transformData");
                if(transformData != null) {
                    Map<String, Object> map = mapper.readValue((String)transformData,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    transform.field("transformData", map);
                }
                transform.field("updateDate", data.get("updateDate"));
                transform.field("updateUserId", data.get("updateUserId"));
                transform.save();
                db.commit();
            }
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        // remove the cached list if in order to reload it
        Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage("ruleMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)ruleMap.get("cache");
        if(cache != null) {
            cache.remove(data.get("ruleClass"));
        }
    }

    protected void updTransformResponse(Map<String, Object> data) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> resRuleSequenceIdx = db.getMetadata().getIndexManager().getIndex("ResRuleSequenceIdx");
            OCompositeKey key = new OCompositeKey(data.get("ruleClass"), data.get("sequence"));
            OIdentifiable oid = (OIdentifiable) resRuleSequenceIdx.get(key);
            if (oid != null) {
                ODocument transform = (ODocument) oid.getRecord();
                transform.field("transformRule", data.get("transformRule"));
                // transformData is a json string, convert it to map.
                Object transformData = data.get("transformData");
                if(transformData != null) {
                    Map<String, Object> map = mapper.readValue((String)transformData,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    transform.field("transformData", map);
                }
                transform.field("updateDate", data.get("updateDate"));
                transform.field("updateUserId", data.get("updateUserId"));
                transform.save();
                db.commit();
            }
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        // remove the cached list if in order to reload it
        Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage("ruleMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)ruleMap.get("cache");
        if(cache != null) {
            cache.remove(data.get("ruleClass"));
        }
    }

    protected void delTransformRequest(Map<String, Object> data) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> reqRuleSequenceIdx = db.getMetadata().getIndexManager().getIndex("ReqRuleSequenceIdx");
            OCompositeKey key = new OCompositeKey(data.get("ruleClass"), data.get("sequence"));
            OIdentifiable oid = (OIdentifiable) reqRuleSequenceIdx.get(key);
            if (oid != null) {
                ODocument transform = (ODocument) oid.getRecord();
                transform.delete();
                transform.save();
                db.commit();
            }
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        // remove the cached list if in order to reload it
        Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage("ruleMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)ruleMap.get("cache");
        if(cache != null) {
            cache.remove(data.get("ruleClass"));
        }
    }

    protected void delTransformResponse(Map<String, Object> data) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> resRuleSequenceIdx = db.getMetadata().getIndexManager().getIndex("ResRuleSequenceIdx");
            OCompositeKey key = new OCompositeKey(data.get("ruleClass"), data.get("sequence"));
            OIdentifiable oid = (OIdentifiable) resRuleSequenceIdx.get(key);
            if (oid != null) {
                ODocument transform = (ODocument) oid.getRecord();
                transform.delete();
                transform.save();
                db.commit();
            }
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        // remove the cached list if in order to reload it
        Map<String, Object> ruleMap = ServiceLocator.getInstance().getMemoryImage("ruleMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)ruleMap.get("cache");
        if(cache != null) {
            cache.remove(data.get("ruleClass"));
        }
    }

    public List<Map<String, Object>> getTransformRequest(String ruleClass) {
        String sql = "SELECT FROM TransformRequest WHERE ruleClass = '" + ruleClass + "' ORDER BY sequence";
        List<Map<String, Object>> transforms = null;

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
                transforms = (List<Map<String, Object>>)rule.get("transformRequest");
            }
        }
        if(transforms == null) {
            ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
            try {
                OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
                List<ODocument> docs = db.command(query).execute();
                transforms = new ArrayList<Map<String, Object>> ();
                if(docs != null) {
                    for(ODocument doc: docs) {
                        Map<String, Object> map = new HashMap<String, Object> ();
                        map.put("sequence", doc.field("sequence"));
                        map.put("transformRule", doc.field("transformRule"));
                        map.put("transformData", doc.field("transformData"));
                        map.put("createUserId", doc.field("createUserId"));
                        transforms.add(map);
                    }
                }
                // put an empty list into the cache if no transform rules available. This can avoid access db every time the cache is hit.
                Map<String, Object> rule = (Map<String, Object>)cache.get(ruleClass);
                if(rule != null) {
                    rule.put("transformRequest", transforms);
                } else {
                    rule = new HashMap<String, Object>();
                    rule.put("transformRequest", transforms);
                    cache.put(ruleClass, rule);
                }
            } catch (Exception e) {
                logger.error("Exception:", e);
                throw e;
            } finally {
                db.close();
            }
        }
        return transforms;
    }

    public List<Map<String, Object>> getTransformResponse(String ruleClass) {
        String sql = "SELECT FROM TransformResponse WHERE ruleClass = '" + ruleClass + "' ORDER BY sequence";
        List<Map<String, Object>> transforms = null;

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
                transforms = (List<Map<String, Object>>)rule.get("transformResponse");
            }
        }
        if(transforms == null) {
            ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
            try {
                OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
                List<ODocument> docs = db.command(query).execute();
                transforms = new ArrayList<Map<String, Object>> ();
                if(docs != null) {
                    for(ODocument doc: docs) {
                        Map<String, Object> map = new HashMap<String, Object> ();
                        map.put("sequence", doc.field("sequence"));
                        map.put("transformRule", doc.field("transformRule"));
                        map.put("transformData", doc.field("transformData"));
                        map.put("createUserId", doc.field("createUserId"));
                        transforms.add(map);
                    }
                }
                // put an empty list into the cache if no transform rules available. This can avoid access db every time the cache is hit.
                Map<String, Object> rule = (Map<String, Object>)cache.get(ruleClass);
                if(rule != null) {
                    rule.put("transformResponse", transforms);
                } else {
                    rule = new HashMap<String, Object>();
                    rule.put("transformResponse", transforms);
                    cache.put(ruleClass, rule);
                }
            } catch (Exception e) {
                logger.error("Exception:", e);
                throw e;
            } finally {
                db.close();
            }
        }
        return transforms;
    }

    protected String getTransformRequestBySeq(String ruleClass, Integer sequence) {
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OIndex<?> reqRuleSequenceIdx = db.getMetadata().getIndexManager().getIndex("ReqRuleSequenceIdx");
            OCompositeKey key = new OCompositeKey(ruleClass, sequence);
            OIdentifiable oid = (OIdentifiable) reqRuleSequenceIdx.get(key);
            if (oid != null) {
                ODocument transform = (ODocument) oid.getRecord();
                json = transform.toJSON();
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        return json;
    }

    protected String getTransformResponseBySeq(String ruleClass, Integer sequence) {
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OIndex<?> resRuleSequenceIdx = db.getMetadata().getIndexManager().getIndex("ResRuleSequenceIdx");
            OCompositeKey key = new OCompositeKey(ruleClass, sequence);
            OIdentifiable oid = (OIdentifiable) resRuleSequenceIdx.get(key);
            if (oid != null) {
                ODocument transform = (ODocument) oid.getRecord();
                json = transform.toJSON();
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        return json;
    }

}
