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

package com.networknt.light.rule.form;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.Rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 8/29/2014.
 *
 */
public class UpdFormRule extends AbstractFormRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        String host = (String)data.get("host");
        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains("formAdmin")) {
                error = "Role owner or admin or formAdmin is required to update form";
                inputMap.put("responseCode", 401);
            } else {
                String userHost = (String)user.get("host");
                if(userHost != null && !userHost.equals(host)) {
                    error = "User can only update form for host: " + host;
                    inputMap.put("responseCode", 401);
                } else {
                    int inputVersion = (int)data.get("@version");
                    String id = (String)data.get("id");
                    String json = getFormById(id);
                    if(json == null) {
                        error = "Form with id " + id + " cannot be found";
                        inputMap.put("responseCode", 404);
                    } else {
                        Map<String, Object> form = mapper.readValue(json,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        int storedVersion = (int)form.get("@version");
                        if(inputVersion != storedVersion) {
                            inputMap.put("responseCode", 400);
                            error = "Updating version " + inputVersion + " doesn't match stored version " + storedVersion;
                        } else {
                            Map eventMap = getEventMap(inputMap);
                            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                            inputMap.put("eventMap", eventMap);
                            eventData.putAll((Map<String, Object>)inputMap.get("data"));
                            eventData.put("updateDate", new java.util.Date());
                            eventData.put("updateUserId", user.get("userId"));
                            // no need to remove host from eventData as owner update won't touch id and host.
                        }
                    }
                }
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

