package com.networknt.light.rule.blog;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;

/**
 * Created by husteve on 10/8/2014.
 */
public class DelBlogRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return delBfn("blog", objects);
    }
}
