package com.networknt.light.rule.menu;

import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import java.util.Map;

/**
 * Created by Nicholas Azar on 3/25/2015.
 */
public class AddMenuComponentRule extends AbstractMenuRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String menuComponentId = (String)data.get("menuComponentId");
        String host = (String)data.get("host");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String userHost = (String)user.get("host");
        if(userHost != null) {
            if (!userHost.equals(host)) {
                error = "You can only add page from host: " + host;
                inputMap.put("responseCode", 403);
            }
        } else {
            // remove host as this is the owner
            data.remove("host");
        }
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            String json = getMenuComponentByHost(graph, host);
            if(json != null) {
                error = "Menu component with the same host exists";
                inputMap.put("responseCode", 400);
            } else {
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.putAll(data);
                eventData.put("createDate", new java.util.Date());
                eventData.put("createUserId", user.get("userId"));
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }
}
