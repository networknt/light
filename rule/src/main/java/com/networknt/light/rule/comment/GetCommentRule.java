package com.networknt.light.rule.comment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.blog.AbstractBlogRule;
import com.networknt.light.util.ServiceLocator;

import java.util.*;

/**
 * Created by steve on 03/12/14.
 */
public class GetCommentRule extends AbstractCommentRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("@class", "Comment");
        long total = getTotal(data, criteria);
        if(total > 0) {
            String json = getComment(data, criteria);
            List<Map<String, Object>> comments
                    = mapper.readValue(json, new TypeReference<List<HashMap<String, Object>>>() {});
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", total);
            result.put("comments", comments);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        } else {
            inputMap.put("error", "No user can be found.");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
