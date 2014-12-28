package com.networknt.light.rule.blog;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;

/**
 * Created by husteve on 10/10/2014.
 */
public class UpdBlogRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return updBfn("blog", objects);
    }
}
