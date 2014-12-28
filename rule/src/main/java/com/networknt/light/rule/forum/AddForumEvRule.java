package com.networknt.light.rule.forum;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.user.AbstractUserRule;

import java.util.Map;

/**
 * Created by steve on 26/11/14.
 */
public class AddForumEvRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return addBfnEv("forum", objects);
    }
}
