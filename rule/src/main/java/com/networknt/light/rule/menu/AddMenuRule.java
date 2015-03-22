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
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/29/2014.
 *
 * This is the REST API endpoint to add a menu for a host.
 *
 * AccessLevel R [owner]
 */
public class AddMenuRule extends AbstractMenuRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String error = null;
        String host = (String)data.get("host");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            String json = getMenu(graph, (String)data.get("host"));
            if(json != null) {
                error = "Menu for the host exists";
                inputMap.put("responseCode", 400);
            } else {
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.put("host", host);
                eventData.put("createDate", new java.util.Date());
                eventData.put("createUserId", user.get("userId"));

                // make sure all menuItems exist if there are any.
                List<String> menuItems = (List<String>)data.get("menuItems");
                if(menuItems != null && menuItems.size() > 0) {
                    List<String> addMenuItems = new ArrayList<String>();
                    for(String menuItemRid: menuItems) {
                        Vertex menuItem = DbService.getVertexByRid(graph, menuItemRid);
                        if(menuItem == null) {
                            error = "MenuItem with @rid " + menuItemRid + " cannot be found.";
                            inputMap.put("responseCode", 404);
                            break;
                        } else {
                            addMenuItems.add((String)menuItem.getProperty("menuItemId"));
                        }
                    }
                    eventData.put("addMenuItems", addMenuItems);
                }
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
            return true;
        }
    }
}
