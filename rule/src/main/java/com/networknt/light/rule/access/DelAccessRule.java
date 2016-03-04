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

package com.networknt.light.rule.access;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.RuleEngine;
import com.networknt.light.rule.rule.AbstractRuleRule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 30/01/15.
 *
 * This is used in access admin to delete an access control for endpoints.
 *
 * owner can delete access control for any endpoint and host admin and ruleAdmin
 * can delete endpoint for their own host.
 *
 * Due to the importance of the API, the code level access control is in place.
 *
 * AccessLevel R [owner, admin, ruleAdmin]
 *
 */
public class DelAccessRule extends AbstractAccessRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        String rid = (String)data.get("@rid");
        int inputVersion = (int)data.get("@version");
        String ruleClass = (String)data.get("ruleClass");
        String error = null;

        if(user == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains("ruleAdmin")) {
                error = "Role owner or admin or ruleAdmin is required to delete rule";
                inputMap.put("responseCode", 403);
            } else {
                String host = (String)user.get("host");
                if(host != null && !host.equals(data.get("host"))) {
                    error = "User can only delete access control from host: " + host;
                    inputMap.put("responseCode", 403);
                } else {
                    // check if the access control exist or not.
                    OrientGraph graph = ServiceLocator.getInstance().getGraph();
                    try {
                        Vertex access = DbService.getVertexByRid(graph, rid);
                        if(access == null) {
                            error = "Access control with @rid " + rid + " cannot be found";
                            inputMap.put("responseCode", 404);
                        } else {
                            int storedVersion = access.getProperty("@version");
                            if(inputVersion != storedVersion) {
                                error = "Deleting version " + inputVersion + " doesn't match stored version " + storedVersion;
                                inputMap.put("responseCode", 400);
                            } else {
                                Map eventMap = getEventMap(inputMap);
                                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                                inputMap.put("eventMap", eventMap);
                                eventData.put("ruleClass", ruleClass);
                                eventData.put("updateDate", new java.util.Date());
                                eventData.put("updateUserId", user.get("userId"));
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Exception:", e);
                        throw e;
                    } finally {
                        graph.shutdown();
                    }
                }
            }
        }

        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            return true;
        }
    }
}
