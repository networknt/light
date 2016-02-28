package com.networknt.light.rule.file;

import com.networknt.light.rule.Rule;

/**
 * Created by steve on 27/02/16.
 *
 * AccessLevel: R [fileAdmin, admin, owner]
 */
public class RenFileRule extends AbstractFileRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return renFile(objects);
    }
}
