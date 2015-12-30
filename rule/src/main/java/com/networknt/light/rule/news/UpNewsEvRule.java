package com.networknt.light.rule.news;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;

/**
 * Created by hus5 on 3/6/2015.
 */
public class UpNewsEvRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return upBranchEv("news", objects);
    }
}
