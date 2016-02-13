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

package com.networknt.light.rule.menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.networknt.light.util.Util;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 10/27/2014.
 * The menuMap.cache has three type of keys. host, label and @rid
 */
public abstract class AbstractMenuRule extends AbstractRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractMenuRule.class);

    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();

    public abstract boolean execute (Object ...objects) throws Exception;

    protected String getJsonByRid(String rid) {
        String json = null;
        Map<String, Object> menuMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("menuMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)menuMap.get("cache");
        if(cache != null) {
            json = (String)cache.get("rid");
        }
        if(json == null) {
            json = DbService.getJsonByRid(rid);
            // put it into the blog cache.
            if(json != null) {
                if(cache == null) {
                    cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                            .maximumWeightedCapacity(1000)
                            .build();
                    menuMap.put("cache", cache);
                }
                cache.put(rid, json);
            }
        }
        return json;
    }

    protected void delMenu(Map<String, Object> data) throws Exception {
        String host = (String)data.get("host");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex menu = graph.getVertexByKey("Menu.host", host);
            if(menu != null) {
                // cascade deleting all menuItems belong to the host only.
                for (Vertex menuItem : graph.getVerticesOfClass("MenuItem")) {
                    if(host.equals(menuItem.getProperty("host"))) {
                        graph.removeVertex(menuItem);
                    }
                }
                graph.removeVertex(menu);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }

        Map<String, Object> menuMap = ServiceLocator.getInstance().getMemoryImage("menuMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)menuMap.get("cache");
        if(cache != null) {
            cache.remove(host);
        }
    }

    protected void delMenuItem(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex menuItem = graph.getVertexByKey("MenuItem.menuItemId",data.get("menuItemId"));
            if(menuItem != null) {
                graph.removeVertex(menuItem);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }

        // no need to refresh cache as there is no reference to this menuItem anywhere.
    }

    protected String addMenu( Map<String, Object> data) throws Exception {
        String json = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            OrientVertex menu = graph.addVertex("class:Menu", "host", data.get("host"), "createDate", data.get("createDate"));
            List<String> addMenuItems = (List<String>)data.get("addMenuItems");
            if(addMenuItems != null && addMenuItems.size() > 0) {
                // find vertex for each menuItem id and create edge to it.
                for(String menuItemId: addMenuItems) {
                    Vertex menuItem = graph.getVertexByKey("MenuItem.menuItemId", menuItemId);
                    menu.addEdge("Own", menuItem);
                }
            }
            Vertex user = graph.getVertexByKey("User.userId", data.get("createUserId"));
            user.addEdge("Create", menu);
            graph.commit();
            json = menu.getRecord().toJSON("fetchPlan:menuItems:2");
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
        Map<String, Object> menuMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("menuMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)menuMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(100)
                    .build();
            menuMap.put("cache", cache);
        }
        cache.put(data.get("host"), json);
        return json;
    }

    protected void addMenuItem(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex user = graph.getVertexByKey("User.userId", data.remove("createUserId"));
            List<String> addMenuItems = (List<String>)data.remove("addMenuItems");
            OrientVertex menuItem = graph.addVertex("class:MenuItem", data);
            if(addMenuItems != null && addMenuItems.size() > 0) {
                // find vertex for each menuItem id and create edge to it.
                for(String menuItemId: addMenuItems) {
                    Vertex childMenuItem = graph.getVertexByKey("MenuItem.menuItemId", menuItemId);
                    menuItem.addEdge("Own", childMenuItem);
                }
            }
            user.addEdge("Create", menuItem);
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
    }

    protected String getMenu(OrientGraph graph, String host) {
        String json = null;
        Map<String, Object> menuMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("menuMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)menuMap.get("cache");
        if(cache != null) {
            json = (String)cache.get(host);
        }
        if(json == null) {
            Vertex menu = graph.getVertexByKey("Menu.host", host);
            if(menu != null) {
                json = ((OrientVertex)menu).getRecord().toJSON("rid,fetchPlan:[*]in_Create:-2 [*]out_Create:-2 [*]in_Update:-2 [*]out_Update:-2 [*]in_Own:-2 [*]out_Own:4");
            }
            if(json != null) {
                if(cache == null) {
                    cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                            .maximumWeightedCapacity(1000)
                            .build();
                    menuMap.put("cache", cache);
                }
                cache.put(host, json);
            }
        }
        return json;
    }

    protected String getMenuItem(OrientGraph graph, String menuItemId) throws Exception {
        String json = null;
        Vertex menuItem = graph.getVertexByKey("MenuItem.menuItemId", menuItemId);
        if(menuItem != null) {
            json = ((OrientVertex)menuItem).getRecord().toJSON();
        }
        return json;
    }

    protected String getAllMenu(OrientGraph graph, String host) {
        String json = null;
        String jsonMenu = null;
        String jsonMenuItem = null;
        String sqlMenu = "select from Menu";
        if(host != null) {
            sqlMenu += " where host = ?";
        }
        String sqlMenuItem = "select from MenuItem";
        // assumption here is menuItems are not empty.
        OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sqlMenuItem);
        List<ODocument> menuItems = graph.getRawGraph().command(query).execute(host);
        if(menuItems.size() > 0) {
            jsonMenuItem = OJSONWriter.listToJSON(menuItems, null);
            json = "{\"menuItems\":" + jsonMenuItem;
        }

        query = new OSQLSynchQuery<>(sqlMenu);
        List<ODocument> menus = graph.getRawGraph().command(query).execute(host);
        if(menus.size() > 0) {
            jsonMenu = OJSONWriter.listToJSON(menus, null);
            json += ", \"menus\":" + jsonMenu + "}";
        } else {
            json += "}";
        }
        return json;
    }

    protected void updMenu(Map<String, Object> data) throws Exception {
        String host = (String)data.get("host");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex menu = graph.getVertexByKey("Menu.host", host);
            if(menu != null) {
                Set<String> addMenuItems = (Set)data.get("addMenuItems");
                if(addMenuItems != null) {
                    for(String menuItemId: addMenuItems) {
                        Vertex menuItem = graph.getVertexByKey("MenuItem.menuItemId", menuItemId);
                        menu.addEdge("Own", menuItem);
                    }
                }
                Set<String> delMenuItems = (Set)data.get("delMenuItems");
                if(delMenuItems != null) {
                    for(String menuItemId: delMenuItems) {
                        Vertex menuItem = graph.getVertexByKey("MenuItem.menuItemId", menuItemId);
                        for (Edge edge : (Iterable<Edge>) menu.getEdges(Direction.OUT, "Own")) {
                            if(edge.getVertex(Direction.IN).equals(menuItem)) graph.removeEdge(edge);
                        }
                    }
                }
                menu.setProperty("updateDate", data.get("updateDate"));
            }
            Vertex updateUser = graph.getVertexByKey("User.userId", data.get("updateUserId"));
            updateUser.addEdge("Update", menu);
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
        // remove the cache item in order to reload the menu.
        Map<String, Object> menuMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("menuMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)menuMap.get("cache");
        if(cache != null) {
            cache.remove(data.get("host"));
        }
    }

    protected String getMenuItemMap(OrientGraph graph, String host) throws Exception {
        String sql = "SELECT FROM MenuItem";
        if(host != null) {
            sql += " WHERE host = ? OR host IS NULL";
        }
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (Vertex menuItem : (Iterable<Vertex>) graph.command(new OCommandSQL(sql)).execute()) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("label", (String)menuItem.getProperty("menuItemId"));
            map.put("value", menuItem.getId().toString());
            list.add(map);
        }
        return mapper.writeValueAsString(list);
    }

    protected void updMenuItem(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex menuItem = graph.getVertexByKey("MenuItem.menuItemId", (String)data.get("menuItemId"));
            if(menuItem != null) {
                // handle addMenuItems and delMenuItems
                Set<String> addMenuItems = (Set)data.get("addMenuItems");
                if(addMenuItems != null) {
                    for(String menuItemId: addMenuItems) {
                        Vertex vertex = graph.getVertexByKey("MenuItem.menuItemId", menuItemId);
                        menuItem.addEdge("Own", vertex);
                    }
                }
                Set<String> delMenuItems = (Set)data.get("delMenuItems");
                if(delMenuItems != null) {
                    for(String menuItemId: delMenuItems) {
                        Vertex vertex = graph.getVertexByKey("MenuItem.menuItemId", menuItemId);
                        for (Edge edge : (Iterable<Edge>) menuItem.getEdges(Direction.OUT, "Own")) {
                            if(edge.getVertex(Direction.IN).equals(vertex)) graph.removeEdge(edge);
                        }
                    }
                }
                String path = (String)data.get("path");
                if(path != null && !path.equals(menuItem.getProperty("path"))) {
                    menuItem.setProperty("path", path);
                }
                String tpl = (String)data.get("tpl");
                if(tpl != null && !tpl.equals(menuItem.getProperty("tpl"))) {
                    menuItem.setProperty("tpl", tpl);
                }
                String ctrl = (String)data.get("ctrl");
                if(ctrl != null && !ctrl.equals(menuItem.getProperty("ctrl"))) {
                    menuItem.setProperty("ctrl", ctrl);
                }
                Boolean left = (Boolean)data.get("left");
                if(left != null && !left.equals(menuItem.getProperty("left"))) {
                    menuItem.setProperty("left", left);
                }
                List roles = (List)data.get("roles");
                if(roles != null) {
                    menuItem.setProperty("roles", roles);
                } else {
                    menuItem.setProperty("roles", new ArrayList());
                }
                menuItem.setProperty("updateDate", data.get("updateDate"));
            }
            Vertex updateUser = graph.getVertexByKey("User.userId", data.get("updateUserId"));
            updateUser.addEdge("Update", menuItem);
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
    }
}
