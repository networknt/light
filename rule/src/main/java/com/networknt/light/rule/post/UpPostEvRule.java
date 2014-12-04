package com.networknt.light.rule.post;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 28/11/14.
 */
public class UpPostEvRule extends AbstractPostRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        upVotePost(data);
        return true;
    }
}
