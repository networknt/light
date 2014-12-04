package com.networknt.light.rule.role;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.menu.AbstractMenuRule;

import java.util.Map;

/**
 * Created by husteve on 10/31/2014.
 */
public class UpdRoleEvRule extends AbstractRoleRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        updRole(data);
        return true;
    }
}
