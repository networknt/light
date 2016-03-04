package com.networknt.light.rule.address;

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
 *
 * Confirm shipping address will only be called from shopping cart. So assume cartTotal is not null
 *
 * AccessLevel R [user]
 *
 */
public class CnfShippingAddressRule extends AbstractAddressRule implements Rule {
    static final XLogger logger = XLoggerFactory.getXLogger(CnfShippingAddressRule.class);


    @Override
    public boolean execute(Object... objects) throws Exception {
        logger.entry(objects);
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String error = null;
        Map<String, Object> resultMap = null;
        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        String userId = (String)user.get("userId");
        String host = (String)data.get("host");
        // expect a list of products in order to calculate shipping cost, shipping address etc.
        // the calculation will be done on the server side in order to avoid hack in the js.
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex updateUser = (OrientVertex)graph.getVertexByKey("User.userId", userId);
            if(updateUser != null) {
                // now return the shipping cost and tax according to the address.
                BigDecimal cartTotal = new BigDecimal(data.get("cartTotal").toString());
                List<Map<String, Object>> items = (List)data.get("cartItems");
                resultMap = new HashMap<String, Object>();
                Map<String, Object> shippingAddress = (Map<String, Object>)data.get("shippingAddress");
                BigDecimal shipping = calculateShipping(host, shippingAddress, items, cartTotal);
                resultMap.put("shipping", shipping);
                // calculate taxes
                Map<String, BigDecimal> taxes = calculateTax(host, shippingAddress, items, cartTotal.add(shipping));
                resultMap.put("taxes", taxes);
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
            inputMap.put("result", mapper.writeValueAsString(resultMap));
            return true;
        }
    }
}
