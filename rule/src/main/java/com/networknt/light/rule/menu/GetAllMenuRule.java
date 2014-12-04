package com.networknt.light.rule.menu;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.form.AbstractFormRule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 29/10/14.
 */
public class GetAllMenuRule extends AbstractMenuRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>) payload.get("user");
        List roles = (List)user.get("roles");
        if(roles.contains("owner") || roles.contains("menuAdmin") || roles.contains("admin")) {
            String host = (String) user.get("host");
            String menus = getAllMenu(host);
            if(menus != null) {
                inputMap.put("result", menus);
                return true;
            } else {
                inputMap.put("result", "No menu can be found.");
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
