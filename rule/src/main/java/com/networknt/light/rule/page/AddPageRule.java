package com.networknt.light.rule.page;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.form.AbstractFormRule;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/24/2014.
 */
public class AddPageRule extends AbstractPageRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String id = (String)data.get("id");
        String host = (String)data.get("host");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains("pageAdmin")) {
                error = "Role owner or admin or pageAdmin is required to add page";
                inputMap.put("responseCode", 401);
            } else {
                String userHost = (String)user.get("host");
                if(userHost != null) {
                    if(!userHost.equals(host)) {
                        error = "User can only add page from host: " + host;
                        inputMap.put("responseCode", 401);
                    } else {
                        String json = getPageById(id);
                        if(json != null) {
                            error = "Page with the same id exists";
                            inputMap.put("responseCode", 400);
                        } else {
                            Map eventMap = getEventMap(inputMap);
                            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                            inputMap.put("eventMap", eventMap);
                            eventData.putAll((Map<String, Object>)inputMap.get("data"));
                            eventData.put("createDate", new java.util.Date());
                            eventData.put("createUserId", user.get("userId"));
                        }
                    }
                } else {
                    String json = getPageById(id);
                    if(json != null) {
                        error = "Page with the same id exists";
                        inputMap.put("responseCode", 400);
                    } else {
                        Map eventMap = getEventMap(inputMap);
                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                        inputMap.put("eventMap", eventMap);
                        eventData.putAll((Map<String, Object>)inputMap.get("data"));
                        eventData.put("createDate", new java.util.Date());
                        eventData.put("createUserId", user.get("userId"));
                        // remove host from data as this is owner adding role
                        eventData.remove("host");
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
