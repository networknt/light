package com.networknt.light.rule.forum;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.user.AbstractUserRule;

import java.util.Map;

/**
 * Created by steve on 26/11/14.
 */
public class AddForumEvRule extends AbstractForumRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        addForum(data);
        return true;
    }
}
