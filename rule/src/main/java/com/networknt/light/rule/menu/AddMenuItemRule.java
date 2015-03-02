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
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/29/2014.
 * AccessLevel R [owner, admin, menuAdmin]
 */
public class AddMenuItemRule extends AbstractMenuRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String error = null;
        String host = (String)user.get("host");
        if(host != null && !host.equals(data.get("host"))) {
            error = "You can only add menuItem for host: " + host;
            inputMap.put("responseCode", 403);
        } else {
            OrientGraphNoTx graph = ServiceLocator.getInstance().getNoTxGraph();
            try {
                String json = getMenuItem(graph, (String) data.get("menuItemId"));
                if(json != null) {
                    error = "MenuItem for the label exists";
                    inputMap.put("responseCode", 400);
                } else {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    List<String> menuItems = (List)data.remove("menuItems");
                    // convert to menuItemIds from rids
                    if(menuItems != null && menuItems.size() > 0) {
                        List<String> addMenuItems = new ArrayList();
                        for(String rid: menuItems) {
                            Vertex menuItem = DbService.getVertexByRid(graph, rid);
                            addMenuItems.add(menuItem.getProperty("menuItemId"));
                        }
                        data.put("addMenuItems", addMenuItems);
                    }
                    eventData.putAll((Map<String, Object>) inputMap.get("data"));
                    eventData.put("createDate", new java.util.Date());
                    eventData.put("createUserId", user.get("userId"));
                    if(host == null) {
                        eventData.remove("host");
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
