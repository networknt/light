package com.networknt.light.rule.forum;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 01/12/14.
 */
public class GetForumPostRule extends AbstractForumRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        if(data.get("id") == null || data.get("host") == null) {
            inputMap.put("error", "Host and Id are required");
            inputMap.put("responseCode", 400);
            return false;
        }
        String posts = getForumPost(data);
        if(posts != null) {
            inputMap.put("result", posts);
            return true;
        } else {
            inputMap.put("error", "No post can be found");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
