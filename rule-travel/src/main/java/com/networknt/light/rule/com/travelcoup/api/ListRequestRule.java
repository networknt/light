package com.networknt.light.rule.com.travelcoup.api;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 25/11/15.
 *
 * This is the entry point for list request of hotels.
 *
 * data contains:
 * destination
 * adultsNum
 * childrenNum
 * departureDate
 * returningDate
 *
 *
 */
public class ListRequestRule extends AbstractApiRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String list = requestList(data);
        if(list != null) {
            inputMap.put("result", list);
            return true;
        } else {
            inputMap.put("result", "No list found");
            inputMap.put("responseCode", 404);
            return false;
        }
    }

}
