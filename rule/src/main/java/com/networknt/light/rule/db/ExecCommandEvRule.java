package com.networknt.light.rule.db;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 11/12/14.
 */
public class ExecCommandEvRule extends AbstractDbRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        execCommand(data, true);
        return true;
    }
}
