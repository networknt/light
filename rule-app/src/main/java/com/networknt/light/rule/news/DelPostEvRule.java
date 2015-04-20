package com.networknt.light.rule.news;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;

/**
 * Created by steve on 21/03/15.
 */
public class DelPostEvRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return delPostEv("news", objects);
    }
}
