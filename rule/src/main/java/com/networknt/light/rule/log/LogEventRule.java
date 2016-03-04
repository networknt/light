package com.networknt.light.rule.log;

import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by Steve Hu on 2015-01-20.
 *
 * This is a handler to log all the client side and server side exceptions. Also, it
 * can be used to instrument performance logging or any other events happening on
 * the client side. The data payload is a flexible structure and it is up to you
 * to define what and when to be logged.
 *
 * AccessLevel A
 *
 */
public class LogEventRule extends AbstractRule implements Rule {

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        String userId = null;
        if(user != null) {
            userId = (String)user.get("userId");
        }

        // TODO send notifications for serious events or exceptions.
        // Some events might trigger a pager
        // Some events might trigger a email

        Map eventMap = getEventMap(inputMap);
        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
        inputMap.put("eventMap", eventMap);
        eventData.putAll((Map<String, Object>)inputMap.get("data"));
        eventData.put("createDate", new java.util.Date());
        if(userId != null) eventData.put("createUserId", userId);
        eventData.put("ipAddress", inputMap.get("ipAddress"));

        return true;
    }
}
