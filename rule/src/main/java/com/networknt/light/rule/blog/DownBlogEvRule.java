package com.networknt.light.rule.blog;

import com.networknt.light.rule.Rule;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Map;

/**
 * Created by steve on 11/10/14.
 */
public class DownBlogEvRule extends AbstractBlogRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String,Object> user = (Map<String, Object>)payload.get("user");
        String userRid = (String)user.get("@rid");
        String blogRid = (String) data.get("@rid");
        ODocument blog = downVoteBlog(blogRid, userRid);
        // TODO need to refresh hot list only here.

        return true;
    }
}
