package com.networknt.light.rule.address;

import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Result;
import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 20/02/16.
 */
public class UpdBillingAddressRule extends AbstractAddressRule implements Rule {
    static final XLogger logger = XLoggerFactory.getXLogger(UpdBillingAddressRule.class);


    @Override
    public boolean execute(Object... objects) throws Exception {
        logger.entry(objects);
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String error = null;
        Map<String, Object> resultMap = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String userId = (String)user.get("userId");
        String host  = (String)data.get("host");

        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex updateUser = (OrientVertex)graph.getVertexByKey("User.userId", userId);
            if(updateUser != null) {
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.putAll(data);
                eventData.put("userId", userId);
                eventData.put("updateDate", new java.util.Date());
                // create a braintree customer here.
                Map<String, Object> billingAddress = (Map<String, Object>)data.get("billingAddress");
                CustomerRequest request = new CustomerRequest()
                        .firstName((String)billingAddress.get("firstName"))
                        .lastName((String)billingAddress.get("lastName"))
                        .email((String)billingAddress.get("email"))
                        .phone((String)billingAddress.get("phone"));
                Result<Customer> customerResult = gatewayMap.get(host).customer().create(request);
                if(customerResult.isSuccess()) {
                    Customer customer = customerResult.getTarget();
                    eventData.put("braintreeCustomerId", customer.getId());
                } else {
                    logger.error("failed to create customer" + customerResult.getMessage());
                }

                // now return the tax according to the address if cartTotal exists
                Object total = data.get("cartTotal");
                if(total != null) {
                    BigDecimal cartTotal = new BigDecimal(data.get("cartTotal").toString());
                    List<Map<String, Object>> items = (List)data.get("cartItems");
                    resultMap = new HashMap<String, Object>();
                    // calculate taxes
                    Map<String, BigDecimal> taxes = calculateTax(host, billingAddress, items, cartTotal);
                    resultMap.put("taxes", taxes);
                }
            } else {
                error = "User with userId " + userId + " cannot be found.";
                inputMap.put("responseCode", 404);
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }

        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            if(resultMap != null) inputMap.put("result", mapper.writeValueAsString(resultMap));
            return true;
        }
    }
}
