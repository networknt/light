package com.networknt.light.rule.page;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.form.AbstractFormRule;
import com.networknt.light.util.ServiceLocator;

import java.util.Map;

/**
 * Created by husteve on 10/24/2014.
 */
public class ImpPageRule extends AbstractPageRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map eventMap = getEventMap(inputMap);
            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
            inputMap.put("eventMap", eventMap);
            eventData.putAll(data);
            eventData.put("updateDate", new java.util.Date());
            eventData.put("updateUserId", user.get("userId"));
        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }
}
