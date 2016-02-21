package com.networknt.light.rule.payment;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 2/19/2016.
 */
public class AddSubscriptionEvRule extends AbstractPaymentRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        addSubscription(data);
        return true;
    }
}
