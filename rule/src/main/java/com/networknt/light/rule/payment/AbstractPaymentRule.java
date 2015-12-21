package com.networknt.light.rule.payment;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 19/12/15.
 */
public abstract class AbstractPaymentRule extends AbstractRule implements Rule {
    static final XLogger logger = XLoggerFactory.getXLogger(AbstractPaymentRule.class);
    // This is to cache all the getways for each host in memory so that we don't need to create new gateway
    // for each request.
    static Map<String, BraintreeGateway> gatewayMap = new HashMap<String, BraintreeGateway>();

    static {
        // load braintree config for each host and initialize gateways.
        Map<String, Object> config = ServiceLocator.getInstance().getConfig("braintree");
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            String host = entry.getKey();
            Map<String, Object> braintreeConfig = (Map<String, Object>)entry.getValue();

            BraintreeGateway gateway = new BraintreeGateway((Boolean)braintreeConfig.get("sandbox") == true? Environment.SANDBOX : Environment.PRODUCTION,
                    (String)braintreeConfig.get("merchant_id"), (String)braintreeConfig.get("public_key"), (String)braintreeConfig.get("private_key"));
            gatewayMap.put(host, gateway);
        }
    }

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
            List<String> addMenuItems = (List<String>)data.remove("addMenuItems");
            OrientVertex menuItem = graph.addVertex("class:MenuItem", data);
            if(addMenuItems != null && addMenuItems.size() > 0) {
                // find vertex for each menuItem id and create edge to it.
                for(String menuItemId: addMenuItems) {
                    Vertex childMenuItem = graph.getVertexByKey("MenuItem.menuItemId", menuItemId);
                    menuItem.addEdge("Own", childMenuItem);
                }
            }
            user.addEdge("Create", menuItem);
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
