package com.networknt.light.rule.forum;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;

/**
 * Created by hus5 on 3/6/2015.
 *
 * Up vote forum by users
 *
 * AccessLevel R [user]
 *
 */
public class UpForumRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return upBranch("forum", objects);
    }
}
