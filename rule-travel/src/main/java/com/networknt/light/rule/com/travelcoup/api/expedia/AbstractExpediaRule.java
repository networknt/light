package com.networknt.light.rule.com.travelcoup.api.expedia;

import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.networknt.light.rule.com.travelcoup.api.AbstractApiRule;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.util.Map;

/**
 * Created by steve on 25/11/15.
 *
 * It contains all the shared functions for expedia APIs
 *
 */
public abstract class AbstractExpediaRule extends AbstractApiRule implements Rule {
    XLogger logger = XLoggerFactory.getXLogger(AbstractExpediaRule.class);

    protected String listRequest(Map<String, Object> data) {
        String json = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            ODocument record = graph.getVertex(data.get("@rid")).getRecord();
            json = record.toJSON("rid,fetchPlan:[*]in_Create:-2 out_HasComment:5");
        } catch (Exception e) {
            logger.error("Exception:", e);
        } finally {
            graph.shutdown();
        }
        return json;
    }

}
