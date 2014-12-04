package com.networknt.light.rule.injector.main.feed;

import com.networknt.light.rule.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 04/10/14.
 */
public class UpdFeedMetaRule extends FeedRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        // make sure payload is not null. If you have payload that means the token is valid and not expired.
        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode",401);
        } else {
            // TODO should I check the number of tags?

        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }
}
