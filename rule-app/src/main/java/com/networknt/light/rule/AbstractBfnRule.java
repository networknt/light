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

import com.fasterxml.jackson.core.type.TypeReference;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.server.DbService;
import com.networknt.light.util.HashUtil;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OCompositeKey;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.OCommandSQL;
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
 * Created by steve on 28/12/14.
 * This the abstract class that implements functions for Blog, Forum and News as
 * they share similar traits. Since ids are generated and there is no need to check
 * uniqueness. Just make sure parent and children are checked and converted to ids.
 *
 */
public abstract class AbstractBfnRule  extends AbstractRule implements Rule {
    static final Logger logger = LoggerFactory.getLogger(AbstractBfnRule.class);

    public abstract boolean execute (Object ...objects) throws Exception;

    public boolean addBfn (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String bfnId = (String) data.get(bfnType + "Id");
        String host = (String) data.get("host");
        String id = bfnType + "Id";
        String error = null;
        String userHost = (String)user.get("host");
        if(userHost != null && !userHost.equals(host)) {
            error = "You can only add " + bfnType + " from host: " + host;
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
                ODocument bfn = getODocumentByHostId(graph, bfnType + "HostIdIdx", host, bfnId);
                if(bfn != null) {
                    error = "Id " + bfnId + " exists on host " + host;
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
                            in_Own.add(parent.getProperty(id));
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
                                        out_Own.add((String)child.getProperty(bfnType + "Id"));
                                    }
                                }
                            }
                            eventData.put("out_Own", out_Own);
                        }
                    }
                    if(error == null) {
                        eventMap.put(id, HashUtil.generateUUID());
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
            // update the bfn tree as one of bfn has changed.
            Map<String, Object> bfnMap = ServiceLocator.getInstance().getMemoryImage("bfnMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)bfnMap.get("treeCache");
            if(cache != null) {
                cache.remove(host + bfnType);
            }
            return true;
        }
    }

    public boolean addBfnEv (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        addBfnDb(bfnType, data);
        return true;
    }

    protected void addBfnDb(String bfnType, Map<String, Object> data) throws Exception {
        String className = bfnType.substring(0, 1).toUpperCase() + bfnType.substring(1);
        String host = (String)data.get("host");
        String id = bfnType + "Id";
        String index = className + "." + id;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            Vertex createUser = graph.getVertexByKey("User.userId", data.remove("createUserId"));
            List<String> parentIds = (List<String>)data.remove("in_Own");
            List<String> childrenIds = (List<String>)data.remove("out_Own");
            OrientVertex bfn = graph.addVertex("class:" + className, data);
            createUser.addEdge("Create", bfn);
            // parent
            if(parentIds != null && parentIds.size() == 1) {
                OrientVertex parent = getBfnByHostId(graph, bfnType, host, parentIds.get(0));
                if(parent != null) {
                    parent.addEdge("Own", bfn);
                }
            }
            // children
            if(childrenIds != null) {
                for(String childId: childrenIds) {
                    OrientVertex child = getBfnByHostId(graph, bfnType, host, childId);
                    if(child != null) {
                        bfn.addEdge("Own", child);
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

    public boolean delBfn (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String host = (String) data.get("host");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            String userHost = (String)user.get("host");
            if(userHost != null && !userHost.equals(host)) {
                error = "You can only delete " + bfnType + " from host: " + host;
                inputMap.put("responseCode", 403);
            } else {
                Vertex bfn = DbService.getVertexByRid(graph, rid);
                if(bfn != null) {
                    // Do no check if there are any children for the bfn. Just delete it. The edge
                    // will be deleted automatically and children can be linked to other bfn later.
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("host", host);
                    String id = bfnType + "Id";
                    eventData.put(id, bfn.getProperty(id));
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
            // update the bfn tree as one of bfn has changed.
            Map<String, Object> bfnMap = ServiceLocator.getInstance().getMemoryImage("bfnMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)bfnMap.get("treeCache");
            if(cache != null) {
                cache.remove(host + bfnType);
            }
            return true;
        }
    }

    public boolean delBfnEv (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        delBfnDb(bfnType, data);
        return true;
    }

    protected void delBfnDb(String bfnType, Map<String, Object> data) throws Exception {
        String className = bfnType.substring(0, 1).toUpperCase() + bfnType.substring(1);
        String id = bfnType + "Id";
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            OrientVertex bfn = getBfnByHostId(graph, bfnType, (String)data.get("host"), (String)data.get(id));
            if(bfn != null) {
                graph.removeVertex(bfn);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    public boolean updBfn (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String host = (String) data.get("host");
        String id = bfnType + "Id";
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            String userHost = (String)user.get("host");
            if(userHost != null && !userHost.equals(host)) {
                inputMap.put("result", "You can only update " + bfnType + " from host: " + host);
                inputMap.put("responseCode", 403);
                return false;
            } else {
                Vertex bfn = DbService.getVertexByRid(graph, rid);
                if(bfn != null) {
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
                            for (Vertex vertex : (Iterable<Vertex>) bfn.getVertices(Direction.IN, "Own")) {
                                // we only expect one parent here.
                                storedParentRid = vertex.getId().toString();
                                storedParentId = vertex.getProperty(id);
                            }
                            if(parentRids.get(0).equals(storedParentRid)) {
                                // same parent, do nothing
                            } else {
                                eventData.put("delParentId", storedParentId);
                                eventData.put("addParentId", parent.getProperty(id));
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
                                inputChildren.add((String)child.getProperty(id));
                            }
                        }
                        Set<String> storedChildren = new HashSet<String>();
                        for (Vertex vertex : (Iterable<Vertex>) bfn.getVertices(Direction.OUT, "Own")) {
                            storedChildren.add((String)vertex.getProperty(id));
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
        // update the bfn tree as one of bfn has changed.
        Map<String, Object> bfnMap = ServiceLocator.getInstance().getMemoryImage("bfnMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)bfnMap.get("treeCache");
        if(cache != null) {
            cache.remove(host + bfnType);
        }
        return true;
    }

    public boolean updBfnEv (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        updBfnDb(bfnType, data);
        return true;
    }

    private OrientVertex getBfnByHostId(OrientGraph graph, String bfnType, String host, String id) {
        OrientVertex bfn = null;
        OIndex<?> hostIdIdx = graph.getRawGraph().getMetadata().getIndexManager().getIndex(bfnType + "HostIdIdx");
        OCompositeKey key = new OCompositeKey(host, id);
        OIdentifiable oid = (OIdentifiable) hostIdIdx.get(key);
        if (oid != null) {
            bfn = graph.getVertex(oid.getRecord());
        }
        return bfn;
    }

    protected void updBfnDb(String bfnType, Map<String, Object> data) throws Exception {
        String className = bfnType.substring(0, 1).toUpperCase() + bfnType.substring(1);
        String id = bfnType + "Id";
        String host = (String)data.get("host");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            Vertex updateUser = graph.getVertexByKey("User.userId", data.remove("updateUserId"));
            OrientVertex bfn = getBfnByHostId(graph, bfnType, host, (String)data.get(id));
            if (bfn != null) {
                if(data.get("description") != null) {
                    bfn.setProperty("description", data.get("description"));
                } else {
                    bfn.removeProperty("description");
                }
                if(data.get("attributes") != null) {
                    bfn.setProperty("attributes", data.get("attributes"));
                } else {
                    bfn.removeProperty("attributes");
                }
                bfn.setProperty("updateDate", data.get("updateDate"));

                // parent
                String delParentId = (String)data.get("delParentId");
                if(delParentId != null) {
                    for (Edge edge : (Iterable<Edge>) bfn.getEdges(Direction.IN, "Own")) {
                        graph.removeEdge(edge);
                    }
                }
                String addParentId = (String)data.get("addParentId");
                if(addParentId != null) {
                    OrientVertex parent = getBfnByHostId(graph, bfnType, host, addParentId);
                    if (parent != null) {
                        parent.addEdge("Own", bfn);
                    }
                }

                // handle addChildren and delChildren
                Set<String> addChildren = (Set)data.get("addChildren");
                if(addChildren != null) {
                    for(String childId: addChildren) {
                        OrientVertex vertex = getBfnByHostId(graph, bfnType, host, childId);
                        bfn.addEdge("Own", vertex);
                    }
                }
                Set<String> delChildren = (Set)data.get("delChildren");
                if(delChildren != null) {
                    for(String childId: delChildren) {
                        OrientVertex vertex = getBfnByHostId(graph, bfnType, host, childId);
                        for (Edge edge : (Iterable<Edge>) bfn.getEdges(Direction.OUT, "Own")) {
                            if(edge.getVertex(Direction.IN).equals(vertex)) graph.removeEdge(edge);
                        }
                    }
                }
                // updateUser
                updateUser.addEdge("Update", bfn);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    public boolean downBfn (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String host = (String) data.get("host");
        String id = bfnType + "Id";
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex bfn = (OrientVertex)DbService.getVertexByRid(graph, rid);
            OrientVertex voteUser = (OrientVertex)graph.getVertexByKey("User.userId", user.get("userId"));
            if(bfn == null) {
                error = "@rid " + rid + " cannot be found";
                inputMap.put("responseCode", 404);
            } else {
                // TODO check if the current user has down voted the bfn before.
                boolean voted = false;
                for (Edge edge : voteUser.getEdges(bfn, Direction.OUT, "DownVote")) {
                    if(edge.getVertex(Direction.IN).equals(bfn)) voted = true;
                }
                if(voted) {
                    error = "You have down voted the " + bfnType + " already";
                    inputMap.put("responseCode", 400);
                } else {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("host", host);
                    eventData.put(id, bfn.getProperty(id));
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
            // update the bfn tree as one of bfn has changed.
            Map<String, Object> bfnMap = ServiceLocator.getInstance().getMemoryImage("bfnMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)bfnMap.get("treeCache");
            if(cache != null) {
                cache.remove(host + bfnType);
            }
            return true;
        }
    }

    public boolean downBfnEv (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        downBfnDb(bfnType, data);
        return true;
    }

    protected void downBfnDb(String bfnType, Map<String, Object> data) throws Exception {
        String className = bfnType.substring(0, 1).toUpperCase() + bfnType.substring(1);
        String id = bfnType + "Id";
        String index = className + "." + id;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            OrientVertex updateUser = (OrientVertex)graph.getVertexByKey("User.userId", data.remove("updateUserId"));
            OrientVertex bfn = (OrientVertex)graph.getVertexByKey(index, data.get(id));
            if(bfn != null && updateUser != null) {
                // remove UpVote edge if there is.
                for (Edge edge : updateUser.getEdges(bfn, Direction.OUT, "UpVote")) {
                    if(edge.getVertex(Direction.IN).equals(bfn)) graph.removeEdge(edge);
                }
                updateUser.addEdge("DownVote", bfn);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    public boolean upBfn (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String host = (String) data.get("host");
        String id = bfnType + "Id";
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex bfn = (OrientVertex)DbService.getVertexByRid(graph, rid);
            OrientVertex voteUser = (OrientVertex)graph.getVertexByKey("User.userId", user.get("userId"));
            if(bfn == null) {
                error = "@rid " + rid + " cannot be found";
                inputMap.put("responseCode", 404);
            } else {
                // TODO check if the current user has up voted the bfn before.
                boolean voted = false;
                for (Edge edge : voteUser.getEdges(bfn, Direction.OUT, "UpVote")) {
                    if(edge.getVertex(Direction.IN).equals(bfn)) voted = true;
                }
                if(voted) {
                    error = "You have up voted the " + bfnType + " already";
                    inputMap.put("responseCode", 400);
                } else {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("host", host);
                    eventData.put(id, bfn.getProperty(id));
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
            // update the bfn tree as one of bfn has changed.
            Map<String, Object> bfnMap = ServiceLocator.getInstance().getMemoryImage("bfnMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)bfnMap.get("treeCache");
            if(cache != null) {
                cache.remove(host + bfnType);
            }
            return true;
        }
    }

    public boolean upBfnEv (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        upBfnDb(bfnType, data);
        return true;
    }

    protected void upBfnDb(String bfnType, Map<String, Object> data) throws Exception {
        String className = bfnType.substring(0, 1).toUpperCase() + bfnType.substring(1);
        String id = bfnType + "Id";
        String index = className + "." + id;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            OrientVertex updateUser = (OrientVertex)graph.getVertexByKey("User.userId", data.remove("updateUserId"));
            OrientVertex bfn = (OrientVertex)graph.getVertexByKey(index, data.get(id));
            if(bfn != null && updateUser != null) {
                // remove DownVote edge if there is.
                for (Edge edge : updateUser.getEdges(bfn, Direction.OUT, "DownVote")) {
                    if(edge.getVertex(Direction.IN).equals(bfn)) graph.removeEdge(edge);
                }
                updateUser.addEdge("UpVote", bfn);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    public boolean addPost(String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String parentId = (String) data.get("parentId");
        String host = (String) data.get("host");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex parent = getBfnByHostId(graph, bfnType, host, parentId);
            if(parent == null) {
                error = "Id " + parentId + " doesn't exist on host " + host;
                inputMap.put("responseCode", 400);
            } else {
                Map<String, Object> user = (Map<String, Object>)payload.get("user");
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.putAll((Map<String, Object>) inputMap.get("data"));
                eventData.put("postId", HashUtil.generateUUID());
                eventData.put("createDate", new java.util.Date());
                eventData.put("createUserId", user.get("userId"));
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
            // update the bfn tree as the number of posts has changed.
            Map<String, Object> bfnMap = ServiceLocator.getInstance().getMemoryImage("bfnMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)bfnMap.get("treeCache");
            if(cache != null) {
                cache.remove(host + bfnType);
            }
            return true;
        }
    }

    public boolean addPostEv (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        addPostDb(bfnType, data);
        return true;
    }

    protected void addPostDb(String bfnType, Map<String, Object> data) throws Exception {
        String className = bfnType.substring(0, 1).toUpperCase() + bfnType.substring(1);
        String id = bfnType + "Id";
        String index = className + "." + id;
        String host = (String)data.get("host");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            Vertex createUser = graph.getVertexByKey("User.userId", data.remove("createUserId"));
            OrientVertex post = graph.addVertex("class:Post", data);
            createUser.addEdge("Create", post);
            // parent
            OrientVertex parent = getBfnByHostId(graph, bfnType, host, (String) data.get("parentId"));
            if(parent != null) {
                parent.addEdge("HasPost", post);
            }
            // tag
            Set<String> inputTags = data.get("tags") != null? new HashSet<String>(Arrays.asList(((String)data.get("tags")).split("\\s*,\\s*"))) : new HashSet<String>();
            for(String tagId: inputTags) {
                Vertex tag = null;
                // get the tag is it exists
                OIndex<?> tagHostIdIdx = graph.getRawGraph().getMetadata().getIndexManager().getIndex("tagHostIdIdx");
                logger.debug("tagHostIdIdx = " + tagHostIdIdx);
                OCompositeKey tagKey = new OCompositeKey(host, tagId);
                logger.debug("tagKey =" + tagKey);
                OIdentifiable tagOid = (OIdentifiable) tagHostIdIdx.get(tagKey);
                if (tagOid != null) {
                    tag = graph.getVertex(tagOid.getRecord());
                    post.addEdge("HasTag", tag);
                } else {
                    tag = graph.addVertex("class:Tag", "host", host, "tagId", tagId, "createDate", data.get("createDate"));
                    createUser.addEdge("Create", tag);
                    post.addEdge("HasTag", tag);
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

    public boolean delPost(String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String)data.get("@rid");
        String host = (String)data.get("host");
        String error = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex post = (OrientVertex)DbService.getVertexByRid(graph, rid);
            if(post != null) {
                // check if the post has comment, if yes, you cannot delete it for now
                // TODO fix it after orientdb 2.2 release.
                // https://github.com/orientechnologies/orientdb/issues/1108
                if(post.countEdges(Direction.OUT, "HasComment") > 0) {
                    error = "Post has comment(s), cannot be deleted";
                    inputMap.put("responseCode", 400);
                } else {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("postId", post.getProperty("postId"));
                }
            } else {
                error = "@rid " + rid + " cannot be found";
                inputMap.put("responseCode", 404);
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
            // update the bfn tree as the number of posts has changed.
            Map<String, Object> bfnMap = ServiceLocator.getInstance().getMemoryImage("bfnMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)bfnMap.get("treeCache");
            if(cache != null) {
                cache.remove(host + bfnType);
            }
            return true;
        }
    }

    public boolean delPostEv (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        delPostDb(bfnType, data);
        return true;
    }

    protected void delPostDb(String bfnType, Map<String, Object> data) throws Exception {
        String className = bfnType.substring(0, 1).toUpperCase() + bfnType.substring(1);
        String id = bfnType + "Id";
        String index = className + "." + id;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            OrientVertex post = (OrientVertex)graph.getVertexByKey("Post.postId", data.get("postId"));
            if(post != null) {
                // TODO cascade deleting all comments belong to the post.
                // Need to come up a query on that to get the entire tree.
                /*
                // https://github.com/orientechnologies/orientdb/issues/1108
                delete graph...
                */
                graph.removeVertex(post);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    public boolean updPost(String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String host = (String) data.get("host");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            // update post itself and we might have a new api to move post from one parent to another.
            Vertex post = DbService.getVertexByRid(graph, rid);
            if(post != null) {
                Map<String, Object> user = (Map<String, Object>)payload.get("user");
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.put("postId", post.getProperty("postId"));
                eventData.put("title", data.get("title"));
                eventData.put("source", data.get("source"));
                eventData.put("summary", data.get("summary"));
                eventData.put("content", data.get("content"));
                eventData.put("updateDate", new java.util.Date());
                eventData.put("updateUserId", user.get("userId"));
                // tags
                Set<String> inputTags = data.get("tags") != null? new HashSet<String>(Arrays.asList(((String)data.get("tags")).split("\\s*,\\s*"))) : new HashSet<String>();
                Set<String> storedTags = new HashSet<String>();
                for (Vertex vertex : (Iterable<Vertex>) post.getVertices(Direction.OUT, "HasTag")) {
                    storedTags.add((String)vertex.getProperty("tagId"));
                }

                Set<String> addTags = new HashSet<String>(inputTags);
                Set<String> delTags = new HashSet<String>(storedTags);
                addTags.removeAll(storedTags);
                delTags.removeAll(inputTags);

                if(addTags.size() > 0) eventData.put("addTags", addTags);
                if(delTags.size() > 0) eventData.put("delTags", delTags);
            } else {
                error = "@rid " + rid + " cannot be found";
                inputMap.put("responseCode", 404);
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
            // update the bfn tree as the last update time has changed.
            Map<String, Object> bfnMap = ServiceLocator.getInstance().getMemoryImage("bfnMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)bfnMap.get("treeCache");
            if(cache != null) {
                cache.remove(host + bfnType);
            }
            return true;
        }
    }

    public boolean updPostEv (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        updPostDb(bfnType, data);
        return true;
    }

    protected void updPostDb(String bfnType, Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            Vertex updateUser = graph.getVertexByKey("User.userId", data.remove("updateUserId"));
            OrientVertex post = (OrientVertex)graph.getVertexByKey("Post.postId", data.get("postId"));
            if(post != null) {
                updateUser.addEdge("Update", post);
                // fields
                if(data.get("title") != null) {
                    post.setProperty("title", data.get("title"));
                } else {
                    post.removeProperty("name");
                }
                if(data.get("source") != null) {
                    post.setProperty("source", data.get("source"));
                } else {
                    post.removeProperty("source");
                }
                if(data.get("summary") != null) {
                    post.setProperty("summary", data.get("summary"));
                } else {
                    post.removeProperty("summary");
                }
                if(data.get("content") != null) {
                    post.setProperty("content", data.get("content"));
                } else {
                    post.removeProperty("content");
                }
                post.setProperty("updateDate", data.get("updateDate"));

                // handle addTags and delTags
                OIndex<?> hostIdIdx = graph.getRawGraph().getMetadata().getIndexManager().getIndex("tagHostIdIdx");
                Set<String> addTags = (Set)data.get("addTags");
                if(addTags != null) {
                    for(String tagId: addTags) {
                        OCompositeKey key = new OCompositeKey(data.get("host"), tagId);
                        OIdentifiable oid = (OIdentifiable) hostIdIdx.get(key);
                        if (oid != null) {
                            OrientVertex tag = (OrientVertex)oid.getRecord();
                            post.addEdge("HasTag", tag);
                        } else {
                            Vertex tag = graph.addVertex("class:Tag", "host", data.get("host"), "tagId", tagId, "createDate", data.get("createDate"));
                            updateUser.addEdge("Create", tag);
                            post.addEdge("HasTag", tag);
                        }
                    }
                }
                Set<String> delTags = (Set)data.get("delTags");
                if(delTags != null) {
                    for(String tagId: delTags) {
                        OCompositeKey key = new OCompositeKey(data.get("host"), tagId);
                        OIdentifiable oid = (OIdentifiable) hostIdIdx.get(key);
                        if (oid != null) {
                            OrientVertex tag = (OrientVertex) oid.getRecord();
                            for (Edge edge : (Iterable<Edge>) post.getEdges(Direction.OUT, "HasTag")) {
                                if(edge.getVertex(Direction.IN).equals(tag)) graph.removeEdge(edge);
                            }
                        }
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

    public boolean getBfnTree(String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String host = (String)data.get("host");
        String json = null;
        Map<String, Object> bfnMap = ServiceLocator.getInstance().getMemoryImage("bfnMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)bfnMap.get("treeCache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            bfnMap.put("treeCache", cache);
        } else {
            json = (String)cache.get(host + bfnType);
        }
        if(json == null) {
            json = getBfnTreeDb(bfnType, host);
            cache.put(host + bfnType, json);
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

    protected String getBfnTreeDb(String bfnType, String host) {
        String json = null;
        String sql = "SELECT FROM " + bfnType + " WHERE host = ? and in_Own IS NULL ORDER BY id";
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> docs = graph.getRawGraph().command(query).execute(host);
            if(docs.size() > 0) {
                json = OJSONWriter.listToJSON(docs, "rid,fetchPlan:[*]in_Create:-2 [*]out_Create:-2 [*]in_Update:-2 [*]out_Update:-2 [*]out_Own:-1");
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return json;
    }

    public boolean getBfnPost(Object ...objects) throws Exception {
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
            sortedBy = "createDate";
        }
        boolean allowPost = false;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload != null) {
            Map<String,Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(roles.contains("owner")) {
                allowPost = true;
            } else if(roles.contains("admin") || roles.contains("blogAdmin") || roles.contains("blogUser")){
                if(host.equals(user.get("host"))) {
                    allowPost = true;
                }
            }
        }

        // TODO support the following lists: recent, popular, controversial
        // Get the page from cache.
        List<String> list = null;
        Map<String, Object> bfnMap = ServiceLocator.getInstance().getMemoryImage("bfnMap");
        ConcurrentMap<Object, Object> listCache = (ConcurrentMap<Object, Object>)bfnMap.get("listCache");
        if(listCache == null) {
            listCache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            bfnMap.put("listCache", listCache);
        } else {
            list = (List<String>)listCache.get(rid + sortedBy);
        }

        ConcurrentMap<Object, Object> postCache = (ConcurrentMap<Object, Object>)bfnMap.get("postCache");
        if(postCache == null) {
            postCache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            bfnMap.put("postCache", postCache);
        }

        if(list == null) {
            // get the list for db
            list = new ArrayList<String>();
            String json = getBfnPostDb(rid, sortedBy);
            if(json != null) {
                // convert json to list of maps.
                List<Map<String, Object>> posts = mapper.readValue(json,
                        new TypeReference<ArrayList<HashMap<String, Object>>>() {
                        });
                for(Map<String, Object> post: posts) {
                    String postRid = (String)post.get("rid");
                    list.add(postRid);
                    post.remove("@rid");
                    post.remove("@type");
                    post.remove("@version");
                    post.remove("@fieldTypes");
                    postCache.put(postRid, post);
                }
            }
            listCache.put(rid + sortedBy, list);
        }
        long total = list.size();
        if(total > 0) {
            List<Map<String, Object>> posts = new ArrayList<Map<String, Object>>();
            for(int i = pageSize*(pageNo - 1); i < Math.min(pageSize*pageNo, list.size()); i++) {
                String postRid = list.get(i);
                Map<String, Object> post = (Map<String, Object>)postCache.get(postRid);
                posts.add(post);
            }
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", total);
            result.put("posts", posts);
            result.put("allowPost", allowPost);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        } else {
            // there is no post available. but still need to return allowPost
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", 0);
            result.put("allowPost", allowPost);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        }
    }

    protected String getBfnPostDb(String rid, String sortedBy) {
        String json = null;
        // TODO there is a bug that prepared query only support one parameter. That is why sortedBy is concat into the sql.
        String sql = "select @rid, postId, title, content, createDate, parentId, in_Create[0].@rid as createRid, in_Create[0].userId as createUserId " +
                "from (traverse out_Own, out_HasPost from ?) where @class = 'Post' order by " + sortedBy + " desc";
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> posts = graph.getRawGraph().command(query).execute(rid);
            if(posts.size() > 0) {
                json = OJSONWriter.listToJSON(posts, null);
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return json;
    }

    public boolean getBfn(String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>) payload.get("user");
        String host = (String)data.get("host");
        Object userHost = user.get("host");
        if(userHost != null && !userHost.equals(host)) {
            inputMap.put("result", "You can only get " + bfnType + " from host: " + host);
            inputMap.put("responseCode", 403);
            return false;
        } else {
            String docs = getBfnDb(bfnType, host);
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

    protected String getBfnDb(String bfhType, String host) {
        String json = null;
        String sql = "SELECT FROM " + bfhType + " WHERE host = ? ORDER BY createDate";
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

    public boolean getBfnDropdown (String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        String host = (String)data.get("host");
        if(payload == null) {
            inputMap.put("result", "Login is required");
            inputMap.put("responseCode", 401);
            return false;
        } else {
            String docs = getBfnDropdownDb(bfnType, host);
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

    protected String getBfnDropdownDb(String bfnType, String host) {
        String json = null;
        String sql = "SELECT FROM " + bfnType + " WHERE host = ? ORDER BY id";
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> docs = graph.getRawGraph().command(query).execute(host);
            if(docs.size() > 0) {
                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                for(ODocument doc: docs) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("label", (String)doc.field(bfnType + "Id"));
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
