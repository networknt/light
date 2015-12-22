package com.networknt.light.rule.payment;

import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.shipping.AbstractAddressRule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

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
    static final XLogger logger = XLoggerFactory.getXLogger(AddTransactionRule.class);

    @Override
    public boolean execute(Object... objects) throws Exception {
        logger.entry(objects[0]);
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String host = (String)data.get("host");
        Integer orderId = (Integer)data.get("orderId");
        Map<String, Object> transaction = (Map<String, Object>)data.get("transaction");
        String nonce = (String)transaction.get("nonce");
        BigDecimal total = null;
        String error = null;

        // At this moment, the order is saved already in payment state as pending, load the order.
        // with orderId passed in and return it to the client with updated
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex order = (OrientVertex)graph.getVertexByKey("Order.orderId", orderId);
            if(order != null) {
                total = order.getProperty("total");
                TransactionRequest request = new TransactionRequest()
                        .amount(total)
                        .paymentMethodNonce(nonce);
                Result<Transaction> result = gatewayMap.get(host).transaction().sale(request);
                // TODO how to check if result is OK? Not not OK, cancel the order?

                // prepare for update order payment status
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.putAll(data);
                eventData.put("createDate", new java.util.Date());
                eventData.put("createUserId", user.get("userId"));
                // return the order to ui in order to render the summary page.
                String json = order.getRecord().toJSON();
                inputMap.put("result", json);

                // TODO send an email with order info to the customer here. Assuming payment status is paid here.

            } else {
                inputMap.put("responseCode", 400);
                error = "Could not find order with orderId: " + orderId;
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
            return true;
        }
    }
}
