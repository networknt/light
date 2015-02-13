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

package com.networknt.light.rule.form;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.RuleEngine;
import com.networknt.light.util.ServiceLocator;
import com.networknt.light.util.Util;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by steve on 23/09/14.
 */
public abstract class AbstractFormRule extends AbstractRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractFormRule.class);

    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();

    public abstract boolean execute (Object ...objects) throws Exception;

    protected String getFormById(Map<String, Object> inputMap) throws Exception {
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String id = (String)data.get("id");
        String json  = null;
        Map<String, Object> formMap = ServiceLocator.getInstance().getMemoryImage("formMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)formMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(100)
                    .build();
            formMap.put("cache", cache);
        } else {
            json = (String)cache.get(id);
        }
        if(json == null) {
            ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
            try {
                OIndex<?> formIdIdx = db.getMetadata().getIndexManager().getIndex("Form.id");
                // this is a unique index, so it retrieves a OIdentifiable
                OIdentifiable oid = (OIdentifiable) formIdIdx.get(id);
                if (oid != null && oid.getRecord() != null) {
                    json = ((ODocument) oid.getRecord()).toJSON();
                    if(id.endsWith("_d")) {
                        // enrich the form with dynamicOptions for drop down values
                        json = enrichForm(json, inputMap);
                    }
                    cache.put(id, json);
                }
            } catch (Exception e) {
                logger.error("Exception:", e);
                throw e;
            } finally {
                db.close();
            }
        }
        return json;
    }

    protected String enrichForm(String json, Map<String, Object> inputMap)  throws Exception {
    	Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Pattern pattern = Pattern.compile("\\[\\{\"label\":\"dynamic\",([^]]+)\\}\\]");
        Matcher m = pattern.matcher(json);
        StringBuffer sb = new StringBuffer(json.length());
        while (m.find()) {
            String text = m.group(1);
            // get the values from rules.
            logger.debug("text = {}", text);
            text = text.substring(8);
            logger.debug("text = {}", text);
            Map<String, Object> jsonMap = mapper.readValue(text,
                    new TypeReference<HashMap<String, Object>>() {});
            jsonMap.put("payload", inputMap.get("payload"));
            // inject host into data here.
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("host", data.get("host"));
            jsonMap.put("data", dataMap);
            RuleEngine.getInstance().executeRule(Util.getCommandRuleId(jsonMap), jsonMap);
            String result = (String)jsonMap.get("result");
            logger.debug("result = {}", result);
            if(result != null && result.length() > 0) {
                m.appendReplacement(sb, Matcher.quoteReplacement(result));
            } else {
                m.appendReplacement(sb, Matcher.quoteReplacement("[ ]"));
            }
        }
        m.appendTail(sb);
        logger.debug("form = {}", sb.toString());
        return sb.toString();
    }

    protected String addForm(Map<String, Object> data) throws Exception {
        String json = null;
        String id = (String)data.get("id");
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            ODocument form = new ODocument(schema.getClass("Form"));
            if(data.get("host") != null) form.field("host", data.get("host"));
            form.field("id", id);
            form.field("action", data.get("action"));
            form.field("schema", data.get("schema"));
            form.field("form", data.get("form"));
            if(data.get("modelData") != null) form.field("modelData", data.get("modelData"));
            form.field("createDate", data.get("createDate"));
            form.field("createUserId", data.get("createUserId"));
            form.save();
            db.commit();
            json = form.toJSON();
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        Map<String, Object> formMap = ServiceLocator.getInstance().getMemoryImage("formMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)formMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(100)
                    .build();
            formMap.put("cache", cache);
        }
        if(id.endsWith("_d")) {
            // remove it from the cache so that the next getForm will enrich the dynamic form
            cache.remove(id);
        } else {
            cache.put(id, json);
        }
        return json;
    }

    protected void delForm(String id) {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> formIdIdx = db.getMetadata().getIndexManager().getIndex("Form.id");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable oid = (OIdentifiable) formIdIdx.get(id);
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
        Map<String, Object> formMap = ServiceLocator.getInstance().getMemoryImage("formMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)formMap.get("cache");
        if(cache != null) {
            cache.remove(id);
        }
    }

    protected String updForm(Map<String, Object> data) {
        String json = null;
        String id = (String)data.get("id");
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex("Form.id");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable oId = (OIdentifiable) userIdIdx.get(id);
            if (oId != null) {
                ODocument doc = oId.getRecord();
                doc.field("action", data.get("action"));
                doc.field("schema", data.get("schema"));
                doc.field("form", data.get("form"));
                doc.field("modelData", data.get("modelData"));
                doc.field("updateDate", data.get("updateDate"));
                doc.field("updateUserId", data.get("updateUserId"));
                doc.save();
                db.commit();
                json = doc.toJSON();
            }
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
        } finally {
            db.close();
        }
        Map<String, Object> formMap = ServiceLocator.getInstance().getMemoryImage("formMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)formMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(100)
                    .build();
            formMap.put("cache", cache);
        }
        if(id.endsWith("_d")) {
            // remove it from the cache so that the next getForm will enrich the dynamic form
            cache.remove(id);
        } else {
            cache.put(id, json);
        }
        return json;
    }

    protected String impForm(Map<String, Object> data) throws Exception {
        String json = null;
        String id = (String)data.get("id");
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            OIndex<?> formIdIdx = db.getMetadata().getIndexManager().getIndex("Form.id");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable oid = (OIdentifiable) formIdIdx.get(id);
            if (oid != null && oid.getRecord() != null) {
                oid.getRecord().delete();
            }

            ODocument form = new ODocument(schema.getClass("Form"));
            if(data.get("host") != null) form.field("host", data.get("host"));
            form.field("id", id);
            form.field("action", data.get("action"));
            form.field("schema", data.get("schema"));
            form.field("form", data.get("form"));
            if(data.get("modelData") != null) form.field("modelData", data.get("modelData"));
            form.field("createDate", data.get("createDate"));
            form.field("createUserId", data.get("createUserId"));
            form.save();
            db.commit();
            json = form.toJSON();
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        Map<String, Object> formMap = ServiceLocator.getInstance().getMemoryImage("formMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)formMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(100)
                    .build();
            formMap.put("cache", cache);
        }
        if(id.endsWith("_d")) {
            // remove it from the cache so that the next getForm will enrich the dynamic form
            cache.remove(id);
        } else {
            cache.put(id, json);
        }
        return json;
    }

    protected String getAllForm(String host) {
        String sql = "SELECT FROM Form";
        if(host != null) {
            sql = sql + " WHERE host = '" + host + "' OR host IS NULL";
        }
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
            List<ODocument> forms = db.command(query).execute();
            if(forms != null && forms.size() > 0) {
                json = OJSONWriter.listToJSON(forms, null);
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
