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
 * owner can delete any form and admin or formAdmin can only delete forms belong to the host and name must
 * contain the host.
 *
 * AccessLevel R [owner, admin, formAdmin
 */
public class DelFormRule extends AbstractFormRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String formId = (String)data.get("formId");
        String error = null;

        String host = (String)user.get("host");
        if(host != null) {
            // admin or formAdmin
            if(!host.equals(data.get("host"))) {
                error = "User can only delete form for host: " + host;
                inputMap.put("responseCode", 403);
            } else {
                if(!formId.contains(host)) {
                    // you are not allowed to delete form as it is not owned by the host.
                    error = "form id doesn't contain host: " + host;
                    inputMap.put("responseCode", 403);
                } else {
                    int inputVersion = (int)data.get("@version");
                    String json = getFormById(inputMap);
                    if(json == null) {
                        error = "Form with id " + formId + " cannot be found";
                        inputMap.put("responseCode", 404);
                    } else {
                        Map<String, Object> form = mapper.readValue(json,
                                new TypeReference<HashMap<String, Object>>() {
                                });
                        // check the version
                        int storedVersion = (int)form.get("@version");
                        if (inputVersion != storedVersion) {
                            inputMap.put("responseCode", 400);
                            error = "Deleting version " + inputVersion + " doesn't match stored version " + storedVersion;
                        } else {
                            Map eventMap = getEventMap(inputMap);
                            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                            inputMap.put("eventMap", eventMap);
                            eventData.put("formId", form.get("formId"));
                        }
                    }
                }
            }
        } else {
            // owner
            int inputVersion = (int)data.get("@version");
            String json = getFormById(inputMap);
            if(json == null) {
                error = "Form with id " + formId + " cannot be found";
                inputMap.put("responseCode", 404);
            } else {
                Map<String, Object> form = mapper.readValue(json,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                // check the version
                int storedVersion = (int)form.get("@version");
                if (inputVersion != storedVersion) {
                    inputMap.put("responseCode", 400);
                    error = "Deleting version " + inputVersion + " doesn't match stored version " + storedVersion;
                } else {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("formId", form.get("formId"));
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
