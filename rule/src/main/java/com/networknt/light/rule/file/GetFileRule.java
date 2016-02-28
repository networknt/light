package com.networknt.light.rule.file;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 2/26/2016.
 *
 * AccessLevel R [fileAdmin, admin, owner]
 *
 */
public class GetFileRule extends AbstractFileRule implements Rule {
    public boolean execute(Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        // here we get user's host to decide which domain he/she can access
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String host = (String) user.get("host");

        String files = getFile(host, (String)data.get("path"));
        if(files != null) {
            inputMap.put("result", files);
            return true;
        } else {
            inputMap.put("result", "No files can be found.");
            inputMap.put("responseCode", 404);
            return false;
        }
    }

}
