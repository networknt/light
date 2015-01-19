package com.networknt.light.rule.user;

import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.List;
import java.util.Map;

/**
 * Created by Steve Hu on 2015-01-19.
 */
public class UpdRoleRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            // TODO only owner and admin can do it?
            // owner can add any role and admin can only add role belong to that host.
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            // TODO put this in config.
            if(!roles.contains("owner") && !roles.contains("admin")) {
                error = "Role owner or admin is required to update user role";
                inputMap.put("responseCode", 403);
            } else {
                // TODO make sure at least "user" is in the role array.

                String userHost = (String) user.get("host");
                if (userHost != null) {
                    // this is admin role to update user role
                    if(!userHost.equals(data.get("host"))) {
                        error = "admin can only update user role from host: " + userHost;
                        inputMap.put("responseCode", 403);
                    } else {
                        // TODO make sure all the newly added roles belongs to the host
                    }
                } else {
                    // this is owner update user role
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("userId", data.get("userId"));
                    eventData.put("roles", data.get("roles"));
                    eventData.put("updateUserId", user.get("userId"));
                    eventData.put("updateDate", new java.util.Date());
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
