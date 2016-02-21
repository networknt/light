package com.networknt.light.rule.payment;

import com.networknt.light.rule.AbstractCommerceRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 19/12/15.
 */
public abstract class AbstractPaymentRule extends AbstractCommerceRule implements Rule {
    static final XLogger logger = XLoggerFactory.getXLogger(AbstractPaymentRule.class);

    public abstract boolean execute (Object ...objects) throws Exception;

    /**
     * To save the customer transaction into database.
     *
     * @param data
     * @throws Exception
     */
    protected void addTransaction(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex user = graph.getVertexByKey("User.userId", data.remove("createUserId"));
            Vertex order = graph.getVertexByKey("Order.orderId", data.get("orderId"));
            if(order != null) {
                order.setProperty("paymentStatus", 1);  // update payment status to paid.
                Map<String, Object> transaction = (Map<String, Object>)data.get("transaction");
                order.setProperty("nonce", transaction.get("nonce"));
                //order.setProperty
            }
            user.addEdge("Update", order);
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
    }

    /**
     * To save the customer transaction into database.
     *
     * @param data
     * @throws Exception
     */
    protected void addSubscription(Map<String, Object> data) throws Exception {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex user = graph.getVertexByKey("User.userId", data.remove("createUserId"));
            Vertex order = graph.getVertexByKey("Order.orderId", data.get("orderId"));
            if(order != null) {
                order.setProperty("paymentStatus", 1);  // update payment status to paid.
                List<Map<String, Object>> subscriptions = (List<Map<String, Object>>)data.get("subscriptions");
                order.setProperty("subscriptions", subscriptions);
                //order.setProperty
            }
            user.addEdge("Update", order);
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
    }

}
