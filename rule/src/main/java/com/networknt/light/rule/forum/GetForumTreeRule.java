package com.networknt.light.rule.forum;

import com.networknt.light.rule.Rule;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 28/11/14.
 */
public class GetForumTreeRule extends AbstractForumRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String host = (String)data.get("host");
        String forums = getForumTree(host);
        if(forums != null) {
            inputMap.put("result", forums);
            return true;
        } else {
            inputMap.put("error", "No forum can be found");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
