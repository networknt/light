package com.networknt.light.rule.rule;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.role.AbstractRoleRule;

import java.util.Map;

/**
 * Created by steve on 07/11/14.
 */
public class DelRuleEvRule extends AbstractRuleRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        delRule(rid);
        return true;
    }
}
