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

package com.networknt.light.rule.catalog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.BranchRule;
import com.networknt.light.rule.Rule;
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
 */
public abstract class AbstractCatalogRule extends AbstractBfnRule implements Rule {
    static final Logger logger = LoggerFactory.getLogger(AbstractCatalogRule.class);
    static final String categoryType = "catalog";

    public abstract boolean execute (Object ...objects) throws Exception;

    public boolean addProduct(Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String host = (String) data.get("host");
        String parentRid = (String)data.remove("parentRid");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String userHost = (String)user.get("host");
        if(userHost != null && !userHost.equals(host)) {
            error = "You can only add product from host: " + host;
            inputMap.put("responseCode", 403);
        } else {
            OrientGraph graph = ServiceLocator.getInstance().getGraph();
            try {
                // make sure parent exists if it is not empty.
                Vertex parent = DbService.getVertexByRid(graph, parentRid);
                if(parent == null) {
                    error = "Parent with @rid " + parentRid + " cannot be found.";
                    inputMap.put("responseCode", 404);
                } else {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.putAll((Map<String, Object>) inputMap.get("data"));
                    eventData.put("parentId", parent.getProperty("categoryId"));
                    eventData.put("productId", HashUtil.generateUUID());
                    eventData.put("createDate", new Date());
                    eventData.put("createUserId", user.get("userId"));
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
            clearCache(host, categoryType, parentRid);
            return true;
        }
    }

    private void clearCache(String host, String bfnType, String parentRid) {
        Map<String, Object> categoryMap = ServiceLocator.getInstance().getMemoryImage("categoryMap");
        ConcurrentMap<Object, Object> listCache = (ConcurrentMap<Object, Object>)categoryMap.get("listCache");
        if(listCache != null && listCache.size() > 0) {
            listCache.remove(host + bfnType);
            listCache.remove(parentRid + "createDate" + "desc");
        }
    }


    public boolean addProductEv (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        addProductDb(data);
        return true;
    }

    protected void addProductDb(Map<String, Object> data) throws Exception {
        String host = (String)data.get("host");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            OrientVertex createUser = (OrientVertex)graph.getVertexByKey("User.userId", data.remove("createUserId"));
            String parentId = (String)data.remove("parentId");
            List<String> tags = (List<String>)data.remove("tags");
            OrientVertex product = graph.addVertex("class:Product", data);
            createUser.addEdge("Create", product);
            // parent
            OrientVertex parent = getBranchByHostId(graph, categoryType, host, parentId);
            if(parent != null) {
                parent.addEdge("HasProduct", product);
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
                        product.addEdge("HasTag", tag);
                    } else {
                        tag = graph.addVertex("class:Tag", "host", host, "tagId", tagId, "createDate", data.get("createDate"));
                        createUser.addEdge("Create", tag);
                        product.addEdge("HasTag", tag);
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

    public boolean delProduct(Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String)data.get("@rid");
        String parentRid = null;
        String host = (String)data.get("host");
        String error = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex product = (OrientVertex) DbService.getVertexByRid(graph, rid);
            if(product != null) {
                // check if the product has variant, if yes, you cannot delete it for now
                // TODO fix it after orientdb 2.2 release.
                // https://github.com/orientechnologies/orientdb/issues/1108
                if(product.countEdges(Direction.OUT, "HasComment") > 0) {
                    error = "Product has comment(s), cannot be deleted";
                    inputMap.put("responseCode", 400);
                } else {
                    // get the parentRid in order to clear cache
                    for(Edge e: product.getEdges(Direction.IN, "HasProduct")) {
                        Vertex edgeParent = e.getVertex(Direction.OUT);
                        parentRid = edgeParent.getId().toString();
                    }

                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("productId", product.getProperty("productId"));
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
            clearCache(host, categoryType, parentRid);
            return true;
        }
    }

    public boolean delProductEv (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        delProductDb(data);
        return true;
    }

    protected void delProductDb(Map<String, Object> data) throws Exception {
        String className = "Catalog";
        String id = "categoryId";
        String index = className + "." + id;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            OrientVertex product = (OrientVertex)graph.getVertexByKey("Product.productId", data.get("productId"));
            if(product != null) {
                // TODO cascade deleting all comments belong to the product.
                // Need to come up a query on that to get the entire tree.
                /*
                // https://github.com/orientechnologies/orientdb/issues/1108
                delete graph...
                */
                graph.removeVertex(product);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    public boolean updProduct(Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("rid");
        String originalParentRid = null;
        String parentRid = null;
        String host = (String) data.get("host");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            String userHost = (String)user.get("host");
            if(userHost != null && !userHost.equals(host)) {
                inputMap.put("result", "You can only update " + categoryType + " from host: " + host);
                inputMap.put("responseCode", 403);
                return false;
            } else {
                // update product itself and we might have a new api to move product from one parent to another.
                Vertex product = DbService.getVertexByRid(graph, rid);
                if(product != null) {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("productId", product.getProperty("productId"));
                    eventData.put("name", data.get("name"));
                    eventData.put("host", data.get("host"));
                    eventData.put("description", data.get("description"));
                    eventData.put("variants", data.get("variants"));
                    eventData.put("updateDate", new Date());
                    eventData.put("updateUserId", user.get("userId"));

                    // parent
                    parentRid =  (String)data.get("parentRid");
                    if(parentRid != null) {
                        Vertex parent = DbService.getVertexByRid(graph, parentRid);
                        if(parent != null) {
                            boolean found = false;
                            for(Edge e: product.getEdges(Direction.IN, "HasProduct")) {
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
                        // get originalParentRid from product for clearing cache.
                        for(Edge e: product.getEdges(Direction.IN, "HasProduct")) {
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
                        for (Vertex vertex : (Iterable<Vertex>) product.getVertices(Direction.OUT, "HasTag")) {
                            delTags.add((String)vertex.getProperty("tagId"));
                        }
                        if(delTags.size() > 0) eventData.put("delTags", delTags);
                    } else {
                        Set<String> inputTags = new HashSet<String>(tags);
                        Set<String> storedTags = new HashSet<String>();
                        for (Vertex vertex : (Iterable<Vertex>) product.getVertices(Direction.OUT, "HasTag")) {
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
            }

            // make sure parent exists if it is not empty.

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
            clearCache(host, categoryType, originalParentRid);
            if(parentRid != null) clearCache(host, categoryType, parentRid);
            return true;
        }
    }

    public boolean updProductEv (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        updProductDb(data);
        return true;
    }

    protected void updProductDb(Map<String, Object> data) throws Exception {
        String host = (String)data.get("host");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            Vertex updateUser = graph.getVertexByKey("User.userId", data.remove("updateUserId"));
            OrientVertex product = (OrientVertex)graph.getVertexByKey("Product.productId", data.get("productId"));
            if(product != null) {
                updateUser.addEdge("Update", product);
                // fields
                if(data.get("name") != null) {
                    product.setProperty("name", data.get("name"));
                } else {
                    product.removeProperty("name");
                }
                if(data.get("description") != null) {
                    product.setProperty("description", data.get("description"));
                } else {
                    product.removeProperty("description");
                }
                if(data.get("variants") != null) {
                    product.setProperty("variants", data.get("variants"));
                } else {
                    product.removeProperty("variants");
                }
                product.setProperty("updateDate", data.get("updateDate"));

                // handle parent update
                String parentId = (String)data.get("parentId");
                if(parentId != null) {
                    OrientVertex parent = getBranchByHostId(graph, categoryType, (String) data.get("host"), (String) data.get("parentId"));
                    if(parent != null) {
                        // remove the current edge and add a new one.
                        for(Edge e : product.getEdges(Direction.IN, "HasProduct")) {
                            e.remove();
                        }
                        parent.addEdge("HasProduct", product);
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
                            product.addEdge("HasTag", tag);
                        } else {
                            Vertex tag = graph.addVertex("class:Tag", "host", data.get("host"), "tagId", tagId, "createDate", data.get("updateDate"));
                            updateUser.addEdge("Create", tag);
                            product.addEdge("HasTag", tag);
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
                            for (Edge edge : (Iterable<Edge>) product.getEdges(Direction.OUT, "HasTag")) {
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

    /*
    public boolean getCatalogProduct(Object ...objects) throws Exception {
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
        boolean allowUpdate = false;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload != null) {
            Map<String,Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(roles.contains("owner")) {
                allowUpdate = true;
            } else if(roles.contains("admin") || roles.contains("catalogAdmin") || roles.contains("productAdmin")){
                if(host.equals(user.get("host"))) {
                    allowUpdate = true;
                }
            }
        }
        // get ancestors
        List<Map<String, Object>> ancestors = getAncestorDb(rid);

        // TODO support the following lists: recent, popular
        // Get the page from cache.
        List<String> list = null;
        Map<String, Object> categoryMap = ServiceLocator.getInstance().getMemoryImage("categoryMap");
        ConcurrentMap<Object, Object> listCache = (ConcurrentMap<Object, Object>)categoryMap.get("listCache");
        if(listCache != null) {
            list = (List<String>)listCache.get(rid + sortedBy + sortDir);
        }


        if(list == null) {
            list = getCategoryEntityList(rid, sortedBy, sortDir);
        }
        long total = list.size();
        if(total > 0) {
            List<Map<String, Object>> entities = new ArrayList<Map<String, Object>>();
            ConcurrentMap<Object, Object> entityCache = (ConcurrentMap<Object, Object>)categoryMap.get("entityCache");
            for(int i = pageSize*(pageNo - 1); i < Math.min(pageSize*pageNo, list.size()); i++) {
                String entityRid = list.get(i);
                Map<String, Object> entity = (Map<String, Object>)entityCache.get(entityRid);
                if(entity == null) {
                    logger.warn("entity {} is missing from cache but list {} is in cache", entityRid, rid);
                    getCategoryEntityList(rid, sortedBy, sortDir);
                    entity = (Map<String, Object>)entityCache.get(entityRid);

                }
                entities.add(entity);
            }
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", total);
            result.put("products", entities);
            result.put("rid", rid);
            result.put("allowUpdate", allowUpdate);
            result.put("ancestors", ancestors);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        } else {
            // there is no product available. but still need to return allowUpdate
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", 0);
            result.put("rid", rid);
            result.put("allowUpdate", allowUpdate);
            result.put("ancestors", ancestors);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        }
    }
    protected String getCategoryEntitytDb(String rid, String sortedBy, String sortDir) {
        String json = null;
        // TODO there is a bug that prepared query only support one parameter. That is why sortedBy is concat into the sql.
        String sql = "select @rid, productId, name, description, variants, createDate, parentId, in_Create[0].@rid as createRid, " +
                "in_Create[0].userId as createUserId, out_HasTag.tagId " +
                "from (traverse out_Own, out_HasProduct from ?) where @class = 'Product' order by " + sortedBy + " " + sortDir;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> products = graph.getRawGraph().command(query).execute(rid);
            if(products.size() > 0) {
                json = OJSONWriter.listToJSON(products, null);
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return json;
    }
    */

    @Override
    protected List<String> getCategoryEntityListDb(String categoryRid, String sortedBy, String sortDir) {
        List<String> entityList = null;
        String sql = "select @rid from (traverse out_Own, out_HasProduct from ?) where @class = 'Product' order by " + sortedBy + " " + sortDir;
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

    @Override
    protected Map<String, Object> getCategoryEntityDb(String entityRid) {
        Map<String, Object> jsonMap = null;
        String sql = "SELECT @rid as rid, productId, name, description, variants, createDate," +
                "in_HasProduct[0].@rid as parentRid, in_HasProduct[0].categoryId as parentId, " +
                "in_Create[0].@rid as createRid, in_Create[0].userId as createUserId, " +
                "out_HasTag.tagId as tags FROM ? ";
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> entities = graph.getRawGraph().command(query).execute(entityRid);
            if(entities.size() > 0) {
                jsonMap = new HashMap<String, Object>();
                ODocument entity = entities.get(0);
                jsonMap.put("rid", ((ODocument)entity.field("rid")).field("@rid").toString());
                jsonMap.put("productId", entity.field("productId"));
                jsonMap.put("name", entity.field("name"));
                jsonMap.put("description", entity.field("description"));
                jsonMap.put("variants", entity.field("variants"));
                jsonMap.put("createDate", entity.field("createDate"));
                jsonMap.put("parentRid", ((ODocument)entity.field("parentRid")).field("@rid").toString());
                jsonMap.put("parentId", entity.field("parentId"));
                jsonMap.put("createRid", ((ODocument)entity.field("createRid")).field("@rid").toString());
                jsonMap.put("createUserId", entity.field("createUserId"));
                if(entity.field("tags") != null) jsonMap.put("tags", entity.field("tags"));
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return jsonMap;
    }

    /*
    protected List getAncestorDb(String rid) {
        List<Map<String, Object>> ancestors = null;
        String sql = "select @rid, categoryId, description from (traverse in('Own') from ?)";
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> docs = graph.getRawGraph().command(query).execute(rid);
            if(docs.size() > 0) {
                ancestors = new ArrayList<Map<String, Object>>();
                for (int i=docs.size()-1; i >= 0; i--) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    OrientVertex doc = graph.getVertex(docs.get(i).getRecord());
                    String id = doc.getProperty("rid").toString();
                    id = id.substring(id.indexOf('[') + 1, id.indexOf(']'));
                    map.put("rid", id);
                    map.put("categoryId", doc.getProperty("categoryId"));
                    map.put("description", doc.getProperty("description"));
                    ancestors.add(map);
                }
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return ancestors;
    }
    */

    public boolean getProduct(Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String categoryId = (String)data.get("categoryId");
        String host = (String)data.get("host");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        String json = null;
        Map<String,Object> user = (Map<String, Object>)payload.get("user");
        List roles = (List)user.get("roles");
        if(roles.contains("owner")) {
            json = getProductDb();
        } else {
            if(host.equals(user.get("host"))) {
                json = getProductDb(host);
            } else {
                inputMap.put("result", "Permission denied");
                inputMap.put("responseCode", 401);
                return false;
            }
        }
        if(json != null) {
            inputMap.put("result", json);
            return true;
        } else {
            inputMap.put("result", "Not Found");
            inputMap.put("responseCode", 404);
            return false;
        }
    }

    protected String getProductDb(String host) {
        String json = null;
        String sql = "select from product where host = ?";
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> products = graph.getRawGraph().command(query).execute(host);
            if(products.size() > 0) {
                json = OJSONWriter.listToJSON(products, null);
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return json;
    }

    protected String getProductDb() {
        String json = null;
        String sql = "select from product";
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> products = graph.getRawGraph().command(query).execute();
            if(products.size() > 0) {
                json = OJSONWriter.listToJSON(products, null);
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return json;
    }

    @Override
    protected List<String> getRecentEntityListDb(String host, String categoryType, String sortedBy, String sortDir) {
        List<String> entityList = null;
        String sql = "select @rid from Product where host = ? order by " + sortedBy + " " + sortDir;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> entities = graph.getRawGraph().command(query).execute(host);
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
