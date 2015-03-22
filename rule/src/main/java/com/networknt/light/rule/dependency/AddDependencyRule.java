/*
 * Copyright 2015 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.networknt.light.rule.dependency;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.page.AbstractPageRule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import java.util.Map;

/**
 * Created by steve on 3/9/2015.
 *
 * for admin or ruleAdmin, you can select dest only belongs to the host. However, if source
 * is not publishing the message, you get nothing.
 *
 * AccessLevel R [owner, admin, ruleAdmin]
 *
 */
public class AddDependencyRule extends AbstractDependencyRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String host = (String)data.get("host");
        String source = (String)data.get("source");
        String dest = (String)data.get("desc");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String userHost = (String)user.get("host");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            Vertex sourceRule = DbService.getVertexByRid(graph, source);
            Vertex destRule = DbService.getVertexByRid(graph, dest);
            if(sourceRule == null || destRule == null) {
                error = "source rule or destination rule doesn't exist";
                inputMap.put("responseCode", 400);
            } else {
                String sourceRuleClass = sourceRule.getProperty("ruleClass");
                String destRuleClass = destRule.getProperty("ruleClass");
                if(userHost != null) {
                    if (!userHost.equals(host)) {
                        error = "You can only add dependency from host: " + host;
                        inputMap.put("responseCode", 403);
                    } else {
                        // make sure dest ruleClass contains host.
                        if(!destRuleClass.contains(host)) {
                            error = "Destination rule doesn't belong to the host " + host;
                            inputMap.put("responseCode", 403);
                        } else {
                            // check if there is an depend edge from source to dest
                            boolean hasEdge = false;
                            for (Edge edge : (Iterable<Edge>) sourceRule.getEdges(Direction.OUT, "Own")) {
                                if(edge.getVertex(Direction.IN) == destRule) hasEdge = true;
                            }
                            if(hasEdge) {
                                error = "There is depend edge between source rule and dest rule";
                                inputMap.put("responseCode", 400);
                            } else {
                                Map eventMap = getEventMap(inputMap);
                                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                                inputMap.put("eventMap", eventMap);
                                eventData.put("sourceRuleClass", sourceRuleClass);
                                eventData.put("destRuleClass", destRuleClass);
                                eventData.put("content", data.get("content"));
                                eventData.put("createDate", new java.util.Date());
                                eventData.put("createUserId", user.get("userId"));
                            }
                        }
                    }
                }

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
