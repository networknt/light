package com.networknt.light.rule.blog;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.user.AbstractUserRule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by husteve on 10/8/2014.
 */
public class DelBlogRule extends AbstractBlogRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");

        String rid = (String)data.get("@rid");
        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            // TODO check if the user has permission to do so.

            String json = getJsonByRid(rid);
            if(json == null) {
                error = "Blog with @rid " + rid + " cannot be found";
                inputMap.put("responseCode", 404);
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
