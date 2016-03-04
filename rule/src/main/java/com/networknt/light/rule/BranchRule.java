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

package com.networknt.light.rule;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.server.DbService;
import com.networknt.light.util.HashUtil;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OCompositeKey;
import com.orientechnologies.orient.core.index.OIndex;
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

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 25/04/15.
 *
 * Moved from blog, news and forum as this can be share by other category.
 *
 */
public abstract class BranchRule extends AbstractRule implements Rule {

    static final Logger logger = LoggerFactory.getLogger(BranchRule.class);

    public abstract boolean execute (Object ...objects) throws Exception;

    public boolean addBranch (String branchType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        String categoryId = (String) data.get("categoryId");
        String host = (String) data.get("host");
        String error = null;
        String userHost = (String)user.get("host");
        if(userHost != null && !userHost.equals(host)) {
            error = "You can only add " + branchType + " from host: " + host;
            inputMap.put("responseCode", 403);
        } else {
            Map eventMap = getEventMap(inputMap);
            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
            inputMap.put("eventMap", eventMap);
            eventData.putAll((Map<String, Object>) inputMap.get("data"));
            eventData.put("createDate", new java.util.Date());
            eventData.put("createUserId", user.get("userId"));
            OrientGraph graph = ServiceLocator.getInstance().getGraph();
            try {
                ODocument branch = getODocumentByHostId(graph, branchType + "HostIdIdx", host, categoryId);
                if(branch != null) {
                    error = "categoryId " + categoryId + " exists on host " + host;
                    inputMap.put("responseCode", 400);
                } else {
                    // make sure parent exists if it is not empty.
                    List<String> parentRids = (List<String>)data.get("in_Own");
                    if(parentRids != null && parentRids.size() == 1) {
                        Vertex parent = DbService.getVertexByRid(graph, parentRids.get(0));
                        if(parent == null) {
                            error = "Parent with @rid " + parentRids.get(0) + " cannot be found.";
                            inputMap.put("responseCode", 404);
                        } else {
                            // convert parent from @rid to id
                            List in_Own = new ArrayList();
                            in_Own.add(parent.getProperty("categoryId"));
                            eventData.put("in_Own", in_Own);
                        }
                    }
                    if(error == null) {
                        // make sure all children exist if there are any.
                        // and make sure all children have empty parent.
                        List<String> childrenRids = (List<String>)data.get("out_Own");
                        if(childrenRids != null && childrenRids.size() > 0) {
                            List<String> out_Own = new ArrayList<String>();
                            for(String childRid: childrenRids) {
                                if(childRid != null) {
                                    if(parentRids!= null && childRid.equals(parentRids.get(0))) {
                                        error = "Parent shows up in the Children list";
                                        inputMap.put("responseCode", 400);
                                        break;
                                    }
                                    Vertex child = DbService.getVertexByRid(graph, childRid);
                                    if(child == null) {
                                        error = "Child with @rid " + childRid + " cannot be found.";
                                        inputMap.put("responseCode", 404);
                                        break;
                                    } else {
                                        out_Own.add(child.getProperty("categoryId"));
                                    }
                                }
                            }
                            eventData.put("out_Own", out_Own);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Exception:", e);
                throw e;
            } finally {
                graph.shutdown();
            }
        }
        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            // update the branch tree as one of branch has changed.
            Map<String, Object> branchMap = ServiceLocator.getInstance().getMemoryImage("branchMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)branchMap.get("treeCache");
            if(cache != null) {
                cache.remove(host + branchType);
            }
            return true;
        }
    }

    public boolean addBranchEv (String branchType, Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        addBranchDb(branchType, data);
        return true;
    }

    protected void addBranchDb(String branchType, Map<String, Object> data) throws Exception {
        String className = branchType.substring(0, 1).toUpperCase() + branchType.substring(1);
        String host = (String)data.get("host");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            Vertex createUser = graph.getVertexByKey("User.userId", data.remove("createUserId"));
            List<String> parentIds = (List<String>)data.remove("in_Own");
            List<String> childrenIds = (List<String>)data.remove("out_Own");
            OrientVertex branch = graph.addVertex("class:" + className, data);
            createUser.addEdge("Create", branch);
            // parent
            if(parentIds != null && parentIds.size() == 1) {
                OrientVertex parent = getBranchByHostId(graph, branchType, host, parentIds.get(0));
                if(parent != null) {
                    parent.addEdge("Own", branch);
                }
            }
            // children
            if(childrenIds != null) {
                for(String childId: childrenIds) {
                    OrientVertex child = getBranchByHostId(graph, branchType, host, childId);
                    if(child != null) {
                        branch.addEdge("Own", child);
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

    public boolean delBranch (String branchType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String host = (String) data.get("host");
        String error = null;
        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            String userHost = (String)user.get("host");
            if(userHost != null && !userHost.equals(host)) {
                error = "You can only delete " + branchType + " from host: " + host;
                inputMap.put("responseCode", 403);
            } else {
                Vertex branch = DbService.getVertexByRid(graph, rid);
                if(branch != null) {
                    // Do no check if there are any children for the branch. Just delete it. The edge
                    // will be deleted automatically and children can be linked to other branch later.
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("host", host);
                    eventData.put("categoryId", branch.getProperty("categoryId"));
                } else {
                    error = "@rid " + rid + " doesn't exist on host " + host;
                    inputMap.put("responseCode", 404);
                }
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            // update the branch tree as one of branch has changed.
            Map<String, Object> branchMap = ServiceLocator.getInstance().getMemoryImage("branchMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)branchMap.get("treeCache");
            if(cache != null) {
                cache.remove(host + branchType);
            }
            return true;
        }
    }

    public boolean delBranchEv (String branchType, Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        delBranchDb(branchType, data);
        return true;
    }

    protected void delBranchDb(String branchType, Map<String, Object> data) throws Exception {
        String className = branchType.substring(0, 1).toUpperCase() + branchType.substring(1);
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            OrientVertex branch = getBranchByHostId(graph, branchType, (String)data.get("host"), (String)data.get("categoryId"));
            if(branch != null) {
                graph.removeVertex(branch);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    public boolean updBranch (String branchType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String host = (String) data.get("host");
        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            String userHost = (String)user.get("host");
            if(userHost != null && !userHost.equals(host)) {
                inputMap.put("result", "You can only update " + branchType + " from host: " + host);
                inputMap.put("responseCode", 403);
                return false;
            } else {
                Vertex branch = DbService.getVertexByRid(graph, rid);
                if(branch != null) {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.putAll((Map<String, Object>)inputMap.get("data"));
                    eventData.put("updateDate", new java.util.Date());
                    eventData.put("updateUserId", user.get("userId"));

                    // make sure parent exists if it is not empty.
                    List parentRids = (List)data.get("in_Own");
                    if(parentRids != null) {
                        if(rid.equals(parentRids.get(0))) {
                            inputMap.put("result", "parent @rid is the same as current @rid");
                            inputMap.put("responseCode", 400);
                            return false;
                        }
                        Vertex parent = DbService.getVertexByRid(graph, (String)parentRids.get(0));
                        if(parent != null) {
                            String storedParentRid = null;
                            String storedParentId = null;
                            for (Vertex vertex : (Iterable<Vertex>) branch.getVertices(Direction.IN, "Own")) {
                                // we only expect one parent here.
                                storedParentRid = vertex.getId().toString();
                                storedParentId = vertex.getProperty("categoryId");
                            }
                            if(parentRids.get(0).equals(storedParentRid)) {
                                // same parent, do nothing
                            } else {
                                if(storedParentId != null) eventData.put("delParentId", storedParentId);
                                eventData.put("addParentId", parent.getProperty("categoryId"));
                            }
                        } else {
                            inputMap.put("result", "Parent with @rid " + parentRids.get(0) + " cannot be found");
                            inputMap.put("responseCode", 404);
                            return false;
                        }
                    }
                    // make sure all children exist if there are any.
                    // and make sure all children have empty parent.
                    List<String> childrenRids = (List<String>)data.get("out_Own");
                    if(childrenRids != null && childrenRids.size() > 0) {
                        List<String> childrenIds = new ArrayList<String>();
                        Set<String> inputChildren = new HashSet<String>();
                        for(String childRid: childrenRids) {
                            if(parentRids != null && childRid.equals(parentRids.get(0))) {
                                inputMap.put("result", "Parent shows up in the Children list");
                                inputMap.put("responseCode", 400);
                                return false;
                            }
                            if(childRid.equals(rid)) {
                                inputMap.put("result", "Current object shows up in the Children list");
                                inputMap.put("responseCode", 400);
                                return false;
                            }
                            Vertex child = DbService.getVertexByRid(graph, childRid);
                            if(child == null) {
                                inputMap.put("result", "Child with @rid " + childRid + " cannot be found");
                                inputMap.put("responseCode", 404);
                                return false;
                            } else {
                                inputChildren.add(child.getProperty("categoryId"));
                            }
                        }
                        Set<String> storedChildren = new HashSet<String>();
                        for (Vertex vertex : (Iterable<Vertex>) branch.getVertices(Direction.OUT, "Own")) {
                            storedChildren.add(vertex.getProperty("categoryId"));
                        }

                        Set<String> addChildren = new HashSet<String>(inputChildren);
                        Set<String> delChildren = new HashSet<String>(storedChildren);
                        addChildren.removeAll(storedChildren);
                        delChildren.removeAll(inputChildren);

                        if(addChildren.size() > 0) eventData.put("addChildren", addChildren);
                        if(delChildren.size() > 0) eventData.put("delChildren", delChildren);
                    }
                } else {
                    inputMap.put("result",  "@rid " + rid + " cannot be found");
                    inputMap.put("responseCode", 404);
                    return false;
                }
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        // update the branch tree as one of branch has changed.
        Map<String, Object> branchMap = ServiceLocator.getInstance().getMemoryImage("branchMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)branchMap.get("treeCache");
        if(cache != null) {
            cache.remove(host + branchType);
        }
        return true;
    }

    public boolean updBranchEv (String branchType, Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        updBranchDb(branchType, data);
        return true;
    }

    public OrientVertex getBranchByHostId(OrientGraph graph, String branchType, String host, String categoryId) {
        OrientVertex branch = null;
        OIndex<?> hostIdIdx = graph.getRawGraph().getMetadata().getIndexManager().getIndex(branchType + "HostIdIdx");
        OCompositeKey key = new OCompositeKey(host, categoryId);
        OIdentifiable oid = (OIdentifiable) hostIdIdx.get(key);
        if (oid != null) {
            branch = graph.getVertex(oid.getRecord());
        }
        return branch;
    }

    protected void updBranchDb(String branchType, Map<String, Object> data) throws Exception {
        String host = (String)data.get("host");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            Vertex updateUser = graph.getVertexByKey("User.userId", data.remove("updateUserId"));
            OrientVertex branch = getBranchByHostId(graph, branchType, host, (String)data.get("categoryId"));
            if (branch != null) {
                if(data.get("description") != null) {
                    branch.setProperty("description", data.get("description"));
                } else {
                    branch.removeProperty("description");
                }
                if(data.get("attributes") != null) {
                    branch.setProperty("attributes", data.get("attributes"));
                } else {
                    branch.removeProperty("attributes");
                }
                branch.setProperty("updateDate", data.get("updateDate"));

                // parent
                String delParentId = (String)data.get("delParentId");
                if(delParentId != null) {
                    for (Edge edge : (Iterable<Edge>) branch.getEdges(Direction.IN, "Own")) {
                        graph.removeEdge(edge);
                    }
                }
                String addParentId = (String)data.get("addParentId");
                if(addParentId != null) {
                    OrientVertex parent = getBranchByHostId(graph, branchType, host, addParentId);
                    if (parent != null) {
                        parent.addEdge("Own", branch);
                    }
                }

                // handle addChildren and delChildren
                Set<String> addChildren = (Set)data.get("addChildren");
                if(addChildren != null) {
                    for(String childId: addChildren) {
                        OrientVertex vertex = getBranchByHostId(graph, branchType, host, childId);
                        branch.addEdge("Own", vertex);
                    }
                }
                Set<String> delChildren = (Set)data.get("delChildren");
                if(delChildren != null) {
                    for(String childId: delChildren) {
                        OrientVertex vertex = getBranchByHostId(graph, branchType, host, childId);
                        for (Edge edge : (Iterable<Edge>) branch.getEdges(Direction.OUT, "Own")) {
                            if(edge.getVertex(Direction.IN).equals(vertex)) graph.removeEdge(edge);
                        }
                    }
                }
                // updateUser
                updateUser.addEdge("Update", branch);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    public boolean downBranch (String branchType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String host = (String) data.get("host");
        String error = null;
        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex branch = (OrientVertex)DbService.getVertexByRid(graph, rid);
            OrientVertex voteUser = (OrientVertex)graph.getVertexByKey("User.userId", user.get("userId"));
            if(branch == null) {
                error = "@rid " + rid + " cannot be found";
                inputMap.put("responseCode", 404);
            } else {
                // TODO check if the current user has down voted the branch before.
                boolean voted = false;
                for (Edge edge : voteUser.getEdges(branch, Direction.OUT, "DownVote")) {
                    if(edge.getVertex(Direction.IN).equals(branch)) voted = true;
                }
                if(voted) {
                    error = "You have down voted the " + branchType + " already";
                    inputMap.put("responseCode", 400);
                } else {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("host", host);
                    eventData.put("categoryId", branch.getProperty("categoryId"));
                    eventData.put("updateUserId", user.get("userId"));
                }
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            // update the branch tree as one of branch has changed.
            Map<String, Object> branchMap = ServiceLocator.getInstance().getMemoryImage("branchMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)branchMap.get("treeCache");
            if(cache != null) {
                cache.remove(host + branchType);
            }
            return true;
        }
    }

    public boolean downBranchEv (String branchType, Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        downBranchDb(branchType, data);
        return true;
    }

    protected void downBranchDb(String branchType, Map<String, Object> data) throws Exception {
        String className = branchType.substring(0, 1).toUpperCase() + branchType.substring(1);
        String index = className + ".categoryId";
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            OrientVertex updateUser = (OrientVertex)graph.getVertexByKey("User.userId", data.remove("updateUserId"));
            OrientVertex branch = (OrientVertex)graph.getVertexByKey(index, data.get("categoryId"));
            if(branch != null && updateUser != null) {
                // remove UpVote edge if there is.
                for (Edge edge : updateUser.getEdges(branch, Direction.OUT, "UpVote")) {
                    if(edge.getVertex(Direction.IN).equals(branch)) graph.removeEdge(edge);
                }
                updateUser.addEdge("DownVote", branch);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    public boolean upBranch (String branchType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String host = (String) data.get("host");
        String error = null;
        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex branch = (OrientVertex)DbService.getVertexByRid(graph, rid);
            OrientVertex voteUser = (OrientVertex)graph.getVertexByKey("User.userId", user.get("userId"));
            if(branch == null) {
                error = "@rid " + rid + " cannot be found";
                inputMap.put("responseCode", 404);
            } else {
                // TODO check if the current user has up voted the branch before.
                boolean voted = false;
                for (Edge edge : voteUser.getEdges(branch, Direction.OUT, "UpVote")) {
                    if(edge.getVertex(Direction.IN).equals(branch)) voted = true;
                }
                if(voted) {
                    error = "You have up voted the " + branchType + " already";
                    inputMap.put("responseCode", 400);
                } else {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("host", host);
                    eventData.put("categoryId", branch.getProperty("categoryId"));
                    eventData.put("updateUserId", user.get("userId"));
                }
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            // update the branch tree as one of branch has changed.
            Map<String, Object> branchMap = ServiceLocator.getInstance().getMemoryImage("branchMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)branchMap.get("treeCache");
            if(cache != null) {
                cache.remove(host + branchType);
            }
            return true;
        }
    }

    public boolean upBranchEv (String branchType, Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        upBranchDb(branchType, data);
        return true;
    }

    protected void upBranchDb(String branchType, Map<String, Object> data) throws Exception {
        String className = branchType.substring(0, 1).toUpperCase() + branchType.substring(1);
        String index = className + ".categoryId";
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            OrientVertex updateUser = (OrientVertex)graph.getVertexByKey("User.userId", data.remove("updateUserId"));
            OrientVertex branch = (OrientVertex)graph.getVertexByKey(index, data.get("categoryId"));
            if(branch != null && updateUser != null) {
                // remove DownVote edge if there is.
                for (Edge edge : updateUser.getEdges(branch, Direction.OUT, "DownVote")) {
                    if(edge.getVertex(Direction.IN).equals(branch)) graph.removeEdge(edge);
                }
                updateUser.addEdge("UpVote", branch);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    /**
     * Only this method needs to be cached as it is called by the site home rendering. Other get methods
     * are for admin only and no need to be cached.
     * @param branchType
     * @param objects
     * @return
     * @throws Exception
     */
    public boolean getBranchTree(String branchType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String host = (String)data.get("host");
        String json = null;
        Map<String, Object> branchMap = ServiceLocator.getInstance().getMemoryImage("branchMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)branchMap.get("treeCache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(100)
                    .build();
            branchMap.put("treeCache", cache);
        } else {
            json = (String)cache.get(host + branchType);
        }
        if(json == null) {
            json = getBranchTreeDb(branchType, host);
            if(json != null) cache.put(host + branchType, json);
        }
        if(json != null) {
            inputMap.put("result", json);
            return true;
        } else {
            inputMap.put("result", "No document can be found");
            inputMap.put("responseCode", 404);
            return false;
        }
    }

    protected String getBranchTreeDb(String branchType, String host) {
        String json = null;
        String sql = "SELECT FROM " + branchType + " WHERE host = ? and in_Own IS NULL ORDER BY id";
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> docs = graph.getRawGraph().command(query).execute(host);
            if(docs.size() > 0) {
                json = OJSONWriter.listToJSON(docs, "rid,fetchPlan:[*]in_Create:-2 [*]out_Create:-2 [*]in_Update:-2 [*]out_Update:-2 [*]in_HasPost:-2 [*]out_HasPost:-2 [*]out_Own:5");
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return json;
    }

    public boolean getBranch(String branchType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        String host = (String)data.get("host");
        Object userHost = user.get("host");
        if(userHost != null && !userHost.equals(host)) {
            inputMap.put("result", "You can only get " + branchType + " from host: " + host);
            inputMap.put("responseCode", 403);
            return false;
        } else {
            String docs = getBranchDb(branchType, host);
            if(docs != null) {
                inputMap.put("result", docs);
                return true;
            } else {
                inputMap.put("result", "No document can be found");
                inputMap.put("responseCode", 404);
                return false;
            }
        }
    }

    protected String getBranchDb(String branchType, String host) {
        String json = null;
        String sql = "SELECT FROM " + branchType + " WHERE host = ? ORDER BY createDate";
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> docs = graph.getRawGraph().command(query).execute(host);
            if(docs.size() > 0) {
                json = OJSONWriter.listToJSON(docs, null);
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return json;
    }

    public boolean getBranchDropdown (String branchType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String host = (String)data.get("host");
        String docs = getBranchDropdownDb(branchType, host);
        if(docs != null) {
            inputMap.put("result", docs);
            return true;
        } else {
            inputMap.put("result", "No document can be found");
            inputMap.put("responseCode", 404);
            return false;
        }
    }

    protected String getBranchDropdownDb(String branchType, String host) {
        String json = null;
        String sql = "SELECT FROM " + branchType + " WHERE host = ? ORDER BY categoryId";
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> docs = graph.getRawGraph().command(query).execute(host);
            if(docs.size() > 0) {
                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                for(ODocument doc: docs) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("label", (String)doc.field("categoryId"));
                    map.put("value", doc.field("@rid").toString());
                    list.add(map);
                }
                json = mapper.writeValueAsString(list);
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return json;
    }
}
