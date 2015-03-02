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

package com.networknt.light.rule.role;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.RuleEngine;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/31/2014.
 */
public class UpdRoleRule extends AbstractRoleRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String rid = (String)data.get("@rid");
        String error = null;
        String host = (String)user.get("host");
        OrientGraphNoTx graph = ServiceLocator.getInstance().getNoTxGraph();
        if(host != null) {
            if(!host.equals(data.get("host"))) {
                error = "User can only update role for host: " + host;
                inputMap.put("responseCode", 401);
            } else {
                try {
                    Vertex role = DbService.getVertexByRid(graph, rid);
                    if(role == null) {
                        error = "Role with @rid " + rid + " cannot be found";
                        inputMap.put("responseCode", 404);
                    } else {
                        int inputVersion = (int)data.get("@version");
                        int storedVersion = role.getProperty("@version");
                        if(inputVersion != storedVersion) {
                            inputMap.put("responseCode", 400);
                            error = "Updating version " + inputVersion + " doesn't match stored version " + storedVersion;
                        } else {
                            Map eventMap = getEventMap(inputMap);
                            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                            inputMap.put("eventMap", eventMap);
                            eventData.put("roleId", data.get("roleId"));
                            eventData.put("desc", data.get("desc"));
                            eventData.put("host", data.get("host"));
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
        } else {
            try {
                Vertex role = DbService.getVertexByRid(graph, rid);
                if(role == null) {
                    error = "Role with @rid " + rid + " cannot be found";
                    inputMap.put("responseCode", 404);
                } else {
                    int inputVersion = (int)data.get("@version");
                    int storedVersion = role.getProperty("@version");
                    if(inputVersion != storedVersion) {
                        inputMap.put("responseCode", 400);
                        error = "Updating version " + inputVersion + " doesn't match stored version " + storedVersion;
                    } else {
                        Map eventMap = getEventMap(inputMap);
                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                        inputMap.put("eventMap", eventMap);
                        eventData.put("roleId", data.get("roleId"));
                        eventData.put("desc", data.get("desc"));
                        eventData.put("updateDate", new java.util.Date());
                        eventData.put("updateUserId", user.get("userId"));
                        // this is the owner update the role. no host.
                    }
                }
            } catch (Exception e) {
                logger.error("Exception:", e);
                throw e;
            } finally {
                graph.shutdown();
            }
        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }
}
