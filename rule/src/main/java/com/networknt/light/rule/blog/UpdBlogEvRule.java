package com.networknt.light.rule.blog;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by husteve on 10/10/2014.
 */
public class UpdBlogEvRule extends AbstractBlogRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String,Object> user = (Map<String, Object>)payload.get("user");
        String userRid = (String)user.get("@rid");
        String userId = (String)user.get("userId");
        updBlog(data, userRid, userId);
        return true;
    }
}
