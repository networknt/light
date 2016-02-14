package com.networknt.light.rule.rule;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 14/02/16.
 */
public class UpdReqTransformEvRule extends AbstractRuleRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        updReqTransform(data);
        return true;
    }
}
