package com.networknt.light.rule.injector.main.feed;

import com.networknt.light.rule.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 04/10/14.
 */
public class UpdFeedMetaEvRule extends FeedRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        // update process type and process subtype
        updFeedMeta(data, user);
        // TODO update tags for the first five

        return true;
    }
}
