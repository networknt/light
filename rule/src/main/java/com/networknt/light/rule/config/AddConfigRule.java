package com.networknt.light.rule.config;

import com.networknt.light.rule.Rule;

/**
 * Created by steve on 2/18/2016.
 *
 * Access Level R [owner, admin, configAdmin]
 *
 */
public class AddConfigRule extends AbstractConfigRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return addConfig(objects);
    }
}
