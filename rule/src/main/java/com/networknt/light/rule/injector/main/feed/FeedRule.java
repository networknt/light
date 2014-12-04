package com.networknt.light.rule.injector.main.feed;

import com.cibc.rop.data.IDataFeed;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * Created by steve on 9/19/2014.
 *
 * Parent rule that provide some utilities to all the injection rules.
 */
public abstract class FeedRule implements Rule {
    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();

    public abstract boolean execute (Object ...objects) throws Exception;

    public String getRequestId() {
        // all environment share the same counter so that the feed can use it as index.
        long id = DbService.incrementCounter("injector.requestId");
        return "INJN" + id;
    }

    public Map<String, Object> addFeed(Map<String, Object> fields, Map<String, Object> user) throws Exception {
        Map<String, Object> map = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            ODocument doc = new ODocument(fields);
            doc.setClassName("Feed");
            doc.field("createDate", new java.util.Date());
            doc.field("createUserId", user.get("userId"));
            doc.save();
            db.commit();
            String json  = doc.toJSON();
            map = mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return map;
    }

    public void updFeedMeta(Map<String, Object> fields, Map<String, Object> user) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            db.begin();
            OIndex<?> requestIdIdx = db.getMetadata().getIndexManager().getIndex("Feed.requestId");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable oId = (OIdentifiable) requestIdIdx.get(fields.get("requestId"));
            if (oId != null) {
                ODocument doc = oId.getRecord();
                doc.field("processTypeCd", fields.get("processTypeCd"));
                doc.field("processSubtypeCd", fields.get("processSubtypeCd"));
                doc.field("updateUserId", user.get("userId"));
                doc.field("updateDate", new java.util.Date());
                doc.save();
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

    protected long getTotalNumberFeed(Map<String, Object> criteria) {
        long total = 0;
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) as count FROM Feed");

        String whereClause = DbService.getWhereClause(criteria);
        if(whereClause != null && whereClause.length() > 0) {
            sql.append(whereClause);
        }

        System.out.println("sql=" + sql);
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            total = ((ODocument)db.query(new OSQLSynchQuery<ODocument>(sql.toString())).get(0)).field("count");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return total;
    }

    protected List<String> getDataFeedTypes() {
        List<String> items = null;
        String sql = "SELECT DISTINCT(dataFeedType) FROM Feed ";
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
            List<ODocument> list = db.command(query).execute();
            if(list.size() > 0) {
                items = new ArrayList<String>();
                for(ODocument doc: list) {
                    items.add(doc.field("DISTINCT"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return items;
    }

    protected List<String> getProcessTypeCds() {
        List<String> items = null;
        String sql = "SELECT DISTINCT(processTypeCd) FROM Feed ";
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
            List<ODocument> list = db.command(query).execute();
            if(list.size() > 0) {
                items = new ArrayList<String>();
                for(ODocument doc: list) {
                    items.add(doc.field("DISTINCT"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return items;
    }

    protected List<String> getProcessSubtypeCds() {
        List<String> items = null;
        String sql = "SELECT DISTINCT(processSubtypeCd) FROM Feed ";
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql.toString());
            List<ODocument> list = db.command(query).execute();
            if(list.size() > 0) {
                items = new ArrayList<String> ();
                for(ODocument doc: list) {
                    items.add(doc.field("DISTINCT"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return items;
    }

    protected List<String> getCreateUserIds() {
        List<String> items = null;
        String sql = "SELECT DISTINCT(createUserId) FROM Feed ";
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql.toString());
            List<ODocument> list = db.command(query).execute();
            if(list.size() > 0) {
                items = new ArrayList<String> ();
                for(ODocument doc: list) {
                    items.add(doc.field("DISTINCT"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return items;
    }

    protected List<String> getUpdateUserIds() {
        List<String> items = null;
        String sql = "SELECT DISTINCT(updateUserId) FROM Feed ";
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql.toString());
            List<ODocument> list = db.command(query).execute();
            if(list.size() > 0) {
                items = new ArrayList<String> ();
                for(ODocument doc: list) {
                    items.add(doc.field("DISTINCT"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return items;
    }

    protected String getFeed(Map<String, Object> criteria) {
        String json = null;
        StringBuilder sql = new StringBuilder("SELECT FROM Feed ");
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
        System.out.println("sql=" + sql);
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql.toString());
            List<ODocument> list = db.command(query).execute();
            if(list.size() > 0) {
                json = OJSONWriter.listToJSON(list, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return json;
    }

    public Field findInheritedField(Class clazz, String fieldName) throws NoSuchFieldException {
        if (clazz == null)
            throw new NoSuchFieldException("No class");
        if (fieldName == null)
            throw new NoSuchFieldException("No field name");
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            System.out.println("No field name " + fieldName);
            return findInheritedField(clazz.getSuperclass(), fieldName);
        }
    }

    public Object getInputField(String value, Object type) throws ParseException{
        if (type.equals(Long.TYPE)) return new Long(value);
        else if (type.equals(Integer.TYPE)) return new Integer(value);
        else if (type.equals(Boolean.TYPE)) return new Boolean(value);
        else {
            if (type.equals(Date.class)){
                DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
                return format.parse(value);
            }
        }
        return value;
    }

    void sendToQueue(String env, IDataFeed dataFeed) throws Exception {
        Map<String, Object> injectorEnvMap = ServiceLocator.getInstance().getInjectorEnvMap();
        Map<String, Object> envMap = (Map<String, Object>)injectorEnvMap.get(env + dataFeed.getDataFeedType());
        try {
            Context context = new InitialContext(new Hashtable<>(envMap));

            QueueConnectionFactory queueConnectionFactory =
                    (QueueConnectionFactory)context.lookup((String)envMap.get("queueFactory"));
            QueueConnection conn = queueConnectionFactory.createQueueConnection();
            QueueSession session = conn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

            // Lookup the Queue and create the sender
            javax.jms.Queue queue = (javax.jms.Queue)context.lookup((String)envMap.get("queueName"));
            QueueSender sender = session.createSender(queue);
            // Create message and send it out
            ObjectMessage message = session.createObjectMessage();
            message.setObject((Serializable)dataFeed);
            sender.send(message);
            // Close down Session & Connection
            session.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
