package com.networknt.light.rule.blog;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;

/**
 * Created by steve on 3/6/2015.
 */
public class UpdPostEvRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return updPostEv("blog", objects);
    }
}
