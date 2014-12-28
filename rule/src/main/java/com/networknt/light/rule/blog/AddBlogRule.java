package com.networknt.light.rule.blog;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;

/**
 * Created by steve on 08/10/14.
 */
public class AddBlogRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return addBfn("blog", objects);
    }
}
