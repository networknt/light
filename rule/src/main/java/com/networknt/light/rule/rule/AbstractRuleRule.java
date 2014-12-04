package com.networknt.light.rule.rule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/8/2014.
 */
public abstract class AbstractRuleRule implements Rule {

    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();

    public abstract boolean execute (Object ...objects) throws Exception;

    protected String getRuleByRuleClass(String ruleClass) {
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OIndex<?> ruleClassIdx = db.getMetadata().getIndexManager().getIndex("Rule.ruleClass");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable rule = (OIdentifiable) ruleClassIdx.get(ruleClass);
            if (rule != null && rule.getRecord() != null) {
                json = ((ODocument) rule.getRecord()).toJSON();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return json;
    }

    protected String addRule(Map<String, Object> data, String userRid, String userId) throws Exception {
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            ODocument rule = new ODocument(schema.getClass("Rule"));
            rule.field("ruleClass", data.get("ruleClass"));
            rule.field("host", data.get("host"));
            rule.field("sourceCode", data.get("sourceCode"));
            rule.field("createDate", new java.util.Date());
            rule.field("createUserId", userId);
            rule.field("createUserRid", userRid);
            rule.save();
            db.commit();
            json  = rule.toJSON();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return json;
    }

    protected void updRule(Map<String, Object> data, String userRid, String userId) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            ODocument rule = db.load(new ORecordId((String)data.get("@rid")));
            if (rule != null) {
                String host = (String)data.get("host");
                if(host != null && !host.equals(rule.field("host"))) {
                    rule.field("host", host);
                }
                String sourceCode = (String)data.get("sourceCode");
                if(sourceCode != null && !sourceCode.equals(rule.field("sourceCode"))) {
                    rule.field("sourceCode", sourceCode);
                }
                rule.field("updateDate", new java.util.Date());
                rule.field("updateUserRid", userRid);
                rule.field("updateUserId", userId);
                rule.save();
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

    protected void delRule(String rid) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            db.delete(new ORecordId(rid));
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
    }

    protected String getRules(String host) {
        String sql = "SELECT FROM Rule";
        if(host != null) {
            sql = sql + " WHERE host = '" + host;
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

}
