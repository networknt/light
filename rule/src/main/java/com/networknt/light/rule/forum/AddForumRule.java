package com.networknt.light.rule.forum;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.user.AbstractUserRule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.HashUtil;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 26/11/14.
 */
public class AddForumRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return addBfn("forum", objects);
    }
}
