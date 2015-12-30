package com.networknt.light.rule.forum;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;

/**
 * Created by steve on 21/03/15.
 */
public class UpdPostEvRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return updPostEv("forum", objects);
    }
}
