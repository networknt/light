package com.networknt.light.rule.injector.main.feed;

import com.networknt.light.rule.Rule;

import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/3/2014.
 */
public class InjAllFeedEvRule extends FeedRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        List<Map<String, Object>> feeds = (List<Map<String, Object>>)data.get("feeds");
        for (Map<String, Object> feed : feeds) {
            addFeed(feed, user);
        }
        return true;
    }
}
