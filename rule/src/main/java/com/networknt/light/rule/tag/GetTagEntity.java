package com.networknt.light.rule.tag;

import com.networknt.light.rule.Rule;

/**
 * Created by steve on 30/01/16.
 *
 * AccessLevel A anybody
 *
 */
public class GetTagEntity extends AbstractTagRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return getTagEntity(objects);
    }
}
