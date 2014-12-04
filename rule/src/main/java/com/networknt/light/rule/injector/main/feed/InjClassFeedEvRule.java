package com.networknt.light.rule.injector.main.feed;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by husteve on 9/5/2014.
 */
public class InjClassFeedEvRule extends ClassFeedRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        addFeed(data, user);
        return true;
    }
}
