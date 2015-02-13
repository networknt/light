package com.networknt.light.rule.access;

import com.networknt.light.rule.Rule;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 01/02/15.
 */
public class GetAllAccessRule extends AbstractAccessRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>) payload.get("user");
        List roles = (List)user.get("roles");
        if(roles.contains("owner") || roles.contains("admin") || roles.contains("ruleAdmin")) {
            String host = (String) user.get("host");
            String hostAccesses = getAccesses(host);
            if(hostAccesses != null) {
                inputMap.put("result", hostAccesses);
                return true;
            } else {
                inputMap.put("result", "No access control can be found.");
                inputMap.put("responseCode", 404);
                return false;
            }
        } else {
            inputMap.put("result", "Permission denied");
            inputMap.put("responseCode", 403);
            return false;
        }
    }
}
