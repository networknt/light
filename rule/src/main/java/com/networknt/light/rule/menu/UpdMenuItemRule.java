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
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/29/2014.
 */
public class UpdMenuItemRule extends AbstractMenuRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        String rid = (String)data.get("@rid");
        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("menuAdmin") && !roles.contains("admin")) {
                error = "Role owner or admin or menuAdmin is required to update menuItem";
                inputMap.put("responseCode", 401);
            } else {
                String host = (String)user.get("host");
                if(host != null && !host.equals(data.get("host"))) {
                    error = "User can only update menuItem for host: " + host;
                    inputMap.put("responseCode", 401);
                } else {
                    ODocument menuItem = DbService.getODocumentByRid(rid);
                    if(menuItem == null) {
                        error = "MenuItem with @rid " + rid + " cannot be found";
                        inputMap.put("responseCode", 404);
                    } else {
                        // need to make sure that all the menuItems exist and convert to id for event replay.
                        List menuItemIds = new ArrayList<String>();
                        List<String> menuItems = (List<String>)data.get("menuItems");
                        if(menuItems != null && menuItems.size() > 0) {
                            for(String menuItemRid: menuItems) {
                                ODocument childItem = DbService.getODocumentByRid(menuItemRid);
                                if(childItem == null) {
                                    error = "MenuItem with @rid " + menuItemRid + " cannot be found";
                                    inputMap.put("responseCode", 404);
                                    break;
                                } else {
                                    menuItemIds.add(childItem.field("id"));
                                }
                            }
                        }
                        Map eventMap = getEventMap(inputMap);
                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                        inputMap.put("eventMap", eventMap);
                        eventData.put("id", menuItem.field("id"));
                        eventData.put("path", data.get("path"));
                        eventData.put("tpl", data.get("tpl"));
                        eventData.put("ctrl", data.get("ctrl"));
                        eventData.put("left", data.get("left"));
                        eventData.put("roles", data.get("roles"));
                        eventData.put("menuItemIds", menuItemIds);
                        // TODO remove host for owner?
                        eventData.put("updateDate", new java.util.Date());
                        eventData.put("updateUserId", user.get("userId"));
                    }
                }
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
