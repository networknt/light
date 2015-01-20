package com.networknt.light.rule.log;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.db.AbstractDbRule;

import java.util.Map;

/**
 * Created by admin on 2015-01-20.
 */
public class LogEventEvRule extends AbstractDbRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        // TODO Dispatch the event to all the internal subscribers.
        // This will be called when replay the events and be careful regarding to the side effects.

        return true;
    }
}
