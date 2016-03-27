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

import com.fasterxml.jackson.core.type.TypeReference;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.MapFunction;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

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
                parent = graph.getVertexByKey("Post.entityId", parentId);
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

    protected void clearCommentCache(String entityRid) {
        Map<String, Object> categoryMap = ServiceLocator.getInstance().getMemoryImage("categoryMap");
        ConcurrentMap<Object, Object> commentCache = (ConcurrentMap<Object, Object>)categoryMap.get("commentCache");
        if(commentCache != null && commentCache.size() > 0) {
            commentCache.remove(entityRid + "createDate" + "desc");
            commentCache.remove(entityRid + "createDate" + "asc");
            commentCache.remove(entityRid + "rank" + "desc");
        }
    }

    protected void delComment(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            String commentId = (String)data.get("commentId");
            OrientVertex comment = (OrientVertex)graph.getVertexByKey("Comment.commentId", commentId);
            // remove the edge to this comment
            for (Edge edge : comment.getEdges(Direction.IN)) {
                graph.removeEdge(edge);
            }
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

    protected void spmComment(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            String commentId = (String)data.get("commentId");
            OrientVertex comment = (OrientVertex)graph.getVertexByKey("Comment.commentId", commentId);
            if(comment != null) {
                String userId = (String)data.get("userId");
                OrientVertex user = (OrientVertex)graph.getVertexByKey("User.userId", userId);
                if(user != null) {
                    // check if this user has reported spam for this comment.
                    boolean reported = false;
                    for (Edge edge : user.getEdges(comment, Direction.OUT, "ReportSpam")) {
                        if(edge.getVertex(Direction.IN).equals(comment)) {
                            reported = true;
                            graph.removeEdge(edge);
                        }
                    }
                    if(!reported) {
                        user.addEdge("ReportSpam", comment);
                    }
                }
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    protected void upComment(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            String commentId = (String)data.get("commentId");
            OrientVertex comment = (OrientVertex)graph.getVertexByKey("Comment.commentId", commentId);
            if(comment != null) {
                String userId = (String)data.get("userId");
                OrientVertex user = (OrientVertex)graph.getVertexByKey("User.userId", userId);
                Integer rank = comment.getProperty("rank");
                if(user != null) {
                    // check if this user has down vote for the comment, if yes, then remove it.
                    for (Edge edge : user.getEdges(comment, Direction.OUT, "DownVote")) {
                        if (edge.getVertex(Direction.IN).equals(comment)) {
                            graph.removeEdge(edge);
                            rank++;
                        }
                    }
                    // check if this user has up voted for this comment. if yes, then remove it.
                    boolean upVoted = false;
                    for (Edge edge : user.getEdges(comment, Direction.OUT, "UpVote")) {
                        if(edge.getVertex(Direction.IN).equals(comment)) {
                            upVoted = true;
                            graph.removeEdge(edge);
                            rank--;
                        }
                    }
                    if(!upVoted) {
                        user.addEdge("UpVote", comment);
                        rank++;
                    }
                }
                comment.setProperty("rank", rank);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    protected void downComment(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            String commentId = (String)data.get("commentId");
            OrientVertex comment = (OrientVertex)graph.getVertexByKey("Comment.commentId", commentId);
            if(comment != null) {
                String userId = (String)data.get("userId");
                OrientVertex user = (OrientVertex)graph.getVertexByKey("User.userId", userId);
                Integer rank = comment.getProperty("rank");
                if(user != null) {
                    // check if this user has up voted for the comment, if yes, then remove it.
                    for (Edge edge : user.getEdges(comment, Direction.OUT, "UpVote")) {
                        if (edge.getVertex(Direction.IN).equals(comment)) {
                            graph.removeEdge(edge);
                            rank--;
                        }
                    }
                    // check if this user has down voted for this comment. if yes, then remove it.
                    boolean downVoted = false;
                    for (Edge edge : user.getEdges(comment, Direction.OUT, "DownVote")) {
                        if(edge.getVertex(Direction.IN).equals(comment)) {
                            downVoted = true;
                            graph.removeEdge(edge);
                            rank++;
                        }
                    }
                    if(!downVoted) {
                        user.addEdge("DownVote", comment);
                        rank--;
                    }
                }
                comment.setProperty("rank", rank);
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
        StringBuilder sb = new StringBuilder("SELECT COUNT(*) as count FROM (TRAVERSE out_HasComment FROM ").append(data.get("@rid")).append(") ");
        String whereClause = DbService.getWhereClause(criteria);
        if(whereClause != null && whereClause.length() > 0) {
            sb.append(whereClause);
        }
        //System.out.println("sql=" + sb);
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
        StringBuilder sb = new StringBuilder("SELECT FROM (TRAVERSE out_HasComment FROM ").append(data.get("@rid")).append(") ");
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
        //System.out.println("sql=" + sb);
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

    protected boolean getCommentTree(Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String rid = (String)data.get("@rid");
        String host = (String)data.get("host");
        if(rid == null) {
            inputMap.put("result", "@rid is required");
            inputMap.put("responseCode", 400);
            return false;
        }
        Integer pageSize = (Integer)data.get("pageSize");
        Integer pageNo = (Integer)data.get("pageNo");
        if(pageSize == null) {
            inputMap.put("result", "pageSize is required");
            inputMap.put("responseCode", 400);
            return false;
        }
        if(pageNo == null) {
            inputMap.put("result", "pageNo is required");
            inputMap.put("responseCode", 400);
            return false;
        }
        String sortDir = (String)data.get("sortDir");
        String sortedBy = (String)data.get("sortedBy");
        if(sortDir == null) {
            sortDir = "desc";
        }
        if(sortedBy == null) {
            sortedBy = "rank";
        }
        boolean allowUpdate = isUpdateAllowed(rid, host, inputMap);

        // Get the comments from cache.
        String comments = null;
        Map<String, Object> categoryMap = ServiceLocator.getInstance().getMemoryImage("categoryMap");
        ConcurrentMap<Object, Object> commentCache = (ConcurrentMap<Object, Object>)categoryMap.get("commentCache");
        if(commentCache == null) {
            commentCache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(200)
                    .build();
            categoryMap.put("commentCache", commentCache);
        } else {
            comments = (String)commentCache.get(rid + sortedBy + sortDir);
        }
        if(comments == null) {
            // not in cache, get from db
            comments = getCommentTreeDb(rid, sortedBy, sortDir);
            if(comments != null) {
                commentCache.put(rid + sortedBy + sortDir, comments);
            }
        }

        List<Map<String, Object>> list = (comments == null) ? null :
                mapper.readValue(comments, new TypeReference<List<HashMap<String, Object>>>() {});

        if(list != null && list.size() > 0) {
            long total = list.size();
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", total);
            result.put("entities", list.subList(pageSize*(pageNo - 1), Math.min(pageSize*pageNo,list.size())));
            result.put("allowUpdate", allowUpdate);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        } else {
            // there is no post available. but still need to return allowPost
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", 0);
            result.put("allowUpdate", allowUpdate);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        }
    }

    protected boolean isUpdateAllowed(String rid, String host, Map<String, Object> inputMap) {
        boolean isAllowed = false;
        // get the parent class from rid.
        String parentClass = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex parent = (OrientVertex)DbService.getVertexByRid(graph, rid);
            if(parent != null) {
                parentClass = parent.getLabel().toLowerCase();
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }

        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        if(user != null) {
            List roles = (List)user.get("roles");
            if(roles.contains("owner")) {
                isAllowed = true;
            } else if(roles.contains("admin") || roles.contains(parentClass + "Admin") || roles.contains(parentClass + "User")) {
                if(host.equals(user.get("host"))) {
                    isAllowed = true;
                }
            }
        }
        return isAllowed;
    }

    protected String getCommentTreeDb(String rid, String sortedBy, String sortDir) {
        String sql = "select from Comment where in_HasComment[0] = " + rid + " ORDER BY " + sortedBy + " " + sortDir;
        String json = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> list = graph.getRawGraph().command(query).execute();
            if(list.size() > 0) {
                json = OJSONWriter.listToJSON(list, "rid,fetchPlan:[*]in_HasComment:-2 [*]out_ReportSpam:-2 [*]out_UpVote:-2 [*]out_DownVote:-2 in_Create[]:0 [*]out_Create:-2 [*]out_Update:-2 [*]out_HasComment:-1");
                // need to fixed the in_Create within the json using json path.
                DocumentContext dc = JsonPath.parse(json);
                MapFunction propsFunction = new StripPropsMapFunction();
                dc.map("$..in_UpVote[*]", propsFunction);
                dc.map("$..in_DownVote[*]", propsFunction);
                dc.map("$..in_ReportSpam[*]", propsFunction);

                MapFunction createFunction = new StripInCreateMapFunction();
                json = dc.map("$..in_Create[0]", createFunction).jsonString();
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return json;
    }

    private class StripPropsMapFunction implements MapFunction {
        @Override
        public Object map(Object currentValue, Configuration configuration) {
            String value = null;
            if(currentValue instanceof Map) {
                value = (String) ((Map) currentValue).get("@rid");
            } else if(currentValue instanceof String) {
                value = (String)currentValue;
            }
            return value;
        }
    }

    private class StripInCreateMapFunction implements MapFunction {
        Map<String, Object> userMap = new HashMap<String, Object>();
        @Override
        public Object map(Object currentValue, Configuration configuration) {
            if(currentValue instanceof Map) {
                ((Map) currentValue).remove("roles");
                ((Map) currentValue).remove("email");
                ((Map) currentValue).remove("credential");
                ((Map) currentValue).remove("createDate");
                ((Map) currentValue).remove("host");
                ((Map) currentValue).remove("out_Create");
                ((Map) currentValue).remove("out_Update");
                ((Map) currentValue).remove("out_ReportSpam");
                ((Map) currentValue).remove("out_UpVote");
                ((Map) currentValue).remove("out_DownVote");
                String rid = (String)((Map) currentValue).get("@rid");
                if(userMap.get(rid) == null) {
                    userMap.put(rid, currentValue);
                }
            } else if(currentValue instanceof String) {
                currentValue = userMap.get((String)currentValue);
            }
            return currentValue;
        }
    }
}
