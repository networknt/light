package com.networknt.light.rule.forum;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 02/12/14.
 */
public class AddPostEvRule extends AbstractForumRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        addPost(data);
        return true;
    }
}
