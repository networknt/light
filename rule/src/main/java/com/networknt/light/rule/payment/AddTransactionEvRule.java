package com.networknt.light.rule.payment;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 20/12/15.
 *
 * This is the event rule to save the transaction created by the validation rule. As payment
 * is integrated with braintree, so the call to braintree is handled in the validation rule
 * to avoid side effect. This event rule will only save the created transactions in order to
 * recreate snapshot.
 *
 */
public class AddTransactionEvRule extends AbstractPaymentRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        addTransaction(data);
        return true;
    }

}
