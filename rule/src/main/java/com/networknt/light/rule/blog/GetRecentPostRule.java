package com.networknt.light.rule.blog;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;

/**
 * Created by steve on 18/01/16.
 *
 * AccessLevel A everyone can access
 *
 * This API get all the post regardless category order by the time of post.
 *
 */
public class GetRecentPostRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return getBfnRecentPost("Blog", objects);
    }
}
