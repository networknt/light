package com.networknt.light.rule.forum;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;

/**
 * Created by hus5 on 3/6/2015.
 *
 * Down vote a forum
 *
 * AccessLevel R [user]
 *
 */
public class DownForumRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return downBranch("forum", objects);
    }
}
