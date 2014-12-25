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
public class UpdMenuRule extends AbstractMenuRule implements Rule {
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
                error = "Role owner or admin or menuAdmin is required to update menu";
                inputMap.put("responseCode", 401);
            } else {
                String host = (String)user.get("host");
                if(host != null && !host.equals(data.get("host"))) {
                    error = "User can only update menu for host: " + host;
                    inputMap.put("responseCode", 401);
                } else {
                    ODocument menu = DbService.getODocumentByRid(rid);
                    if(menu == null) {
                        error = "Menu with @rid " + rid + " cannot be found";
                        inputMap.put("responseCode", 404);
                    } else {
                        int inputVersion = (int)data.get("@version");
                        int storedVersion = menu.field("@version");
                        if(inputVersion != storedVersion) {
                            inputMap.put("responseCode", 400);
                            error = "Updating version " + inputVersion + " doesn't match stored version " + storedVersion;
                        } else {
                            // need to make sure that all the menuItems exist and convert to id for event replay.
                            List menuItemIds = new ArrayList<String>();
                            List<String> menuItems = (List<String>)data.get("menuItems");
                            for(String menuItemRid: menuItems) {
                                ODocument menuItem = DbService.getODocumentByRid(menuItemRid);
                                if(menuItem == null) {
                                    error = "MenuItem with @rid " + menuItemRid + " cannot be found";
                                    inputMap.put("responseCode", 404);
                                    break;
                                } else {
                                    menuItemIds.add(menuItem.field("id"));
                                }
                            }
                            Map eventMap = getEventMap(inputMap);
                            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                            inputMap.put("eventMap", eventMap);
                            eventData.put("host", data.get("host"));
                            eventData.put("menuItemIds", menuItemIds);
                            eventData.put("updateDate", new java.util.Date());
                            eventData.put("updateUserId", user.get("userId"));
                        }
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
