package com.networknt.light.rule.comment;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 21/03/15.
 */
public class UpdCommentEvRule extends AbstractCommentRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        updComment(data);
        return true;
    }
}
