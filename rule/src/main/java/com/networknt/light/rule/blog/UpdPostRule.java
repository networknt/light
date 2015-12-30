package com.networknt.light.rule.blog;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;

/**
 * Created by steve on 3/6/2015.
 * Update post in a blog
 *
 * AccessLevel R [owner, admin, blogAdmin, blogUser]
 *
 * blogUser can only update his or her blog
 *
 */
public class UpdPostRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return updPost("blog", objects);
    }
}
