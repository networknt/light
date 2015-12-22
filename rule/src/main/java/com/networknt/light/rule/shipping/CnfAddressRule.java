package com.networknt.light.rule.shipping;

import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve on 13/12/15.
 *
 * This rule is to confirm that the shipping address in profile is correct and nothing needs
 * to be updated. This is a readonly rule and shipping cost and tax will be calculated and returned.
 *
 * AccessLevel user
 *
 */
public class CnfAddressRule extends AbstractAddressRule implements Rule {
    static final XLogger logger = XLoggerFactory.getXLogger(UpdAddressRule.class);


    @Override
    public boolean execute(Object... objects) throws Exception {
        logger.entry(objects);
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String error = null;
        Map<String, Object> resultMap = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String rid = (String)user.get("@rid");
        // expect a list of products in order to calculate shipping cost, shipping address etc.
        // the calculation will be done on the server side in order to avoid hack in the js.
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            Vertex updateUser = DbService.getVertexByRid(graph, rid);
            if(updateUser != null) {
                // now return the shipping cost and tax according to the address.
                BigDecimal cartTotal = new BigDecimal(data.get("cartTotal").toString());
                resultMap = new HashMap<String, Object>();
                Map<String, Object> shippingAddress = (Map<String, Object>)data.get("shippingAddress");
                BigDecimal shipping = AbstractAddressRule.calculateShipping((String) shippingAddress.get("province"), cartTotal);
                resultMap.put("shipping", shipping);
                // calculate taxes
                Map<String, BigDecimal> taxes = calculateTax((String)shippingAddress.get("province"), cartTotal.add(shipping));
                resultMap.put("taxes", taxes);
            } else {
                error = "User with rid " + rid + " cannot be found.";
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
