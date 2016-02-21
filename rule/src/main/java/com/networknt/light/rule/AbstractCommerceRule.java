package com.networknt.light.rule;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.networknt.light.util.ServiceLocator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve on 21/02/16.
 */
public abstract class AbstractCommerceRule extends AbstractRule implements Rule {
    public abstract boolean execute (Object ...objects) throws Exception;

    // This is to cache all the getways for each host in memory so that we don't need to create new gateway
    // for each request.
    protected static Map<String, BraintreeGateway> gatewayMap = new HashMap<String, BraintreeGateway>();

    static {
        // load braintree config for each host and initialize gateways.
        Map<String, Object> config = ServiceLocator.getInstance().getJsonMapConfig("braintree");
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            String host = entry.getKey();
            Map<String, Object> braintreeConfig = (Map<String, Object>)entry.getValue();

            BraintreeGateway gateway = new BraintreeGateway((Boolean)braintreeConfig.get("sandbox") == true? Environment.SANDBOX : Environment.PRODUCTION,
                    (String)braintreeConfig.get("merchant_id"), (String)braintreeConfig.get("public_key"), (String)braintreeConfig.get("private_key"));
            gatewayMap.put(host, gateway);
        }
    }

}
