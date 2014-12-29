package com.networknt.light.rule.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.RuleEngine;
import com.networknt.light.util.Util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 14/12/14.
 */
public class ReplayEventRule extends AbstractDbRule implements Rule {

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains("dbAdmin")) {
                error = "Role owner or admin or dbAdmin is required to replay events";
                inputMap.put("responseCode", 403);
            } else {
                String content = (String)data.get("content");
                // content may contains several events, parse it.
                List<Map<String, Object>> events = mapper.readValue(content,
                    new TypeReference<List<HashMap<String, Object>>>() {});

                // replay event one by one.
                for(Map<String, Object> event: events) {
                    RuleEngine.getInstance().executeRule(Util.getEventRuleId(event), event);
                }
            }
        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }
}
