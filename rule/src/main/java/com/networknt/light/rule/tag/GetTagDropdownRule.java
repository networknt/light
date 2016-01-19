package com.networknt.light.rule.tag;

import com.networknt.light.rule.Rule;

/**
 * Created by steve on 17/01/16.
 */
public class GetTagDropdownRule extends AbstractTagRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return getTagDropdown(objects);
    }

}
