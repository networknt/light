package com.networknt.light.rule.forum;

import com.networknt.light.rule.Rule;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 28/11/14.
 */
public class GetForumDropdownRule extends AbstractForumRule implements Rule {
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
            String forums = getForumDropdown(host);
            if(forums != null) {
                inputMap.put("result", forums);
                return true;
            } else {
                inputMap.put("error", "No forum can be found");
                inputMap.put("responseCode", 404);
                return false;
            }
        }
    }
}
