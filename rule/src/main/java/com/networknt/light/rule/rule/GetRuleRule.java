package com.networknt.light.rule.rule;

import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.role.AbstractRoleRule;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 08/10/14.
 */
public class GetRuleRule extends AbstractRuleRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>) payload.get("user");
        List roles = (List)user.get("roles");
        if(roles.contains("owner") || roles.contains("admin") || roles.contains("ruleAdmin")) {
            String host = (String) user.get("host");
            String hostRules = getRules(host);
            if(hostRules != null) {
                inputMap.put("result", hostRules);
                return true;
            } else {
                inputMap.put("result", "No rule can be found.");
                inputMap.put("responseCode", 404);
                return false;
            }
        } else {
            inputMap.put("result", "Permission denied");
            inputMap.put("responseCode", 401);
            return false;
        }
    }
}
