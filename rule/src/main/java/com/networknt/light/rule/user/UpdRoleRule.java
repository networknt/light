package com.networknt.light.rule.user;

import com.networknt.light.rule.Rule;

import java.util.List;
import java.util.Map;

/**
 * Created by Steve Hu on 2015-01-19.
 *
 * Update role for a user.
 *
 * AccessLevel R [owner, admin, userAdmin]
 *
 */
public class UpdRoleRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        String error = null;
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

        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            return true;
        }
    }
}
