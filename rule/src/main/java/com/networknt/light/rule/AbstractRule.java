package com.networknt.light.rule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OCompositeKey;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by husteve on 10/14/2014.
 */
public abstract class AbstractRule implements Rule {
    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
    public abstract boolean execute (Object ...objects) throws Exception;

    protected ODocument getCategoryByRid(String categoryRid) {
        Map<String, Object> categoryMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("categoryMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)categoryMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            categoryMap.put("cache", cache);
        }
        ODocument category = (ODocument)cache.get("categoryRid");
        if(category == null) {
            // TODO warning to increase cache if this happens.
            category = DbService.getODocumentByRid(categoryRid);
            // put it into the category cache.
            if(category != null) {
                cache.put(categoryRid, category);
            }
        }
        return category;
    }

    protected ODocument getProductByRid(String productRid) {
        Map<String, Object> productMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("productMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)productMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            productMap.put("cache", cache);
        }
        ODocument product = (ODocument)cache.get("productRid");
        if(product == null) {
            // TODO warning to increase cache if this happens.
            product = DbService.getODocumentByRid(productRid);
            if(product != null) {
                cache.put(productRid, product);
            }
        }
        return product;
    }

    protected Map<String, Object> getEventMap(Map<String, Object> inputMap) {
        Map<String, Object> eventMap = new HashMap<String, Object>();
        Map<String, Object> payload = (Map<String, Object>)inputMap.get("payload");
        if(payload != null) {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            if(user != null)  eventMap.put("createUserId", user.get("userId"));
        }

        if(inputMap.get("host") != null) {
            eventMap.put("host", inputMap.get("host"));
        }
        if(inputMap.get("app") != null) {
            eventMap.put("app", inputMap.get("app"));
        }
        eventMap.put("category", inputMap.get("category"));
        eventMap.put("name", inputMap.get("name"));
        eventMap.put("createDate", new java.util.Date());
        eventMap.put("data", new HashMap<String, Object>());
        return eventMap;
    }

    protected ODocument getODocumentByHostId(String index, String host, String id) {
        ODocument doc = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OIndex<?> hostIdIdx = db.getMetadata().getIndexManager().getIndex(index);
            // this is a unique index, so it retrieves a OIdentifiable
            OCompositeKey key = new OCompositeKey(host, id);
            OIdentifiable oid = (OIdentifiable) hostIdIdx.get(key);
            if (oid != null) {
                doc = (ODocument)oid.getRecord();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return doc;
    }

}
