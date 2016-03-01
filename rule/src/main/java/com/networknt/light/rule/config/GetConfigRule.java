package com.networknt.light.rule.config;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 2/18/2016.
 *
 * The lookup sequence is host then system.
 *
 * In some cases, the config values need to be accessed in the dropdown list. so the
 * access level is A (anyone)
 *
 * AccessLevel: A
 */
public class GetConfigRule extends AbstractConfigRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String host = (String)data.get("host");
        String configId = (String)data.get("configId");
        String jsonPath = (String)data.get("jsonPath");
        String json = getConfig(host, configId, jsonPath);
        if(json != null) {
            inputMap.put("result", json);
            return true;
        } else {
            inputMap.put("result", "Config with " + configId + " cannot be found on host " + host);
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
