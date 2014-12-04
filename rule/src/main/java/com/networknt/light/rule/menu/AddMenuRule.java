package com.networknt.light.rule.menu;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.blog.AbstractBlogRule;

import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/29/2014.
 */
public class AddMenuRule extends AbstractMenuRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("menuAdmin") && !roles.contains("admin")) {
                error = "Role owner or admin or menuAdmin is required to add menu";
                inputMap.put("responseCode", 401);
            } else {
                String host = (String)user.get("host");
                if(host != null && !host.equals(data.get("host"))) {
                    error = "User can only add menu for host: " + host;
                    inputMap.put("responseCode", 401);
                } else {
                    String json = getMenu((String)data.get("host"));
                    if(json != null) {
                        error = "Menu for the host exists";
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
