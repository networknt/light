package com.networknt.light.rule.db;

import com.networknt.light.rule.Rule;

import java.io.File;
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
                error = "Role owner or admin or dbAdmin is required to add schema";
                inputMap.put("responseCode", 401);
            } else {

                String path = (String)data.get("path");
                if(path != null) {
                    // make sure that the Path exists.
                    File file = new File(path);
                    if (file.exists() && file.isDirectory()) {
                        String result = exportEvent(path);
                        inputMap.put("result", result);
                    } else {
                        error = "Path must exist and is a folder";
                        inputMap.put("responseCode", 400);
                    }
                } else {
                    error = "Path is required";
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
