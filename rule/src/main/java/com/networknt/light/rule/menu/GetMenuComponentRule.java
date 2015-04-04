package com.networknt.light.rule.menu;

import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import java.util.Map;

/**
 * Created by Nicholas Azar on 3/19/2015.
 */

public class GetMenuComponentRule extends AbstractMenuRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String host = (String)data.get("host");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        String json = null;
        try {
            json = getMenuComponentByHost(graph, host);
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        if(json != null) {
            inputMap.put("result", json);
            return true;
        } else {
            inputMap.put("Errrr...", 404);
            return false;
        }
    }
}