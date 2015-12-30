package com.networknt.light.rule.forum;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;

/**
 * Created by hus5 on 3/6/2015.
 */
public class DownForumEvRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return downBranchEv("forum", objects);
    }
}
