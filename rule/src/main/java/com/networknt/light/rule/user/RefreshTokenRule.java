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
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve on 19/09/14.
 *
 * get an access token with a refresh token
 *
 * AccessLevel R [user]
 */
public class RefreshTokenRule extends AbstractUserRule implements Rule {

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String error = null;

        String refreshToken = (String)data.get("refreshToken");
        String userId = (String)data.get("userId");
        String clientId = (String)data.get("clientId");
        if(refreshToken == null || userId == null || clientId == null) {
            inputMap.put("responseCode", 401);
            error = "Refresh token or userId or clientId is missing";
        } else {
            OrientGraph graph = ServiceLocator.getInstance().getGraph();
            try {
                Vertex user = getUserByUserId(graph, userId);
                if(user != null) {
                    Vertex credential = user.getProperty("credential");
                    if (checkRefreshToken(credential, clientId, refreshToken)) {
                        String jwt = generateToken(user, clientId);
                        if (jwt != null) {
                            Map<String, String> tokens = new HashMap<String, String>();
                            tokens.put("accessToken", jwt);
                            inputMap.put("result", mapper.writeValueAsString(tokens));
                        }
                    } else {
                        error = "Invalid refresh token";
                        inputMap.put("responseCode", 400);
                    }
                } else {
                    error = "The userId " + userId + " has not been registered";
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
