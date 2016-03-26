package com.networknt.light.rule.comment;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 26/03/16.
 */
public class SpmCommentEvRule extends AbstractCommentRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        spmComment(data);
        return true;
    }
}
