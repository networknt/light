package com.networknt.light.rule.access;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.rule.AbstractRuleRule;

import java.util.Map;

/**
 * Created by steve on 30/01/15.
 */
public class DelAccessEvRule extends AbstractAccessRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        delAccess(data);
        return true;
    }
}
