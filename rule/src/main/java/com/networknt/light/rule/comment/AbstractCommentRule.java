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

package com.networknt.light.rule.comment;

import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 03/12/14.
 */
public abstract class AbstractCommentRule extends AbstractRule implements Rule {

    static final Logger logger = LoggerFactory.getLogger(AbstractCommentRule.class);

    public abstract boolean execute (Object ...objects) throws Exception;

    protected void addComment(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            Vertex createUser = graph.getVertexByKey("User.userId", data.remove("createUserId"));
            String parentId = (String)data.remove("parentId");
            String parentClassName = (String)data.remove("parentClassName");
            Vertex parent = null;
            if("Post".equals(parentClassName)) {
                parent = graph.getVertexByKey("Post.postId", parentId);
            } else {
                parent = graph.getVertexByKey("Comment.commentId", parentId);
            }
            OrientVertex comment = graph.addVertex("class:Comment", data);
            createUser.addEdge("Create", comment);
            parent.addEdge("HasComment", comment);
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    protected void delComment(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            String commentId = (String)data.get("commentId");
            OrientVertex comment = (OrientVertex)graph.getVertexByKey("Comment.commentId", commentId);
            graph.removeVertex(comment);
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    protected void updComment(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            String commentId = (String)data.get("commentId");
            OrientVertex comment = (OrientVertex)graph.getVertexByKey("Comment.commentId", commentId);
            if(comment != null) {
                comment.setProperty("content", data.get("content"));
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    protected long getTotal(Map<String, Object> data, Map<String, Object> criteria) {
        long total = 0;
        StringBuilder sb = new StringBuilder("SELECT COUNT(*) as count FROM (TRAVERSE children FROM ").append(data.get("@rid")).append(") ");
        String whereClause = DbService.getWhereClause(criteria);
        if(whereClause != null && whereClause.length() > 0) {
            sb.append(whereClause);
        }
        System.out.println("sql=" + sb);
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            total = ((ODocument)graph.getRawGraph().query(new OSQLSynchQuery<ODocument>(sb.toString())).get(0)).field("count");
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
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
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sb.toString());
            List<ODocument> list = graph.getRawGraph().command(query).execute();
            if(list.size() > 0) {
                json = OJSONWriter.listToJSON(list, null);
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return json;
    }

    protected String getCommentTree(Map<String, Object> data) {
        String json = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            ODocument record = graph.getVertex(data.get("@rid")).getRecord();
            json = record.toJSON("rid,fetchPlan:[*]in_Create:-2 out_HasComment:5");
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return json;
    }
}
