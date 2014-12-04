package com.networknt.light.rule.category;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.blog.AbstractBlogRule;

import java.util.Map;

/**
 * Created by husteve on 10/14/2014.
 */
public class DelCategoryEvRule extends AbstractCategoryRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String categoryRid = (String) data.get("@rid");
        delCategory(categoryRid);
        return true;
    }
}
