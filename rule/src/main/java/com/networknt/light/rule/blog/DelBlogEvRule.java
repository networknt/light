package com.networknt.light.rule.blog;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.user.AbstractUserRule;
import com.networknt.light.util.ServiceLocator;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by husteve on 10/8/2014.
 */
public class DelBlogEvRule extends AbstractBlogRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String blogRid = (String) data.get("@rid");
        String host = (String) data.get("host");
        delBlogUpdCache(blogRid, host);
        return true;
    }
}
