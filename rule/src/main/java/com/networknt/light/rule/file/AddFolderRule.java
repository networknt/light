package com.networknt.light.rule.file;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.config.AbstractConfigRule;

/**
 * Created by steve on 2/26/2016.
 *
 * This rule will create a new folder under domain folder or any specified folder.
 * 
 * AccessLevel R [owner, admin]
 */
public class AddFolderRule extends AbstractFileRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return addFolder(objects);
    }
}
