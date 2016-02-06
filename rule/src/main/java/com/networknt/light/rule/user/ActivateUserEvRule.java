package com.networknt.light.rule.user;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 06/02/16.
 */
public class ActivateUserEvRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        activateUser(data);
        return true;
    }

}
