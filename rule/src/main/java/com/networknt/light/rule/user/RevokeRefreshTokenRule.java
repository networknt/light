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
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Map;

/**
 * Created by steve on 20/01/15.
 */
public class RevokeRefreshTokenRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String password = (String)data.get("password");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> userMap = (Map<String, Object>)payload.get("user");
            String rid = (String)userMap.get("@rid");
            ODocument user = DbService.getODocumentByRid(rid);
            if(user != null) {
                // check password again
                if(checkPassword(user, password)) {
                    // check if there are refresh tokens for the user
                    ODocument credential = (ODocument)user.field("credential");
                    if(credential != null) {
                        Map hostRefreshTokens = credential.field("hostRefreshTokens");
                        if(hostRefreshTokens != null) {
                            // generate the event to remove it.
                            Map eventMap = getEventMap(inputMap);
                            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                            inputMap.put("eventMap", eventMap);
                            eventData.put("userId", user.field("userId"));
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
        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }
}
