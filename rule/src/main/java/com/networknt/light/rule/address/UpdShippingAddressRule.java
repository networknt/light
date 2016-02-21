package com.networknt.light.rule.address;

import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 20/02/16.
 *
 * AccessLevel R [user]
 */
public class UpdShippingAddressRule extends AbstractAddressRule implements Rule {
    static final XLogger logger = XLoggerFactory.getXLogger(UpdShippingAddressRule.class);


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
        String host = (String)data.get("host");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            Vertex updateUser = DbService.getVertexByRid(graph, rid);
            if(updateUser != null) {
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.putAll(data);
                eventData.put("userId", updateUser.getProperty("userId"));
                eventData.put("updateDate", new java.util.Date());

                // now return the shipping cost and tax according to the address if cartTotal exists
                Object total = data.get("cartTotal");
                if(total != null) {
                    BigDecimal cartTotal = new BigDecimal(data.get("cartTotal").toString());
                    List<Map<String, Object>> items = (List)data.get("cartItems");
                    resultMap = new HashMap<String, Object>();
                    Map<String, Object> shippingAddress = (Map<String, Object>)data.get("shippingAddress");
                    BigDecimal shipping = calculateShipping(host, shippingAddress, items, cartTotal);
                    resultMap.put("shipping", shipping);
                    // calculate taxes
                    Map<String, BigDecimal> taxes = calculateTax(host, shippingAddress, items, cartTotal.add(shipping));
                    resultMap.put("taxes", taxes);
                }
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
            if(resultMap != null) inputMap.put("result", mapper.writeValueAsString(resultMap));
            return true;
        }
    }
}
