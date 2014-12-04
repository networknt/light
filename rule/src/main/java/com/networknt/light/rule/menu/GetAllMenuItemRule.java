package com.networknt.light.rule.menu;

import com.networknt.light.rule.Rule;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 13/11/14.
 */
public class GetAllMenuItemRule extends AbstractMenuRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>) payload.get("user");
        List roles = (List)user.get("roles");
        if(roles.contains("owner") || roles.contains("menuAdmin") || roles.contains("admin")) {
            String host = (String) user.get("host");
            String menuItems = getAllMenuItem(host);
            if(menuItems != null) {
                inputMap.put("result", menuItems);
                return true;
            } else {
                inputMap.put("result", "No menuItem can be found.");
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
