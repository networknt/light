package com.networknt.light.rule.order;

import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 20/12/15.
 */
public abstract class AbstractOrderRule extends AbstractRule implements Rule {
    static final XLogger logger = XLoggerFactory.getXLogger(AbstractOrderRule.class);

    public abstract boolean execute (Object ...objects) throws Exception;

    /**
     * To save the order right before routing to payment gateway
     *
     * @param data
     * @throws Exception
     */
    protected void addOrder(Map<String, Object> data) throws Exception {
        logger.entry(data);
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex user = graph.getVertexByKey("User.userId", data.remove("createUserId"));
            OrientVertex order = graph.addVertex("class:Order", data);
            user.addEdge("Order", order);
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
