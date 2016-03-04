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
 * Confirm billing address can only be called from shopping cart. assume cartTotal is there.
 *
 * AccessLevel R [user]
 *
 */
public class CnfBillingAddressRule extends AbstractAddressRule implements Rule {
    static final XLogger logger = XLoggerFactory.getXLogger(CnfBillingAddressRule.class);


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
        // expect a list of products/services in order to calculate tax etc.
        // the calculation will be done on the server side in order to avoid hack in the js.
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex updateUser = (OrientVertex)graph.getVertexByKey("User.userId", userId);
            if(updateUser != null) {
                BigDecimal cartTotal = new BigDecimal(data.get("cartTotal").toString());
                List<Map<String, Object>> items = (List)data.get("cartItems");
                resultMap = new HashMap<String, Object>();
                Map<String, Object> billingAddress = (Map<String, Object>)data.get("billingAddress");
                // calculate taxes
                Map<String, BigDecimal> taxes = calculateTax(host, billingAddress, items, cartTotal);
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
