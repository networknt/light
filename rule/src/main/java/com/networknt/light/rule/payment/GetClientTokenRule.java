package com.networknt.light.rule.payment;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import java.util.Map;

/**
 * Created by steve on 19/12/15.
 * Get client token from braintree payment in order to initialize the dropin form
 *
 * AccessLevel user
 *
 */
public class GetClientTokenRule extends AbstractPaymentRule implements Rule {

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");

        String error = null;
        // using host to get the production or sandbox configuration for braintree payment.
        String host = (String)data.get("host");
        // userId will be passed to braintree for customer_id in order to save default payment method.
        String userId = (String)user.get("userId");

        if(host == null) {
            error = "Host is missing from command data";
            inputMap.put("responseCode", 400);
        } else {
            BraintreeGateway gateway = gatewayMap.get(host);
            ClientTokenRequest clientTokenRequest = new ClientTokenRequest();
            String clientToken = gateway.clientToken().generate(clientTokenRequest);
            if(clientToken == null) {
                error = "Failed to generate client token";
                inputMap.put("responseCode", 400);
            } else {
                inputMap.put("result", "{\"clientToken\": \"" + clientToken + "\"}");
            }
        }
        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            return true;
        }
    }


}
