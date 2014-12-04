package com.networknt.light.rule.user;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.blog.AbstractBlogRule;

import java.util.Map;

/**
 * Created by husteve on 10/17/2014.
 */
public class UpUserEvRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        upVoteUser(data);
        // TODO refresh hot user list and cold user list
        return true;
    }
}
