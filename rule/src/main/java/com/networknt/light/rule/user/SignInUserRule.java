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

package com.networknt.light.rule.user;

import com.networknt.light.rule.Rule;
import com.networknt.light.util.HashUtil;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve on 14/09/14.
 *
 * AccessLevel A
 *
 */
public class SignInUserRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String userIdEmail = (String) data.get("userIdEmail");
        String inputPassword = (String) data.get("password");
        Boolean rememberMe = (Boolean)data.get("rememberMe");
        String clientId = (String)data.get("clientId");
        String error = null;
        // check if clientId is passed in.
        if(clientId == null || clientId.trim().length() == 0) {
            error = "ClientId is required";
            inputMap.put("responseCode", 400);
        } else {
            OrientGraph graph = ServiceLocator.getInstance().getGraph();
            try {
                Vertex user = null;
                if(isEmail(userIdEmail)) {
                    user = getUserByEmail(graph, userIdEmail);
                } else {
                    user = getUserByUserId(graph, userIdEmail);
                }
                if(user != null) {
                    if(checkPassword(graph, user, inputPassword)) {
                        String jwt = generateToken(user, clientId);
                        if(jwt != null) {
                            Map eventMap = getEventMap(inputMap);
                            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                            inputMap.put("eventMap", eventMap);
                            Map<String, String> tokens = new HashMap<String, String>();
                            tokens.put("accessToken", jwt);
                            if(rememberMe != null && rememberMe) {
                                // generate refreshToken
                                String refreshToken = HashUtil.generateUUID();
                                tokens.put("refreshToken", refreshToken);
                                String hashedRefreshToken = HashUtil.md5(refreshToken);
                                eventData.put("hashedRefreshToken", hashedRefreshToken);
                            }
                            inputMap.put("result", mapper.writeValueAsString(tokens));
                            eventData.put("clientId", clientId);
                            eventData.put("userId", user.getProperty("userId"));
                            eventData.put("host", data.get("host"));  // add host as refreshToken will be associate with host.
                            eventData.put("logInDate", new java.util.Date());
                        }
                    } else {
                        error = "Invalid password";
                        inputMap.put("responseCode", 400);
                    }
                } else {
                    error = "Invalid userId or email";
                    inputMap.put("responseCode", 400);
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
