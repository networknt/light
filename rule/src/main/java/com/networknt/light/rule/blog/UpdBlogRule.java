package com.networknt.light.rule.blog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/10/2014.
 */
public class UpdBlogRule extends AbstractBlogRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        int inputVersion = (int)data.get("@version");
        String blogRid = (String)data.get("@rid");
        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode",401);
        } else {
            Map<String,Object> user = (Map<String, Object>)payload.get("user");
            String userRid = (String)user.get("@rid");

            String json = getJsonByRid(blogRid);
            if(json == null) {
                error = "Blog with @rid " + blogRid + " cannot be found";
                inputMap.put("responseCode", 404);
            } else {
                Map<String, Object> blog = mapper.readValue(json,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                int storedVersion = (int)blog.get("@version");
                if(inputVersion != storedVersion) {
                    error = "Updating version " + inputVersion + " doesn't match stored version " + storedVersion;
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
