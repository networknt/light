package com.networknt.light.rule.forum;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.form.AbstractFormRule;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 26/11/14.
 */
public class GetForumRule extends AbstractForumRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        String host = (String)data.get("host");
        if(payload == null) {
            inputMap.put("error", "Login is required");
            inputMap.put("responseCode", 401);
            return false;
        } else {
            Map<String, Object> user = (Map<String, Object>) payload.get("user");
            List roles = (List)user.get("roles");
            if(roles.contains("owner") || roles.contains("admin") || roles.contains("forumAdmin")) {
                Object userHost = user.get("host");
                if(userHost != null && !userHost.equals(host)) {
                    inputMap.put("error", "User can only get forum from host: " + host);
                    inputMap.put("responseCode", 401);
                    return false;
                } else {
                    String forums = getForum(host);
                    if(forums != null) {
                        inputMap.put("result", forums);
                        return true;
                    } else {
                        inputMap.put("error", "No forum can be found");
                        inputMap.put("responseCode", 404);
                        return false;
                    }
                }
            } else {
                inputMap.put("error", "Role owner or admin or forumAdmin is required to get all forums");
                inputMap.put("responseCode", 401);
                return false;
            }
        }
    }
}
