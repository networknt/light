package com.networknt.light.rule.payment;

import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.networknt.light.rule.Rule;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by steve on 20/12/15.
 *
 * This rule get payment method nonce from braintree dropin and create a transaction from it.
 * It is a way to charge customer once you have authorization from braintree payment gateway.
 *
 * AccessLevel User
 *
 */
public class AddTransactionRule extends AbstractPaymentRule implements Rule {


    @Override
    public boolean execute(Object... objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String host = (String)data.get("host");
        String nonce = (String)data.get("nonce");
        // In order to make sure that the amount is correct, the final amount will be recalculated
        // based on the items in the cart.

        TransactionRequest request = new TransactionRequest()
                .amount(new BigDecimal("100.00"))
                .paymentMethodNonce(nonce);
        Result<Transaction> result = gatewayMap.get(host).transaction().sale(request);


        return false;
    }
}
