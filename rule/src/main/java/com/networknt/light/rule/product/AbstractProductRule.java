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

package com.networknt.light.rule.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by husteve on 10/14/2014.
 */
public abstract class AbstractProductRule extends AbstractRule implements Rule {
    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
    public abstract boolean execute (Object ...objects) throws Exception;

    protected ODocument getProductByHostName(String host, String name) {
        ODocument product = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OIndex<?> hostNameIdx = db.getMetadata().getIndexManager().getIndex("hostNameIdx");
            // this is a unique index, so it retrieves a OIdentifiable
            OCompositeKey key = new OCompositeKey(host, name);
            OIdentifiable oid = (OIdentifiable) hostNameIdx.get(key);
            if (oid != null) {
                product = (ODocument)oid.getRecord();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return product;
    }

    protected ODocument addProduct(Map<String, Object> data, String userId) throws Exception {
        ODocument product = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        Map<String, Object> productMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("productMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)productMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            productMap.put("cache", cache);
        }

        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            product = new ODocument(schema.getClass("Product"));
            product.field("host", data.get("host"));
            product.field("name", data.get("name"));
            product.field("attributes", data.get("attributes"));
            java.util.Date d = new java.util.Date();
            product.field("createDate", d);
            product.field("updateDate", d);
            product.field("createUser", userId);
            product.save();
            cache.put(product.field("@rid").toString(), product);
            String categoryRid = (String)data.get("categoryRid");
            if(categoryRid != null) {
                // get the category and update entities list
                ODocument category = getCategoryByRid(categoryRid);
                List entities = category.field("entities");
                if(entities == null) {
                    entities = new ArrayList();
                }
                entities.add(product);
                category.field("entities", entities);
                category.save();
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return product;
    }

    protected void delProduct(String productRid) throws Exception {
        ODocument product = DbService.delODocumentByRid(productRid);
        // rebuild cache in memory.
        Map<String, Object> productMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("productMap");
        // update central cache
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)productMap.get("cache");
        if(cache != null) {
            cache.remove(productRid);
        }
    }

    protected void updProduct(String productRid, Map<String, Object> data) throws Exception {
        ODocument product = getProductByRid(productRid);
        if(product != null) {
            product.field("name", data.get("name"));
            product.field("attributes", data.get("attributes"));
            product.field("updateDate", new java.util.Date());
            product.save();
        }
    }

    protected List<ODocument> searchProductDb(Map<String, Object> criteria) {
        List<ODocument> products = null;
        StringBuilder sql = new StringBuilder("SELECT FROM Product ");
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
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql.toString());
            products = db.command(query).execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return products;
    }

    protected String searchProduct(Map<String, Object> criteria) throws Exception {
        // first check if the full list is in cache.
        String json = null;
        Map<String, Object> result = new HashMap<String, Object>();
        List<ODocument> products = new ArrayList<ODocument>();
        int total = 0;
        String host = (String)criteria.get("host");
        Map<String, Object> productMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("productMap");
        Map<String, Object> hostMap = (Map<String, Object>)productMap.get(host);
        if(hostMap == null) {
            hostMap = new ConcurrentHashMap<String, Object>(10, 0.9f, 1);
            productMap.put(host, hostMap);
        }
        String key = null;
        String categoryRid = (String)criteria.get("categoryRid");
        if(categoryRid != null) {
            key = categoryRid + criteria.get("sortedBy");
        } else {
            key = "" + criteria.get("sortedBy");
        }
        Integer pageNo = (Integer)criteria.remove("pageNo");
        Integer pageSize = (Integer)criteria.remove("pageSize");
        List<String> list = (List<String>)hostMap.get(key);
        if(list == null) {
            // not in cache, search from db and put them in cache.
            List<ODocument> docs = searchProductDb(criteria);
            total = docs.size();
            int i = 0;
            list = new ArrayList<String>();
            for(ODocument doc: docs) {
                list.add(doc.field("@rid").toString());
                if(i >= pageSize * (pageNo - 1) && i < pageSize*pageNo) {
                    products.add(doc);
                    i++;
                    // put only the current page in cache.
                    ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)productMap.get("cache");
                    if(cache == null) {
                        cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                                .maximumWeightedCapacity(1000)
                                .build();
                        productMap.put("cache", cache);
                    }
                    cache.put(doc.field("@rid").toString(), doc);
                }
            }
        } else {
            // we have a list of rids.
            total = list.size();
            for(int i = pageSize*(pageNo - 1); i < Math.min(pageSize * pageNo, list.size()); i++) {
                String rid = (String)list.get(i);
                ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)productMap.get("cache");
                ODocument product = (ODocument)cache.get(rid);
                if(product == null) {
                    // not in cache, get from db and put it into the cache.
                    product = DbService.getODocumentByRid(rid);
                    cache.put(rid, product);
                }
                products.add(product);
            }
        }
        if(products != null && products.size() > 0) {
            result.put("total", total);
            result.put("products", OJSONWriter.listToJSON(products, null));
            json = mapper.writeValueAsString(result);
        }
        return json;
    }

    protected Map<String, Object> refreshCache(String host) {
        Map<String, Object> productMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("productMap");
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("host", host);
        criteria.put("sortedBy", "updateDate");
        criteria.put("sortDir", "DESC");
        List<ODocument> products = searchProductDb(criteria);
        Map<String, Object> hostMap = new ConcurrentHashMap<String, Object>(2, 0.9f, 1);
        List<String> newList = new ArrayList<String>();
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)productMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            productMap.put("cache", cache);
        }
        int i = 0;
        int pageSize = 2; // TODO get from config
        for (ODocument product : products) {
            // cache the first page for now. most people will read the first page as it contains
            // new posts.
            if(i < pageSize) {
                cache.put(product.field("@rid").toString(), product);
            }
            newList.add(product.field("@rid").toString());
        }
        hostMap.put("newList", newList);

        // TODO build hot list

        productMap.put(host, hostMap);
        return hostMap;
    }
}
