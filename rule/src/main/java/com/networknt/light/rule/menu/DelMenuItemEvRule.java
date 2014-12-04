package com.networknt.light.rule.menu;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by husteve on 10/29/2014.
 */
public class DelMenuItemEvRule extends AbstractMenuRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        delMenuItem(data);
        return true;
    }
}
