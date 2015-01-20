package com.networknt.light.rule.log;

import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;

import java.util.List;
import java.util.Map;

/**
 * Created by Steve Hu on 2015-01-20.
 *
 * This is a handler to log all the client side and server side exceptions. Also, it
 * can be used to instrument performance logging or any other events happening on
 * the client side. The data payload is a flexible structure and it is up to you
 * to define what and when to be logged.
 *
 */
public class LogEventRule extends AbstractRule implements Rule {

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        // TODO send notifications for serious events or exceptions.

        // Some events might trigger a pager
        // Some events might trigger a email

        return true;
    }
}
