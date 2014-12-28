package com.networknt.light.rule.forum;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.form.AbstractFormRule;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 26/11/14.
 */
public class GetForumRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return getBfn("forum", objects);
    }
}
