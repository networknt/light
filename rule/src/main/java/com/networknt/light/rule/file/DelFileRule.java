package com.networknt.light.rule.file;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 2/26/2016.
 *
 * AccessLevel R [fileAdmin, admin, owner]
 */
public class DelFileRule extends AbstractFileRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return delFile(objects);
    }
}
