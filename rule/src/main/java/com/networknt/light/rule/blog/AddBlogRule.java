package com.networknt.light.rule.blog;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.form.AbstractFormRule;
import com.networknt.light.server.DbService;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 08/10/14.
 */
public class AddBlogRule extends AbstractBlogRule implements Rule {

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String host = (String)data.get("host");
        String id = (String)data.get("id");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains("blogAdmin")) {
                error = "Role owner or admin or blogAdmin is required to add blog";
                inputMap.put("responseCode", 401);
            } else {
                String userHost = (String)user.get("host");
                if(userHost != null && !userHost.equals(host)) {
                    error = "User can only add blog from host: " + host;
                    inputMap.put("responseCode", 401);
                } else {
                    ODocument blog = getODocumentByHostId("blogHostIdIdx", host, id);
                    if(blog == null) {
                        Map eventMap = getEventMap(inputMap);
                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                        inputMap.put("eventMap", eventMap);
                        eventData.putAll((Map<String, Object>)inputMap.get("data"));
                        eventData.put("createDate", new java.util.Date());
                        eventData.put("createUserId", user.get("userId"));
                    } else {
                        error = "Blog with id " + id + " exists on host " + host;
                        inputMap.put("responseCode", 400);
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
