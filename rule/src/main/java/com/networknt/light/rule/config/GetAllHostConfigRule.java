package com.networknt.light.rule.config;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 28/02/16.
 *
 * AccessLevel R [owner, admin, configAdmin]
 */
public class GetAllHostConfigRule extends AbstractConfigRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String host = (String) data.get("host");
        String hostConfigs = getAllHostConfig(host);
        if(hostConfigs != null) {
            inputMap.put("result", hostConfigs);
            return true;
        } else {
            inputMap.put("result", "No config can be found.");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
