package com.networknt.light.rule.host;

import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;

import java.util.List;
import java.util.Map;

/**
 * Created by Steve Hu on 2015-01-19.
 */
public class DelHostRule extends AbstractHostRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner")) {
                error = "Role owner is required to delete host";
                inputMap.put("responseCode", 403);
            } else {
                // check if the host exists or not.
                Map<String, Object> hostMap = ServiceLocator.getInstance().getHostMap();
                if(hostMap.containsKey(data.get("id"))) {
                    // host exists
                    // TODO add host into virtualhost.json here in the command or in event?
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("id", data.get("id"));
                    eventData.put("updateDate", new java.util.Date());
                    eventData.put("updateUserId", user.get("userId"));
                } else {
                    error = "Id for the host does not exist";
                    inputMap.put("responseCode", 400);
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
