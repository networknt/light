package com.networknt.light.rule.order;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.shipping.AbstractAddressRule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 20/12/15.
 * Add an order before routing to the payment gateway with default
 * paymentStatus and fulfillmentStatus as pending. Due to the risk that
 * javascript can be updated on the browser, all the numbers will be
 * calculated again here.
 *
 *
 * AccessLevel User
 *
 */
public class AddOrderRule extends AbstractOrderRule implements Rule {

    @Override
    public boolean execute(Object... objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        List<Map<String, Object>> items = (List<Map<String, Object>>)data.get("items");
        BigDecimal subTotal = new BigDecimal(0.00);
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            for(Map<String, Object> item: items) {
                Vertex product = DbService.getVertexByRid(graph, (String)item.get("rid"));
                String sku = (String)item.get("sku");
                BigDecimal qty = new BigDecimal(item.get("qty").toString());
                BigDecimal price = new BigDecimal(0.00);
                List<Map<String, Object>> variants = product.getProperty("variants");
                for(Map<String, Object> variant : variants) {
                    if(sku.equals(variant.get("sku"))) {
                        price = new BigDecimal(variant.get("price").toString());
                        item.put("price", price);
                        break;
                    }
                }
                subTotal = subTotal.add(price.multiply(qty));
            }

            Vertex u = DbService.getVertexByRid(graph, (String) user.get("@rid"));
            Map<String, Object> shippingAddress = u.getProperty("shippingAddress");
            String province = (String)shippingAddress.get("province");

            // now calculate the shipping cost based on the subTotal for now
            BigDecimal shipping = AbstractAddressRule.calculateShipping(province, subTotal);

            // not calculate the tax based on shipping address.
            Map<String, BigDecimal> taxes = AbstractAddressRule.calculateTax(province, subTotal.add(shipping));
            BigDecimal tax = new BigDecimal(0.00);
            for(BigDecimal b : taxes.values()) {
                tax = tax.add(b);
            }

            BigDecimal total = subTotal.add(shipping).add(tax);

            Map eventMap = getEventMap(inputMap);
            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
            inputMap.put("eventMap", eventMap);
            eventData.putAll(data);

            // add orderId here
            int orderId = DbService.incrementCounter("orderId");
            eventData.put("orderId", orderId);
            eventData.put("subTotal", subTotal);
            eventData.put("shipping", shipping);
            eventData.put("tax", tax);
            eventData.putAll(taxes);
            eventData.put("total", total);
            eventData.put("createDate", new java.util.Date());
            eventData.put("createUserId", user.get("userId"));
            inputMap.put("result", "{\"orderId\":" + orderId + ", \"total\":"  + total + "}");
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        return true;
    }
}
