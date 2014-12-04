package com.networknt.light.rule.rule;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 08/10/14.
 */
public class UpdRuleEvRule extends AbstractRuleRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        updRule(data, (String)user.get("@rid"), (String)user.get("userId"));
        return true;
    }
}
