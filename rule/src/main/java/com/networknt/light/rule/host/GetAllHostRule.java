package com.networknt.light.rule.host;

import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Steve Hu on 2015-01-19.
 */
public class GetAllHostRule extends AbstractHostRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>) payload.get("user");
        List roles = (List)user.get("roles");
        if(roles.contains("owner")) {
            // flatten the set to array with all the elements of the host.
            List hosts = new ArrayList<Map<String, Object>>();
            Map hostMap = ServiceLocator.getInstance().getHostMap();
            Set<String> keys = hostMap.keySet();
            for(String key : keys) {
                Map valueMap = (Map<String, Object>)hostMap.get(key);
                valueMap.put("id", key);
                hosts.add(valueMap);
            }
            inputMap.put("result", mapper.writeValueAsString(hosts));
            return true;
        } else {
            inputMap.put("result", "Permission denied");
            inputMap.put("responseCode", 403);
            return false;
        }
    }
}
