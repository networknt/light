package com.networknt.light.rule.payment;

import com.braintreegateway.*;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 2/19/2016.
 *
 * This is for recurring billing for services.
 *
 * AccessLevel R [user]
 *
 */
public class AddSubscriptionRule extends AbstractPaymentRule implements Rule {
    static final XLogger logger = XLoggerFactory.getXLogger(AddTransactionRule.class);

    @Override
    public boolean execute(Object... objects) throws Exception {
        logger.entry(objects[0]);
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String host = (String)data.get("host");
        String userId = (String)user.get("userId"); // this will be customer_id
        Integer orderId = (Integer)data.get("orderId");
        Map<String, Object> tran = (Map<String, Object>)data.get("transaction");
        String nonce = (String)tran.get("nonce");
        BigDecimal total = null;
        String error = null;

        // At this moment, the order is saved already in payment state as pending, load the order.
        // with orderId passed in and return it to the client with updated
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex order = (OrientVertex)graph.getVertexByKey("Order.orderId", orderId);
            if(order != null) {
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.putAll(data);
                List<Map<String, Object>> subscriptions = new ArrayList<Map<String, Object>>();

                // first need to create a payment method for the customer.
                PaymentMethodRequest paymentMethodRequest = new PaymentMethodRequest()
                .customerId(userId)
                .paymentMethodNonce(nonce);
                Result<? extends PaymentMethod> paymentMethodResult = gatewayMap.get(host).paymentMethod().create(paymentMethodRequest);
                if(paymentMethodResult.isSuccess()) {
                    String token = paymentMethodResult.getTarget().getToken();
                    // we need to iterate the order and create subscriptions for each item.
                    for(Map<String, Object> item: (List<Map<String, Object>>)order.getProperty("items")) {
                        // now use this token to subscribe a plan.
                        SubscriptionRequest subscriptionRequest = new SubscriptionRequest()
                                .paymentMethodToken(token)
                                .planId((String)item.get("sku"));
                        Result<Subscription> subscriptionResult = gatewayMap.get(host).subscription().create(subscriptionRequest);
                        if(subscriptionResult.isSuccess()) {
                            Subscription subscription = subscriptionResult.getTarget();
                            Map<String, Object> sub = new HashMap<String, Object>();
                            sub.put("sku", item.get("sku"));
                            sub.put("subscriptionId", subscription.getId());
                            subscriptions.add(sub);

                        } else {
                            Subscription subscription = subscriptionResult.getTarget();
                            error = subscription.getStatus()
                        }
                    }
                    eventData.put("subscriptions", subscriptions);
                    // TODO return the subscription id/sku info to the UI.

                }

                TransactionRequest request = new TransactionRequest()
                        .amount(total)
                        .paymentMethodNonce(nonce)
                        .options()
                        .submitForSettlement(true)
                        .done();
                Result<Transaction> result = gatewayMap.get(host).transaction().sale(request);
                if (result.isSuccess()) {
                    // return the order to ui in order to render the summary page.
                    String json = order.getRecord().toJSON();
                    inputMap.put("result", json);
                } else if (result.getTransaction() != null) {
                    Transaction transaction = result.getTransaction();
                    error = "Error processing transaction. Status: " + transaction.getStatus() +
                            " Code: " + transaction.getProcessorResponseCode() +
                            " Text: " + transaction.getProcessorResponseText();
                    inputMap.put("responseCode", 400);
                    //System.out.println("Error processing transaction:");
                    //System.out.println("  Status: " + transaction.getStatus());
                    //System.out.println("  Code: " + transaction.getProcessorResponseCode());
                    //System.out.println("  Text: " + transaction.getProcessorResponseText());
                } else {
                    inputMap.put("responseCode", 400);
                    for (ValidationError validationError : result.getErrors().getAllDeepValidationErrors()) {
                        error = error + "Attribute: " + validationError.getAttribute() +
                                " Code: " + validationError.getCode() +
                                " Message: " + validationError.getMessage() + "\n";
                        //System.out.println("Attribute: " + validationError.getAttribute());
                        //System.out.println("  Code: " + validationError.getCode());
                        //System.out.println("  Message: " + validationError.getMessage());
                    }
                }

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
