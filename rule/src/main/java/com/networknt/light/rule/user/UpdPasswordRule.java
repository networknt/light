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
import com.networknt.light.util.HashUtil;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.Map;

/**
 * Created by steve on 8/29/2014.
 *
 * Logged in user update its own password
 *
 * AccessLevel R [user]
 *
 */
public class UpdPasswordRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String error = null;
        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        String userId = (String)user.get("userId");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            Vertex updateUser = graph.getVertexByKey("User.userId", userId);
            if(updateUser != null) {
                String password = (String) data.get("password");
                String newPassword = (String)data.get("newPassword");
                String passwordConfirm = (String)data.get("passwordConfirm");

                // check if the password match
                boolean match = checkPassword(graph, updateUser, password);
                if(match) {
                    if(newPassword.equals(passwordConfirm)) {
                        newPassword = HashUtil.generateStrongPasswordHash(newPassword);
                        Map eventMap = getEventMap(inputMap);
                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                        inputMap.put("eventMap", eventMap);
                        eventData.put("userId", updateUser.getProperty("userId"));
                        eventData.put("password", newPassword);
                        eventData.put("updateDate", new java.util.Date());
                    } else {
                        error = "New password and password confirm are not the same.";
                        inputMap.put("responseCode", 400);
                    }
                } else {
                    error = "The old password is incorrect.";
                    inputMap.put("responseCode", 400);
                }
            } else {
                error = "User with userId " + userId + " cannot be found.";
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
