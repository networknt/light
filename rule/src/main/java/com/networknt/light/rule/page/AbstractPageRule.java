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

package com.networknt.light.rule.page;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.hazelcast.util.executor.StripedRunnable;
import com.networknt.light.model.CacheObject;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.networknt.light.util.Util;
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by husteve on 10/24/2014.
 */
public abstract class AbstractPageRule extends AbstractRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractPageRule.class);

    public abstract boolean execute (Object ...objects) throws Exception;

    /*
    static {
        System.out.println("AbstractPageRule is called");
        ITopic<Map<String, Object>> page = ServiceLocator.getInstance().getHzInstance().getTopic( "page" );
        page.addMessageListener(new PageMessageListenerImpl());
    }

    // As all the operations are in memory, use the same thread from the publisher.
    private static class PageMessageListenerImpl implements MessageListener<Map<String, Object>> {
        @Override
        public void onMessage(final Message<Map<String, Object>> message) {
            Map<String, Object> eventMap = message.getMessageObject();
            Map<String, Object> data = (Map<String, Object>)eventMap.get("data");
            System.out.println("Received: " + eventMap);
            // simply remove the page from cache in order to reload in the next getPage rule.
            Map<String, Object> pageMap = ServiceLocator.getInstance().getMemoryImage("pageMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)pageMap.get("cache");
            if(cache != null) {
                cache.remove(data.get("pageId"));
            }
        }
    }
    */

    protected CacheObject getPageById(OrientGraph graph, String pageId) {
        CacheObject co = null;
        Map<String, Object> pageMap = ServiceLocator.getInstance().getMemoryImage("pageMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)pageMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            pageMap.put("cache", cache);
        } else {
            co = (CacheObject)cache.get(pageId);
        }
        if(co == null) {
            OrientVertex page = (OrientVertex)graph.getVertexByKey("Page.pageId", pageId);
            if(page != null) {
                String json = page.getRecord().toJSON();
                co = new CacheObject(page.getProperty("@version").toString(), json);
                cache.put(pageId, co);
            }
        }
        return co;
    }

    protected void addPage(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex createUser = graph.getVertexByKey("User.userId", data.remove("createUserId"));
            OrientVertex page = graph.addVertex("class:Page", data);
            createUser.addEdge("Create", page);
            graph.commit();
            String json = page.getRecord().toJSON();
            Map<String, Object> pageMap = ServiceLocator.getInstance().getMemoryImage("pageMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)pageMap.get("cache");
            if(cache == null) {
                cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                        .maximumWeightedCapacity(1000)
                        .build();
                pageMap.put("cache", cache);
            }
            cache.put(data.get("pageId"), new CacheObject(page.getProperty("@version").toString(), json));
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
    }

    protected void delPage(String pageId) {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex page = graph.getVertexByKey("Page.pageId", pageId);
            if(page != null) {
                graph.removeVertex(page);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
        Map<String, Object> pageMap = ServiceLocator.getInstance().getMemoryImage("pageMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)pageMap.get("cache");
        if(cache != null) {
            cache.remove(pageId);
        }
    }

    protected void updPage(Map<String, Object> data) {
        String pageId = (String)data.get("pageId");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            OrientVertex page = (OrientVertex)graph.getVertexByKey("Page.pageId", pageId);
            if(page != null) {
                page.setProperty("content", data.get("content"));
                page.setProperty("updateDate", data.get("updateDate"));
                Vertex updateUser = graph.getVertexByKey("User.userId", data.get("updateUserId"));
                updateUser.addEdge("Update", page);
            }
            graph.commit();
            String json = page.getRecord().toJSON();
            Map<String, Object> pageMap = ServiceLocator.getInstance().getMemoryImage("pageMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)pageMap.get("cache");
            if(cache == null) {
                cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                        .maximumWeightedCapacity(1000)
                        .build();
                pageMap.put("cache", cache);
            }
            CacheObject co = (CacheObject)cache.get(pageId);
            if(co != null) {
                co.setEtag(page.getProperty("@version"));
                co.setData(json);
            } else {
                cache.put(pageId, new CacheObject(page.getProperty("@version").toString(), json));
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
    }

    protected void impPage(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        String pageId = (String)data.get("pageId");
        try {
            graph.begin();
            OrientVertex page = (OrientVertex)graph.getVertexByKey("Page.pageId", pageId);
            if(page != null) {
                graph.removeVertex(page);
            }
            Vertex createUser = graph.getVertexByKey("User.userId", data.remove("createUserId"));
            page = graph.addVertex("class:Page", data);
            createUser.addEdge("Create", page);
            graph.commit();
            String json = page.getRecord().toJSON();
            Map<String, Object> pageMap = ServiceLocator.getInstance().getMemoryImage("pageMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)pageMap.get("cache");
            if(cache == null) {
                cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                        .maximumWeightedCapacity(1000)
                        .build();
                pageMap.put("cache", cache);
            }
            CacheObject co = (CacheObject)cache.get(pageId);
            if(co != null) {
                co.setEtag(page.getProperty("@version"));
                co.setData(json);
            } else {
                cache.put(pageId, new CacheObject(page.getProperty("@version").toString(), json));
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
    }

    protected String getAllPage(OrientGraph graph, String host) {
        String json = null;
        String sql = "SELECT FROM Page";
        if(host != null) {
            sql = sql + " WHERE host = '" + host + "' OR host IS NULL";
        }
        OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sql);
        List<ODocument> pages = graph.getRawGraph().command(query).execute();
        json = OJSONWriter.listToJSON(pages, null);
        return json;
    }

    protected String getPageMap(OrientGraph graph, String host) throws Exception {
        String sql = "SELECT FROM Page";
        if(host != null) {
            sql = sql + " WHERE host = '" + host + "' OR host IS NULL";
        }
        Map<String, String> map = new HashMap<String, String>();
        for (Vertex page : (Iterable<Vertex>) graph.command(new OCommandSQL(sql)).execute()) {
            map.put(page.getProperty("pageId"), page.getProperty("content"));
        }
        return mapper.writeValueAsString(map);
    }

}
