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
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientElementIterable;
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
public abstract class AbstractBfnRule extends BranchRule implements Rule {
    static final Logger logger = LoggerFactory.getLogger(AbstractBfnRule.class);

    public abstract boolean execute (Object ...objects) throws Exception;

    public boolean addPost(String bfnType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String parentRid = (String) data.remove("parentRid");
        String host = (String) data.get("host");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            Vertex parent = DbService.getVertexByRid(graph, parentRid);
            if(parent == null) {
                error = "Rid " + parentRid + " doesn't exist on host " + host;
                inputMap.put("responseCode", 400);
            } else {
                Map<String, Object> user = (Map<String, Object>)payload.get("user");
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.putAll((Map<String, Object>) inputMap.get("data"));
                eventData.put("parentId", parent.getProperty("categoryId"));
                eventData.put("postId", HashUtil.generateUUID());
                eventData.put("createDate", new Date());
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
            clearCache(host, bfnType, parentRid);
            return true;
        }
    }

    private void clearCache(String host, String bfnType, String parentRid) {
        Map<String, Object> categoryMap = ServiceLocator.getInstance().getMemoryImage("categoryMap");
        ConcurrentMap<Object, Object> listCache = (ConcurrentMap<Object, Object>)categoryMap.get("listCache");
        if(listCache != null && listCache.size() > 0) {
            // clear recent list (recent list regardless category
            listCache.remove(host + bfnType);
            // clear newestList for this parentRid only this category
            listCache.remove(parentRid + "createDate" + "desc");
            // TODO handler future list here.
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
        String host = (String)data.get("host");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            Vertex createUser = graph.getVertexByKey("User.userId", data.remove("createUserId"));
            List<String> tags = (List<String>)data.remove("tags");
            
            OrientVertex post = graph.addVertex("class:Post", data);
            createUser.addEdge("Create", post);
            // parent
            OrientVertex parent = getBranchByHostId(graph, bfnType, host, (String) data.get("parentId"));
            if(parent != null) {
                parent.addEdge("HasPost", post);
            }
            // tag
            if(tags != null && tags.size() > 0) {
                for(String tagId: tags) {
                    Vertex tag = null;
                    // get the tag is it exists
                    OIndex<?> tagHostIdIdx = graph.getRawGraph().getMetadata().getIndexManager().getIndex("tagHostIdIdx");
                    OCompositeKey tagKey = new OCompositeKey(host, tagId);
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
        String parentRid = null;
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
                    // now get the parentRid in order to remove the cache.
                    for(Edge e: post.getEdges(Direction.IN, "HasPost")) {
                        Vertex edgeParent = e.getVertex(Direction.OUT);
                        parentRid = edgeParent.getId().toString();
                    }
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
            clearCache(host, bfnType, parentRid);
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
                // TODO remove tags edge. Do I need to?
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
        String rid = (String) data.get("rid");
        String originalParentRid = null;
        String parentRid = null;
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
                eventData.put("host", data.get("host"));
                eventData.put("postId", post.getProperty("postId"));
                eventData.put("title", data.get("title"));
                eventData.put("source", data.get("source"));
                eventData.put("summary", data.get("summary"));
                eventData.put("content", data.get("content"));
                eventData.put("updateDate", new Date());
                eventData.put("updateUserId", user.get("userId"));
                // it is possible for the post to switch parent.
                parentRid = (String)data.get("parentRid");
                if(parentRid != null) {
                    // if parentRid is not even selected, nothing to do with the parent edge.
                    Vertex parent = DbService.getVertexByRid(graph, parentRid);
                    if(parent != null) {
                        boolean found = false;
                        for(Edge e: post.getEdges(Direction.IN, "HasPost")) {
                            Vertex edgeParent = e.getVertex(Direction.OUT);
                            if(edgeParent != null) {
                                originalParentRid =  edgeParent.getId().toString();
                                if(originalParentRid.equals(parentRid)) {
                                    found = true;
                                    break;
                                }

                            }
                        }
                        if(!found) {
                            // replace parent here by passing parentId to the event rule.
                            eventData.put("parentId", parent.getProperty("categoryId"));
                        }
                    }
                } else {
                    // get originalParentRid from post for clearing cache.
                    for(Edge e: post.getEdges(Direction.IN, "HasPost")) {
                        Vertex edgeParent = e.getVertex(Direction.OUT);
                        if(edgeParent != null) {
                            originalParentRid =  edgeParent.getId().toString();
                        }
                    }
                }

                // tags
                List<String> tags = (List)data.get("tags");
                if(tags == null || tags.size() == 0) {
                    // remove all existing tags
                    Set<String> delTags = new HashSet<String>();
                    for (Vertex vertex : (Iterable<Vertex>) post.getVertices(Direction.OUT, "HasTag")) {
                        delTags.add((String)vertex.getProperty("tagId"));
                    }
                    if(delTags.size() > 0) eventData.put("delTags", delTags);
                } else {
                    Set<String> inputTags = new HashSet<String>(tags);
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
            clearCache(host, bfnType, originalParentRid);
            if(parentRid != null) clearCache(host, bfnType, parentRid);
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

                // handle parent update
                String parentId = (String)data.get("parentId");
                if(parentId != null) {
                    OrientVertex parent = getBranchByHostId(graph, bfnType, (String) data.get("host"), (String) data.get("parentId"));
                    if(parent != null) {
                        // remove the current edge and add a new one.
                        for(Edge e : post.getEdges(Direction.IN, "HasPost")) {
                            e.remove();
                        }
                        parent.addEdge("HasPost", post);
                    }
                }

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
                            Vertex tag = graph.addVertex("class:Tag", "host", data.get("host"), "tagId", tagId, "createDate", data.get("updateDate"));
                            updateUser.addEdge("Create", tag);
                            post.addEdge("HasTag", tag);
                        }
                    }
                }
                Set<String> delTags = (Set)data.get("delTags");
                if(delTags != null) {
                    for(String tagId: delTags) {
                        /*
                        OrientVertex branch = null;
                        OIndex<?> hostIdIdx = graph.getRawGraph().getMetadata().getIndexManager().getIndex(branchType + "HostIdIdx");
                        OCompositeKey key = new OCompositeKey(host, id);
                        OIdentifiable oid = (OIdentifiable) hostIdIdx.get(key);
                        if (oid != null) {
                            branch = graph.getVertex(oid.getRecord());
                        }
                        return branch;
                        */

                        OCompositeKey key = new OCompositeKey(data.get("host"), tagId);
                        OIdentifiable oid = (OIdentifiable) hostIdIdx.get(key);
                        if (oid != null) {
                            OrientVertex tag = graph.getVertex(oid.getRecord());
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

    public boolean getCategoryEntity(String categoryType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String categoryRid = (String)data.get("@rid");
        String host = (String)data.get("host");
        if(categoryRid == null) {
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
        boolean allowUpdate = isUpdateAllowed(categoryType, host, inputMap);

        // TODO support the following lists: recent, popular, controversial
        // Get the page from cache.
        List<String> list = getCategoryEntityList(categoryRid, sortedBy, sortDir);
        if(list != null && list.size() > 0) {
            long total = list.size();
            List<Map<String, Object>> entities = new ArrayList<Map<String, Object>>();
            for(int i = pageSize*(pageNo - 1); i < Math.min(pageSize*pageNo, list.size()); i++) {
                String entityRid = list.get(i);
                Map<String, Object> entity = getCategoryEntity(entityRid);
                if(entity != null) {
                    entities.add(entity);
                } else {
                    logger.warn("Could not find entity {} from List {}", entityRid, categoryRid + sortedBy + sortDir);
                }
            }
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", total);
            result.put("entities", entities);
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

    protected boolean isUpdateAllowed(String categoryType, String host, Map<String, Object> inputMap) {
        boolean isAllowed = false;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload != null) {
            Map<String,Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(roles.contains("owner")) {
                isAllowed = true;
            } else if(roles.contains("admin") || roles.contains(categoryType + "Admin") || roles.contains(categoryType + "User")) {
                if(host.equals(user.get("host"))) {
                    isAllowed = true;
                }
            }
        }
        return isAllowed;
    }

    protected List<String> getCategoryEntityList(String categoryRid, String sortedBy, String sortDir) throws Exception {
        List<String> list = null;
        // get list from cache
        Map<String, Object> categoryMap = ServiceLocator.getInstance().getMemoryImage("categoryMap");
        ConcurrentMap<Object, Object> listCache = (ConcurrentMap<Object, Object>)categoryMap.get("listCache");
        if(listCache == null) {
            listCache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(200)
                    .build();
            categoryMap.put("listCache", listCache);
        } else {
            list = (List<String>)listCache.get(categoryRid + sortedBy + sortDir);
        }
        if(list == null) {
            // not in cache, get from db
            list = getCategoryEntityListDb(categoryRid, sortedBy, sortDir);
            if(list != null) {
                listCache.put(categoryRid + sortedBy + sortDir, list);
            }
        }
        return list;
    }

    protected List<String> getCategoryEntityListDb(String categoryRid, String sortedBy, String sortDir) {
        List<String> entityList = null;
        String sql = "select @rid from (traverse out_Own, out_HasPost from ?) where @class = 'Post' order by " + sortedBy + " " + sortDir;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> entities = graph.getRawGraph().command(query).execute(categoryRid);
            if(entities.size() > 0) {
                entityList = new ArrayList<String>();
                for(ODocument entity: entities) {
                    entityList.add(((ODocument)entity.field("rid")).field("@rid").toString());
                }
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return entityList;
    }

    public Map<String, Object> getCategoryEntity(String entityRid) {
        Map<String, Object> entity = null;
        Map<String, Object> categoryMap = ServiceLocator.getInstance().getMemoryImage("categoryMap");
        ConcurrentMap<Object, Object> entityCache = (ConcurrentMap<Object, Object>)categoryMap.get("entityCache");
        if(entityCache == null) {
            entityCache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(10000)
                    .build();
            categoryMap.put("entityCache", entityCache);
        } else {
            entity = (Map<String, Object>)entityCache.get(entityRid);
        }
        if(entity == null) {
            entity = getCategoryEntityDb(entityRid);
            if(entity != null) {
                entityCache.put(entityRid, entity);
            }
        }
        return entity;
    }

    public Map<String, Object> getCategoryEntityDb(String entityRid) {
        Map<String, Object> jsonMap = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex entity = (OrientVertex)DbService.getVertexByRid(graph, entityRid);
            if(entity != null) {
                jsonMap = new HashMap<String, Object>();
                jsonMap.put("type", entity.getLabel());
                jsonMap.put("rid", entity.getIdentity().toString());
                jsonMap.put("createDate", entity.getProperty("createDate"));
                OrientElementIterable iterable = entity.getProperty("in_Create");
                Iterator iterator = iterable.iterator();
                if(iterator.hasNext()) {
                    OrientVertex vertex = (OrientVertex)iterator.next();
                    jsonMap.put("createRid", vertex.getIdentity().toString());
                    jsonMap.put("createUserId", vertex.getProperty("userId"));
                    jsonMap.put("gravatar", vertex.getProperty("gravatar"));
                }
                iterable = entity.getProperty("out_HasTag");
                if(iterable != null) {
                    iterator = iterable.iterator();
                    List<String> tags = new ArrayList<String>();
                    while(iterator.hasNext()) {
                        OrientVertex vertex = (OrientVertex)iterator.next();
                        tags.add(vertex.getProperty("tagId"));
                    }
                    if(tags.size() > 0) jsonMap.put("tags", tags);
                }
                switch(entity.getLabel()) {
                    case "Post":
                        jsonMap.put("postId", entity.getProperty("postId"));
                        jsonMap.put("title", entity.getProperty("title"));
                        jsonMap.put("summary", entity.getProperty("summary"));
                        jsonMap.put("content", entity.getProperty("content"));
                        iterable = entity.getProperty("in_HasPost");
                        iterator = iterable.iterator();
                        if(iterator.hasNext()) {
                            OrientVertex vertex = (OrientVertex)iterator.next();
                            jsonMap.put("parentRid", vertex.getIdentity().toString());
                            jsonMap.put("parentId", vertex.getProperty("categoryId"));
                        }
                        if(entity.getProperty("originalAuthor") != null) jsonMap.put("originalAuthor", entity.getProperty("originalAuthor"));
                        if(entity.getProperty("originalSite") != null) jsonMap.put("originalSite", entity.getProperty("originalSite"));
                        if(entity.getProperty("originalUrl") != null) jsonMap.put("originalUrl", entity.getProperty("originalUrl"));
                        break;
                    case "Product":
                        jsonMap.put("productId", entity.getProperty("productId"));
                        jsonMap.put("name", entity.getProperty("name"));
                        jsonMap.put("description", entity.getProperty("description"));
                        jsonMap.put("variants", entity.getProperty("variants"));
                        iterable = entity.getProperty("in_HasProduct");
                        iterator = iterable.iterator();
                        if(iterator.hasNext()) {
                            OrientVertex vertex = (OrientVertex)iterator.next();
                            jsonMap.put("parentRid", vertex.getIdentity().toString());
                            jsonMap.put("parentId", vertex.getProperty("categoryId"));
                        }
                        break;
                    default:
                        logger.error("Unknown entity type", entity.getLabel());
                }
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return jsonMap;
    }

    /**
     * Get recent entity for category blog/news/forum/catelog regardless category
     *
     * @param objects
     * @return
     * @throws Exception
     */
    public boolean getRecentEntity(String categoryType, Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String host = (String)data.get("host");
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
        // allowUpdate will always be false as it has to be posted in a category context.
        boolean allowUpdate = false;

        // Get the list from cache.
        List<String> list = getRecentEntityList(host, categoryType, sortedBy, sortDir);

        if(list != null && list.size() > 0) {
            long total = list.size();
            List<Map<String, Object>> entities = new ArrayList<Map<String, Object>>();
            for(int i = pageSize*(pageNo - 1); i < Math.min(pageSize*pageNo, list.size()); i++) {
                String entityRid = list.get(i);
                Map<String, Object> entity = getCategoryEntity(entityRid);
                if(entity != null) {
                    entities.add(entity);
                } else {
                    logger.warn("Could not find entity {} from List {}", entityRid, host + categoryType);
                }
            }
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", total);
            result.put("entities", entities);
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

    protected List<String> getRecentEntityList(String host, String categoryType, String sortedBy, String sortDir) {
        List<String> list = null;
        // get list from cache
        Map<String, Object> categoryMap = ServiceLocator.getInstance().getMemoryImage("categoryMap");
        ConcurrentMap<Object, Object> listCache = (ConcurrentMap<Object, Object>)categoryMap.get("listCache");
        if(listCache == null) {
            listCache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(200)
                    .build();
            categoryMap.put("listCache", listCache);
        } else {
            list = (List<String>)listCache.get(host + categoryType);
        }
        if(list == null) {
            // not in cache, get from db
            list = getRecentEntityListDb(host, categoryType, sortedBy, sortDir);
            if(list != null) {
                listCache.put(host + categoryType, list);
            }
        }
        return list;
    }

    protected List<String> getRecentEntityListDb(String host, String categoryType, String sortedBy, String sortDir) {
        List<String> entityList = null;
        String sql = "select @rid from Post where host = ? and in_HasPost[0].@class = ? order by " + sortedBy + " " + sortDir;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> entities = graph.getRawGraph().command(query).execute(host, categoryType);
            if(entities.size() > 0) {
                entityList = new ArrayList<String>();
                for(ODocument entity: entities) {
                    entityList.add(((ODocument)entity.field("rid")).field("@rid").toString());
                }
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return entityList;
    }
}
