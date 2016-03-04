package com.networknt.light.rule.rule;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 31/01/15.
 *
 * AccessLevel R [user]
 *
 */
public class GetRuleDropdownRule extends AbstractRuleRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        if(user == null) {
            inputMap.put("result", "Login is required");
            inputMap.put("responseCode", 401);
            return false;
        } else {
            String host = (String) user.get("host");
            String hostRuleDropdown = getRuleDropdown(host);
            if(hostRuleDropdown != null) {
                inputMap.put("result", hostRuleDropdown);
                return true;
            } else {
                inputMap.put("result", "No rule can be found.");
                inputMap.put("responseCode", 404);
                return false;
            }
        }
    }
}
