package com.networknt.light.rule.category;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.blog.AbstractBlogRule;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by husteve on 10/14/2014.
 */
public class GetCategoryRule extends AbstractCategoryRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String categoryRid = (String) data.get("@rid");
        ODocument category = getCategoryByRid(categoryRid);
        // convert to json string so that Undertow can send it back to browser.
        inputMap.put("result", category.toJSON());
        return true;
    }
}
