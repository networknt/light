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
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.index.OCompositeKey;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.serialization.serializer.OJSONWriter;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 10/27/2014.
 * The menuMap.cache has three type of keys. host, label and @rid
 */
public abstract class AbstractMenuRule extends AbstractRule implements Rule {
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
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        String host = (String)data.get("host");
        try {
            db.begin();
            OIndex<?> menuHostIdx = db.getMetadata().getIndexManager().getIndex("Menu.host");
            OIdentifiable oidMenu = (OIdentifiable) menuHostIdx.get(host);
            if (oidMenu != null) {
                ODocument menu = (ODocument)oidMenu.getRecord();
                //  cascade deleting all menuItems belong to the host only.
                List<ODocument> menuItems = menu.field("menuItems");
                if(menuItems != null) {
                    for(ODocument menuItem: menuItems) {
                        if(menuItem != null && menuItem.field("host") != null) {
                            db.delete(menuItem);
                        }
                    }
                }
                db.delete(menu);
                db.commit();
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        Map<String, Object> menuMap = ServiceLocator.getInstance().getMemoryImage("menuMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)menuMap.get("cache");
        if(cache != null) {
            cache.remove(host);
        }
    }

    protected void delMenuItem(Map<String, Object> data) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OIndex<?> menuItemIdIdx = db.getMetadata().getIndexManager().getIndex("MenuItem.id");
            OIdentifiable oidMenuItem = (OIdentifiable) menuItemIdIdx.get(data.get("id"));
            if (oidMenuItem != null) {
                db.begin();
                ODocument menuItem = (ODocument)oidMenuItem.getRecord();
                db.delete(menuItem);
                db.commit();
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        // no need to refresh cache as there is no reference to this menuItem anywhere.
    }

    protected String addMenu(Map<String, Object> data) throws Exception {
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            ODocument menu = new ODocument(schema.getClass("Menu"));
            menu.field("host", data.get("host"));
            menu.field("createDate", data.get("createDate"));
            menu.field("createUserId", data.get("createUserId"));
            List<String> menuItemIds = (List<String>)data.get("menuItems");
            if(menuItemIds != null && menuItemIds.size() > 0) {
                List menuItems = new ArrayList<ODocument>();
                OIndex<?> menuItemIdIdx = db.getMetadata().getIndexManager().getIndex("MenuItem.id");
                for(String menuItemId: menuItemIds) {
                    // this is a unique index, so it retrieves a OIdentifiable
                    OIdentifiable oid = (OIdentifiable) menuItemIdIdx.get(menuItemId);
                    if (oid != null) {
                        ODocument menuItem = (ODocument)oid.getRecord();
                        menuItems.add(menuItem);
                    }
                }
                menu.field("menuItems", menuItems);
            }
            menu.save();
            db.commit();
            Map<String, Object> menuMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("menuMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)menuMap.get("cache");
            if(cache == null) {
                cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                        .maximumWeightedCapacity(100)
                        .build();
                menuMap.put("cache", cache);
            }
            json = menu.toJSON("fetchPlan:*:2");
            cache.put(data.get("host"), json);
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return json;
    }

    protected String addMenuItem(Map<String, Object> data) throws Exception {
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        OSchema schema = db.getMetadata().getSchema();
        try {
            db.begin();
            ODocument menuItem = new ODocument(schema.getClass("MenuItem"));
            menuItem.field("id", data.get("id"));
            menuItem.field("label", data.get("label"));
            menuItem.field("host", data.get("host"));
            menuItem.field("path", data.get("path"));
            menuItem.field("tpl", data.get("tpl"));
            menuItem.field("ctrl", data.get("ctrl"));
            menuItem.field("left", data.get("left"));
            menuItem.field("roles", data.get("roles"));
            menuItem.field("createDate", data.get("createUser"));
            menuItem.field("createUserId", data.get("createUserId"));
            menuItem.save();
            db.commit();
            json = menuItem.toJSON();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return json;
    }

    protected String getMenu(String host) {
        String json = null;
        Map<String, Object> menuMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("menuMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)menuMap.get("cache");
        if(cache != null) {
            json = (String)cache.get(host);
        }
        if(json == null) {
            ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
            try {
                OIndex<?> menuHostIdx = db.getMetadata().getIndexManager().getIndex("Menu.host");
                // this is a unique index, so it retrieves a OIdentifiable
                OIdentifiable oid = (OIdentifiable) menuHostIdx.get(host);
                if (oid != null) {
                    ODocument doc = (ODocument)oid.getRecord();
                    json = doc.toJSON("fetchPlan:*:2");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                db.close();
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

    protected String getMenuItem(String id) {
        String json = null;
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OIndex<?> menuItemIdIdx = db.getMetadata().getIndexManager().getIndex("MenuItem.id");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable oid = (OIdentifiable) menuItemIdIdx.get(id);
            if (oid != null) {
                ODocument doc = (ODocument)oid.getRecord();
                json = doc.toJSON();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
        return json;
    }

    protected String getAllMenu(String host) {
        String json = null;

        String jsonMenu = null;
        String jsonMenuItem = null;
        String sqlMenu = "select from Menu";
        if(host != null) {
            sqlMenu += " where host = ?";
        }
        String sqlMenuItem = "select from MenuItem";

        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            // assumption here is menuItems are not empty.
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>(sqlMenuItem);
            List<ODocument> menuItems = db.command(query).execute(host);
            if(menuItems.size() > 0) {
                jsonMenuItem = OJSONWriter.listToJSON(menuItems, null);
                json = "{\"menuItems\":" + jsonMenuItem;
            }

            query = new OSQLSynchQuery<>(sqlMenu);
            List<ODocument> menus = db.command(query).execute(host);
            if(menus.size() > 0) {
                jsonMenu = OJSONWriter.listToJSON(menus, null);
                json += ", \"menus\":" + jsonMenu + "}";
            } else {
                json += "}";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return json;
    }

    protected void updMenu(Map<String, Object> data) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        String host = (String)data.get("host");
        try {
            db.begin();
            OIndex<?> menuHostIdx = db.getMetadata().getIndexManager().getIndex("Menu.host");
            // this is a unique index, so it retrieves a OIdentifiable
            OIdentifiable oidMenu = (OIdentifiable) menuHostIdx.get(host);
            if (oidMenu != null) {
                ODocument menu = (ODocument) oidMenu.getRecord();
                // update menuItems.
                List inputItemIds = (List)data.get("menuItemIds");
                if(inputItemIds != null && inputItemIds.size() > 0) {
                    OIndex<?> menuItemIdIdx = db.getMetadata().getIndexManager().getIndex("MenuItem.id");
                    List menuItems = new ArrayList();
                    for(Object obj : inputItemIds) {
                        if(obj != null) {
                            String id = (String)obj;
                            OIdentifiable oidMenuItem = (OIdentifiable) menuItemIdIdx.get(id);
                            if(oidMenuItem != null) {
                                ODocument menuItem = (ODocument)oidMenuItem.getRecord();
                                menuItems.add(menuItem);
                            }
                        }
                    }
                    menu.field("menuItems", menuItems);
                } else {
                    // this is to remove the existing menuItem if there are any.
                    menu.removeField("menuItems");
                }
                menu.field("updateDate", data.get("updateDate"));
                menu.field("updateUserId", data.get("updateUserId"));
                menu.save();
                db.commit();
            }
            // remove the cache item in order to reload the menu.
            Map<String, Object> menuMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("menuMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)menuMap.get("cache");
            if(cache != null) {
                cache.remove(data.get("host"));
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
    }

    protected String getAllMenuItem(String host) {
        String json = null;
        String sql = "SELECT FROM MenuItem";
        if(host != null) {
            sql += " WHERE host = ? OR host IS NULL";
        }
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        try {
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
            List<ODocument> menuItems = db.command(query).execute(host);
            if(menuItems.size() > 0) {
                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                for(ODocument doc: menuItems) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("label", doc.field("id"));
                    map.put("value", doc.field("@rid").toString());
                    list.add(map);
                }
                json = mapper.writeValueAsString(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return json;
    }

    protected void updMenuItem(Map<String, Object> data) throws Exception {
        ODatabaseDocumentTx db = ServiceLocator.getInstance().getDb();
        String id = (String)data.get("id");
        try {
            db.begin();
            OIndex<?> menuItemIdIdx = db.getMetadata().getIndexManager().getIndex("MenuItem.id");
            OIdentifiable oidMenuItem = (OIdentifiable) menuItemIdIdx.get(id);
            if(oidMenuItem != null) {
                ODocument menuItem = oidMenuItem.getRecord();
                List inputItemIds = (List)data.get("menuItemIds");
                if(inputItemIds != null && inputItemIds.size() > 0) {
                    List menuItems = new ArrayList();
                    for(Object obj : inputItemIds) {
                        if(obj != null) {
                            String menuItemId = (String)obj;
                            OIdentifiable oid = (OIdentifiable) menuItemIdIdx.get(menuItemId);
                            if(oid != null) {
                                ODocument item = (ODocument)oid.getRecord();
                                menuItems.add(item);
                            }
                        }
                    }
                    menuItem.field("menuItems", menuItems);
                } else {
                    // this is to remove the existing menuItem if there are any.
                    menuItem.removeField("menuItems");
                }
                String path = (String)data.get("path");
                if(path != null && !path.equals(menuItem.field("path"))) {
                    menuItem.field("path", path);
                }
                String tpl = (String)data.get("tpl");
                if(tpl != null && !tpl.equals(menuItem.field("tpl"))) {
                    menuItem.field("tpl", tpl);
                }
                String ctrl = (String)data.get("ctrl");
                if(ctrl != null && !ctrl.equals(menuItem.field("ctrl"))) {
                    menuItem.field("ctrl", ctrl);
                }
                Boolean left = (Boolean)data.get("left");
                if(left != null && !left.equals(menuItem.field("left"))) {
                    menuItem.field("left", left);
                }
                List roles = (List)data.get("roles");
                if(roles != null) {
                    menuItem.field("roles", roles);
                } else {
                    menuItem.field("roles", new ArrayList());
                }
                menuItem.field("updateDate", data.get("updateDate"));
                menuItem.field("updateUserId", data.get("updateUserId"));
                menuItem.save();
                db.commit();
            }
            // remove the cache item in order to reload the menu.
            Map<String, Object> menuMap = (Map<String, Object>)ServiceLocator.getInstance().getMemoryImage("menuMap");
            ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)menuMap.get("cache");
            if(cache != null) {
                if(data.get("host") != null) {
                    cache.remove(data.get("host"));
                } else {
                    // A common menuItem has been updated, remove all hosts in cache
                    cache.clear();
                }
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            db.close();
        }
    }

}
