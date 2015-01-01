package com.networknt.light.rule.form;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 23/09/14.
 */
public abstract class AbstractFormRule extends AbstractRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractFormRule.class);

    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();

    public abstract boolean execute (Object ...objects) throws Exception;

    protected String getFormById(String id) {
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

    protected String addForm(Map<String, Object> data) throws Exception {
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            ODocument form = new ODocument(schema.getClass("Form"));
            if(data.get("host") != null) form.field("host", data.get("host"));
            form.field("id", data.get("id"));
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
        cache.put(data.get("id"), json);
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
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> userIdIdx = db.getMetadata().getIndexManager().getIndex("Form.id");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable oId = (OIdentifiable) userIdIdx.get(data.get("id"));
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
        cache.put(data.get("id"), json);
        return json;
    }

    protected String impForm(Map<String, Object> data) throws Exception {
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            OIndex<?> formIdIdx = db.getMetadata().getIndexManager().getIndex("Form.id");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable oid = (OIdentifiable) formIdIdx.get(data.get("id"));
            if (oid != null && oid.getRecord() != null) {
                oid.getRecord().delete();
            }

            ODocument form = new ODocument(schema.getClass("Form"));
            if(data.get("host") != null) form.field("host", data.get("host"));
            form.field("id", data.get("id"));
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
        cache.put(data.get("id"), json);
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

