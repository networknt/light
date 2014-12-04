package com.networknt.light.rule.role;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by steve on 21/11/14.
 */
public class GetRoleDropdownRule extends AbstractRoleRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            inputMap.put("result", "Login is required");
            inputMap.put("responseCode", 401);
            return false;
        } else {
            Map<String, Object> user = (Map<String, Object>) payload.get("user");
            String host = (String) user.get("host");
            String hostRoleDropdown = getRoleDropdown(host);
            if(hostRoleDropdown != null) {
                inputMap.put("result", hostRoleDropdown);
                return true;
            } else {
                inputMap.put("result", "No role can be found.");
                inputMap.put("responseCode", 404);
                return false;
            }
        }
    }
}
