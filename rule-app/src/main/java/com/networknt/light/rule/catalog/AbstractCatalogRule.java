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
public abstract class AbstractCatalogRule extends BranchRule implements Rule {
    static final Logger logger = LoggerFactory.getLogger(AbstractCatalogRule.class);
    static final String branchType = "catalog";

    public abstract boolean execute (Object ...objects) throws Exception;

    public boolean addProduct(Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String parentId = (String) data.get("parentId");
        String host = (String) data.get("host");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex parent = getBranchByHostId(graph, branchType, host, parentId);
            if(parent == null) {
                error = "Id " + parentId + " doesn't exist on host " + host;
                inputMap.put("responseCode", 400);
            } else {
                Map<String, Object> user = (Map<String, Object>)payload.get("user");
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.putAll((Map<String, Object>) inputMap.get("data"));
                eventData.put("productId", HashUtil.generateUUID());
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
            // update the branch tree as the number of products has changed.
            Map<String, Object> branchMap = ServiceLocator.getInstance().getMemoryImage("branchMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)branchMap.get("treeCache");
            if(cache != null) {
                cache.remove(host + branchType);
            }
            return true;
        }
    }

    public boolean addProductEv (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        addProductDb(data);
        return true;
    }

    protected void addProductDb(Map<String, Object> data) throws Exception {
        String className = "Catalog";
        String id = "catalogId";
        String index = className + "." + id;
        String host = (String)data.get("host");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try{
            graph.begin();
            Vertex createUser = graph.getVertexByKey("User.userId", data.remove("createUserId"));
            OrientVertex product = graph.addVertex("class:Product", data);
            createUser.addEdge("Create", product);
            // parent
            OrientVertex parent = getBranchByHostId(graph, branchType, host, (String) data.get("parentId"));
            if(parent != null) {
                parent.addEdge("HasProduct", product);
            }
            // tag
            Set<String> inputTags = data.get("tags") != null? new HashSet<String>(Arrays.asList(((String) data.get("tags")).split("\\s*,\\s*"))) : new HashSet<String>();
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
                    product.addEdge("HasTag", tag);
                } else {
                    tag = graph.addVertex("class:Tag", "host", host, "tagId", tagId, "createDate", data.get("createDate"));
                    createUser.addEdge("Create", tag);
                    product.addEdge("HasTag", tag);
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
            // update the branch tree as the number of products has changed.
            Map<String, Object> branchMap = ServiceLocator.getInstance().getMemoryImage("branchMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)branchMap.get("treeCache");
            if(cache != null) {
                cache.remove(host + branchType);
            }
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
        String id = "catalogId";
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
        String rid = (String) data.get("@rid");
        String host = (String) data.get("host");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            // update product itself and we might have a new api to move product from one parent to another.
            Vertex product = DbService.getVertexByRid(graph, rid);
            if(product != null) {
                Map<String, Object> user = (Map<String, Object>)payload.get("user");
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.put("productId", product.getProperty("productId"));
                eventData.put("name", data.get("name"));
                eventData.put("description", data.get("description"));
                eventData.put("variants", data.get("variants"));
                eventData.put("updateDate", new java.util.Date());
                eventData.put("updateUserId", user.get("userId"));

                // tags
                Set<String> inputTags = data.get("tags") != null? new HashSet<String>(Arrays.asList(((String)data.get("tags")).split("\\s*,\\s*"))) : new HashSet<String>();
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
            // update the branch tree as the last update time has changed.
            Map<String, Object> branchMap = ServiceLocator.getInstance().getMemoryImage("branchMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)branchMap.get("treeCache");
            if(cache != null) {
                cache.remove(host + branchType);
            }
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
                            Vertex tag = graph.addVertex("class:Tag", "host", data.get("host"), "tagId", tagId, "createDate", data.get("createDate"));
                            updateUser.addEdge("Create", tag);
                            product.addEdge("HasTag", tag);
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
        boolean allowAdd = false;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload != null) {
            Map<String,Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(roles.contains("owner")) {
                allowAdd = true;
            } else if(roles.contains("admin") || roles.contains("catalogAdmin") || roles.contains("productAdmin")){
                if(host.equals(user.get("host"))) {
                    allowAdd = true;
                }
            }
        }
        // get ancestors
        List<Map<String, Object>> ancestors = getAncestorDb(rid);

        // TODO support the following lists: recent, popular
        // Get the page from cache.
        List<String> list = null;
        Map<String, Object> branchMap = ServiceLocator.getInstance().getMemoryImage("branchMap");
        ConcurrentMap<Object, Object> listCache = (ConcurrentMap<Object, Object>)branchMap.get("listCache");
        if(listCache == null) {
            listCache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            branchMap.put("listCache", listCache);
        } else {
            list = (List<String>)listCache.get(rid + sortedBy);
        }

        ConcurrentMap<Object, Object> productCache = (ConcurrentMap<Object, Object>)branchMap.get("productCache");
        if(productCache == null) {
            productCache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            branchMap.put("productCache", productCache);
        }

        if(list == null) {
            // get the list for db
            list = new ArrayList<String>();
            String json = getCatalogProductDb(rid, sortedBy);
            if(json != null) {
                // convert json to list of maps.
                List<Map<String, Object>> products = mapper.readValue(json,
                        new TypeReference<ArrayList<HashMap<String, Object>>>() {
                        });
                for(Map<String, Object> product: products) {
                    String productRid = (String)product.get("rid");
                    list.add(productRid);
                    product.remove("@rid");
                    product.remove("@type");
                    product.remove("@version");
                    product.remove("@fieldTypes");
                    productCache.put(productRid, product);
                }
            }
            listCache.put(rid + sortedBy, list);
        }
        long total = list.size();
        if(total > 0) {
            List<Map<String, Object>> products = new ArrayList<Map<String, Object>>();
            for(int i = pageSize*(pageNo - 1); i < Math.min(pageSize*pageNo, list.size()); i++) {
                String productRid = list.get(i);
                Map<String, Object> product = (Map<String, Object>)productCache.get(productRid);
                products.add(product);
            }
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", total);
            result.put("products", products);
            result.put("allowAdd", allowAdd);
            result.put("ancestors", ancestors);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        } else {
            // there is no product available. but still need to return allowAdd
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", 0);
            result.put("allowAdd", allowAdd);
            result.put("ancestors", ancestors);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        }
    }

    protected String getCatalogProductDb(String rid, String sortedBy) {
        String json = null;
        // TODO there is a bug that prepared query only support one parameter. That is why sortedBy is concat into the sql.
        String sql = "select @rid, productId, name, description, variants, createDate, parentId, in_Create[0].@rid as createRid, in_Create[0].userId as createUserId " +
                "from (traverse out_Own, out_HasProduct from ?) where @class = 'Product' order by " + sortedBy + " desc";
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

    protected List getAncestorDb(String rid) {
        List<Map<String, Object>> ancestors = null;
        String sql = "select @rid, catalogId, description from (traverse in('Own') from ?)";
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
                    map.put("catalogId", doc.getProperty("catalogId"));
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
}
