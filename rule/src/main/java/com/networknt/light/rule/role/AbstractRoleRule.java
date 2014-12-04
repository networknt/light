package com.networknt.light.rule.role;

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
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by husteve on 10/31/2014.
 */
public abstract class AbstractRoleRule extends AbstractRule implements Rule {
    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();

    public abstract boolean execute (Object ...objects) throws Exception;

    protected String getRoleById(String id) {
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OIndex<?> roleIdIdx = db.getMetadata().getIndexManager().getIndex("Role.id");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable oid = (OIdentifiable) roleIdIdx.get(id);
            if (oid != null) {
                ODocument role = (ODocument)oid.getRecord();
                json = role.toJSON();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return json;
    }

    protected void addRole(Map<String, Object> data) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            ODocument role = new ODocument(schema.getClass("Role"));
            role.field("id", data.get("id"));
            if(data.get("host") != null) role.field("host", data.get("host"));
            role.field("desc", data.get("desc"));
            role.field("createDate", data.get("createDate"));
            role.field("createUserId", data.get("createUserId"));
            role.save();
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
    }

    protected void updRole(Map<String, Object> data) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> roleIdIdx = db.getMetadata().getIndexManager().getIndex("Role.id");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable oid = (OIdentifiable) roleIdIdx.get(data.get("id"));
            if (oid != null && oid.getRecord() != null) {
                ODocument role = oid.getRecord();

            }
            db.commit();

            ODocument role = db.load(new ORecordId((String)data.get("@rid")));
            if (role != null) {
                String host = (String)data.get("host");
                if(host != null && host.length() > 0) {
                    if(!host.equals(role.field("host"))) role.field("host", host);
                } else {
                    role.removeField("host");
                }
                String desc = (String)data.get("desc");
                if(desc != null && !desc.equals(role.field("desc"))) {
                    role.field("desc", desc);
                }
                role.field("updateDate", data.get("updateDate"));
                role.field("updateUserId", data.get("updateUserId"));
                role.save();
                db.commit();
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
    }

    protected void delRole(String id) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> roleIdIdx = db.getMetadata().getIndexManager().getIndex("Role.id");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable oid = (OIdentifiable) roleIdIdx.get(id);
            if (oid != null && oid.getRecord() != null) {
                oid.getRecord().delete();
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
    }

    protected String getRoles(String host) {
        String sql = "SELECT FROM Role";
        if(host != null) {
            sql = sql + " WHERE host = '" + host + "' OR host IS NULL";
        }
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
            List<ODocument> roles = db.command(query).execute();
            json = OJSONWriter.listToJSON(roles, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return json;
    }

    protected String getRoleDropdown(String host) {
        String sql = "SELECT FROM Role";
        if(host != null) {
            sql = sql + " WHERE host = '" + host + "' OR host IS NULL";
        }
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
            List<ODocument> roles = db.command(query).execute();
            if(roles.size() > 0) {
                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                for(ODocument doc: roles) {
                    Map<String, String> map = new HashMap<String, String>();
                    String id = doc.field("id");
                    map.put("label", id);
                    map.put("value", id);
                    list.add(map);
                }
                json = mapper.writeValueAsString(list);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // TODO throw e
        } finally {
            db.close();
        }
        return json;
    }
}
