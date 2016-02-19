package com.networknt.light.rule.config;

import com.networknt.light.rule.Rule;

/**
 * Created by steve on 2/18/2016.
 *
 * AccessLevel R [owner, admin, configAdmin]
 *
 */
public class UpdConfigRule extends AbstractConfigRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return updConfig(objects);
    }

}
