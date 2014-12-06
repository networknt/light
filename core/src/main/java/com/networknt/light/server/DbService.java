package com.networknt.light.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.util.ServiceLocator;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by husteve on 8/25/2014.
 */
public class DbService {
    static final Logger logger = LoggerFactory.getLogger(DbService.class);

    public static void persistEvent(Map<String, Object> eventMap) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            ODocument event = new ODocument(schema.getClass("Event"));
            if(eventMap.get("host") != null) event.field("host", eventMap.get("host"));
            if(eventMap.get("app") != null) event.field("app", eventMap.get("app"));
            event.field("category", eventMap.get("category"));
            event.field("name", eventMap.get("name"));
            event.field("data", eventMap.get("data"));
            if(eventMap.get("createUserId") != null) event.field("createUserId", eventMap.get("createUserId"));
            event.field("createDate", eventMap.get("createDate"));
            event.save();
            db.commit();
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
    }

    public static long incrementCounter(String name) {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            List list = db.command(new OCommandSQL("update Counter INCREMENT value = 1 return after where name = '" + name + "'")).execute();
            ODocument lastDoc = (ODocument)list.get(0);
            return (Long) lastDoc.field("value");
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
    }

    public static String getWhereClause(Map<String, Object> criteria) {
        String wc = null;
        boolean firstFilter = true;
        if(criteria.size() > 0) {
            // get the filter
            Iterator<String> it = criteria.keySet().iterator();
            StringBuilder sb = new StringBuilder();
            while(it.hasNext()) {
                String key = it.next();
                if(("pageNo").equals(key) || "pageSize".equals(key) || ("sortDir").equals(key) || "sortedBy".equals(key)) {
                    continue;
                } else {
                    String filterColumn = key;
                    Object filterValue = criteria.get(key);
                    if(filterValue != null && filterValue.toString().length() > 0) {
                        if(firstFilter) {
                            sb.append(" WHERE ").append(filterColumn);
                            firstFilter = false;
                        } else {
                            sb.append(" AND ").append(filterColumn);
                        }
                        if(filterValue instanceof String) {
                            sb.append(" = '").append(filterValue).append("'");
                        } else {
                            sb.append(" = ").append(filterValue);
                        }
                    }
                    continue;
                }
            }
            wc = sb.toString();
        }
        return wc;
    }

    public static long getCount(String className, Map<String, Object> criteria) {
        long count = 0;
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) as count FROM ").append(className);
        String whereClause = DbService.getWhereClause(criteria);
        if(whereClause != null && whereClause.length() > 0) {
            sql.append(whereClause);
        }
        logger.debug("sql={}", sql);
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            count = ((ODocument)db.query(new OSQLSynchQuery<ODocument>(sql.toString())).get(0)).field("count");
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        return count;
    }

    public static String getData(String className, Map<String, Object> criteria) {
        String json = null;
        StringBuilder sql = new StringBuilder("SELECT FROM ").append(className);
        String whereClause = DbService.getWhereClause(criteria);
        if(whereClause != null && whereClause.length() > 0) {
            sql.append(whereClause);
        }
        String sortedBy = (String)criteria.get("sortedBy");
        String sortDir = (String)criteria.get("sortDir");
        if(sortedBy != null) {
            sql.append(" ORDER BY ").append(sortedBy);
            if(sortDir != null) {
                sql.append(" ").append(sortDir);
            }
        }
        Integer pageSize = (Integer)criteria.get("pageSize");
        Integer pageNo = (Integer)criteria.get("pageNo");
        if(pageNo != null && pageSize != null) {
            sql.append(" SKIP ").append((pageNo - 1) * pageSize);
            sql.append(" LIMIT ").append(pageSize);
        }
        logger.debug("sql={}", sql);
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql.toString());
            List<ODocument> list = db.command(query).execute();
            if(list.size() > 0) {
                json = OJSONWriter.listToJSON(list, null);
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        return json;
    }

    public static int executeSqlCommand(String sql) {
        int recordsUpdated = 0;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            recordsUpdated = db.command(new OCommandSQL(sql)).execute();
            db.commit();
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        return recordsUpdated;
    }

    public static ODocument getODocumentByRid(String rid) {
        ODocument doc = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            doc = db.load(new ORecordId(rid));
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        return doc;
    }

    public static String getJsonByRid(String rid) {
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            ODocument doc = db.load(new ORecordId(rid));
            if(doc != null) json = doc.toJSON();
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        return json;
    }

    public static ODocument delODocumentByRid(String rid) throws Exception {
        ODocument doc = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            doc = db.load(new ORecordId(rid));
            doc.delete();
            doc.save();
            db.commit();
        } catch (Exception e) {
            db.rollback();
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        return doc;
    }
    public static boolean hasReference(String rid, String classList) throws Exception {
        boolean hasReference = false;
        StringBuilder sql = new StringBuilder("find references ").append(rid);
        if(classList != null) {
            sql.append(" [").append(classList).append("]");
        }
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            List list = db.command(new OCommandSQL(sql.toString())).execute();
            if(list != null && list.size() > 0) {
                ODocument refer = (ODocument)list.get(0);
                Set referSet = refer.field("referredBy");
                if(referSet.size() > 0) {
                    hasReference = true;
                }
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            db.close();
        }
        return hasReference;
    }
}
