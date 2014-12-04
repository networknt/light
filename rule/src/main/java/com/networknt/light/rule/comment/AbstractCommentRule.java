package com.networknt.light.rule.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OCompositeKey;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.*;

/**
 * Created by steve on 03/12/14.
 */
public abstract class AbstractCommentRule extends AbstractRule implements Rule {

    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();

    public abstract boolean execute (Object ...objects) throws Exception;

    protected ODocument addComment(Map<String, Object> data) throws Exception {
        ODocument comment = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            comment = new ODocument(schema.getClass("Comment"));
            comment.field("host", data.get("host"));
            comment.field("id", data.get("id"));
            comment.field("content", data.get("comment"));
            comment.field("createDate", data.get("createDate"));
            comment.field("createUserId", data.get("createUserId"));
            // parent
            OIndex<?> idIdx = db.getMetadata().getIndexManager().getIndex(data.get("parentClassName") + ".id");
            OIdentifiable oid = (OIdentifiable) idIdx.get(data.get("parentId"));
            if (oid != null) {
                ODocument parent = (ODocument) oid.getRecord();
                comment.field("parent", parent);
                List children = parent.field("children");
                if(children == null) {
                    children = new ArrayList<ODocument>();
                    children.add(comment);
                    parent.field("children", children);
                } else {
                    children.add(comment);
                }
                parent.save();
            }
            comment.save();
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return comment;
    }

    protected long getTotal(Map<String, Object> data, Map<String, Object> criteria) {
        long total = 0;
        StringBuilder sb = new StringBuilder("SELECT COUNT(*) as count FROM (TRAVERSE children FROM ").append(data.get("@rid")).append(") ");
        String whereClause = DbService.getWhereClause(criteria);
        if(whereClause != null && whereClause.length() > 0) {
            sb.append(whereClause);
        }

        System.out.println("sql=" + sb);
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            total = ((ODocument)db.query(new OSQLSynchQuery<ODocument>(sb.toString())).get(0)).field("count");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return total;
    }

    protected String getComment(Map<String, Object> data, Map<String, Object> criteria) {
        String json = null;
        StringBuilder sb = new StringBuilder("SELECT FROM (TRAVERSE children FROM ").append(data.get("@rid")).append(") ");
        String whereClause = DbService.getWhereClause(criteria);
        if(whereClause != null && whereClause.length() > 0) {
            sb.append(whereClause);
        }
        String sortedBy = (String)criteria.get("sortedBy");
        String sortDir = (String)criteria.get("sortDir");
        if(sortedBy != null) {
            sb.append(" ORDER BY ").append(sortedBy);
            if(sortDir != null) {
                sb.append(" ").append(sortDir);
            }
        }
        Integer pageSize = (Integer)criteria.get("pageSize");
        Integer pageNo = (Integer)criteria.get("pageNo");
        if(pageNo != null && pageSize != null) {
            sb.append(" SKIP ").append((pageNo - 1) * pageSize);
            sb.append(" LIMIT ").append(pageSize);
        }
        System.out.println("sql=" + sb);
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sb.toString());
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

    protected String getCommentTree(Map<String, Object> data) {
        String json = null;
        String sql = "SELECT FROM Comment WHERE parent = ? ORDER BY id DESC";
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> forums = db.command(query).execute(data.get("@rid"));
            if(forums.size() > 0) {
                json = OJSONWriter.listToJSON(forums, "fetchPlan:children:-1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return json;
    }

}
