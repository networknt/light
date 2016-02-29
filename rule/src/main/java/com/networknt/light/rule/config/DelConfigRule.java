package com.networknt.light.rule.config;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;

/**
 * Created by steve on 2/18/2016.
 *
 * AccessLevel R [owner]
 *
 */
public class DelConfigRule extends AbstractConfigRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return delConfig(objects);
    }
}
