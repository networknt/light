package com.networknt.light.rule.comment;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.blog.AbstractBlogRule;

import java.util.Map;

/**
 * Created by steve on 03/12/14.
 */
public class AddCommentEvRule extends AbstractCommentRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        addComment(data);
        return true;
    }
}
