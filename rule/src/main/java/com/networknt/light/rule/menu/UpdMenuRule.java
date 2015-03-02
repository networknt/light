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

import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.index.OCompositeKey;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.*;

/**
 * Created by husteve on 10/29/2014.
 *
 *
 * AccessLevel R [owner, admin, menuAdmin]
 *
 */
public class UpdMenuRule extends AbstractMenuRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String rid = (String)data.get("@rid");
        String error = null;

        String host = (String)user.get("host");
        if(host != null && !host.equals(data.get("host"))) {
            error = "User can only update menu for host: " + host;
            inputMap.put("responseCode", 403);
        } else {
            OrientGraphNoTx graph = ServiceLocator.getInstance().getNoTxGraph();
            try {
                Vertex menu = DbService.getVertexByRid(graph, rid);
                if(menu == null) {
                    error = "Menu with @rid " + rid + " cannot be found";
                    inputMap.put("responseCode", 404);
                } else {
                    int inputVersion = (int)data.get("@version");
                    int storedVersion = menu.getProperty("@version");
                    if(inputVersion != storedVersion) {
                        inputMap.put("responseCode", 422);
                        error = "Updating version " + inputVersion + " doesn't match stored version " + storedVersion;
                    } else {
                        // need to make sure that all the menuItems exist and convert to id for event replay.
                        // and build addMenuItems and delMenuItems sets to the map.
                        Set<String> inputMenuItems = new HashSet<String>();
                        List<String> menuItems = (List<String>)data.get("menuItems");
                        if(menuItems != null) {
                            for(String menuItemRid: menuItems) {
                                Vertex menuItem = DbService.getVertexByRid(graph, menuItemRid);
                                if(menuItem != null) {
                                    inputMenuItems.add(menuItem.getProperty("menuItemId"));
                                }
                            }
                        }

                        Set<String> storedMenuItems = new HashSet<String>();
                        for (Vertex menuItem : (Iterable<Vertex>) menu.getVertices(Direction.OUT, "Own")) {
                            storedMenuItems.add(menuItem.getProperty("menuItemId"));
                        }
                        Set<String> addMenuItems = new HashSet<String>(inputMenuItems);
                        Set<String> delMenuItems = new HashSet<String>(storedMenuItems);
                        addMenuItems.removeAll(storedMenuItems);
                        delMenuItems.removeAll(inputMenuItems);

                        Map eventMap = getEventMap(inputMap);
                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                        inputMap.put("eventMap", eventMap);
                        if(data.get("host") != null) eventData.put("host", data.get("host"));
                        if(addMenuItems.size() > 0) eventData.put("addMenuItems", addMenuItems);
                        if(delMenuItems.size() > 0) eventData.put("delMenuItems", delMenuItems);
                        eventData.put("updateDate", new java.util.Date());
                        eventData.put("updateUserId", user.get("userId"));
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
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }
}
