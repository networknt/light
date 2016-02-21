package com.networknt.light.rule.payment;

import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationError;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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
                total = order.getProperty("total");
                TransactionRequest request = new TransactionRequest()
                        .amount(total)
                        .paymentMethodNonce(nonce)
                        .options()
                        .submitForSettlement(true)
                        .done();
                Result<Transaction> result = gatewayMap.get(host).transaction().sale(request);
                if (result.isSuccess()) {
                    Transaction transaction = result.getTarget();
                    //System.out.println("Success!: " + transaction.getId());
                    // prepare for update order payment status
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.putAll(data);
                    eventData.put("transactionId", transaction.getId());
                    eventData.put("createDate", new java.util.Date());
                    eventData.put("createUserId", user.get("userId"));

                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("orderId", orderId);
                    map.put("transactionId", transaction.getId());
                    inputMap.put("result", mapper.writeValueAsString(map));
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
