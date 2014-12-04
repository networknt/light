package com.networknt.light.rule.comment;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.forum.AbstractForumRule;

import java.util.Map;

/**
 * Created by steve on 03/12/14.
 */
public class GetCommentTreeRule extends AbstractCommentRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String comments = getCommentTree(data);
        if(comments != null) {
            inputMap.put("result", comments);
            return true;
        } else {
            inputMap.put("error", "No comment can be found");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
