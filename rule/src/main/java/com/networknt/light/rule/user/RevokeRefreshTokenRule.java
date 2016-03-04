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
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import java.util.Map;

/**
 * Created by steve on 20/01/15.
 *
 * revoke refresh token if user's device is lost or forget to log out on public computer.
 *
 * user role can only revoke refresh token belongs to the current user.
 *
 * AccessLevel R [owner, admin, userAdmin, user]
 *
 */
public class RevokeRefreshTokenRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String password = (String)data.get("password");
        String error = null;
        Map<String, Object> userMap = (Map<String, Object>) inputMap.get("user");
        String rid = (String)userMap.get("@rid");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            Vertex user = DbService.getVertexByRid(graph, rid);
            if(user != null) {
                // check password again
                if(checkPassword(graph, user, password)) {
                    // check if there are refresh tokens for the user
                    Vertex credential = user.getProperty("credential");
                    if(credential != null) {
                        Map clientRefreshTokens = credential.getProperty("clientRefreshTokens");
                        if(clientRefreshTokens != null) {
                            // generate the event to remove it.
                            Map eventMap = getEventMap(inputMap);
                            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                            inputMap.put("eventMap", eventMap);
                            eventData.put("userId", user.getProperty("userId"));
                            eventData.put("updateDate", new java.util.Date());
                        }
                    }
                } else {
                    error = "Invalid password";
                    inputMap.put("responseCode", 401);
                }
            } else {
                error = "User with rid " + rid + " cannot be found.";
                inputMap.put("responseCode", 404);
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
