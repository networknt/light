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

import com.networknt.light.rule.Rule;

import java.util.List;
import java.util.Map;

/**
 * Add a new form on the server. only owner can add form without host naming space.
 *
 * AccessLevel R [owner, admin, formAdmin]
 */
public class AddFormRule extends AbstractFormRule implements Rule {

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String formId = (String)data.get("formId");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");

        String host = (String)user.get("host");
        if(host != null) {
            if(!host.equals(data.get("host"))) {
                error = "User can only add form from host: " + host;
                inputMap.put("responseCode", 403);
            } else {
                if(!formId.contains(host)) {
                    // you are not allowed to add form as it is not owned by the host.
                    error = "form id doesn't contain host: " + host;
                    inputMap.put("responseCode", 403);
                } else {
                    String json = getFormById(inputMap);
                    if(json != null) {
                        error = "Form with the same id exists";
                        inputMap.put("responseCode", 400);
                    } else {
                        Map eventMap = getEventMap(inputMap);
                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                        inputMap.put("eventMap", eventMap);
                        eventData.putAll((Map<String, Object>)inputMap.get("data"));
                        eventData.put("createDate", new java.util.Date());
                        eventData.put("createUserId", user.get("userId"));
                    }
                }
            }
        } else {
            String json = getFormById(inputMap);
            if(json != null) {
                error = "Form with the same id exists";
                inputMap.put("responseCode", 400);
            } else {
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.putAll((Map<String, Object>)inputMap.get("data"));
                eventData.put("createDate", new java.util.Date());
                eventData.put("createUserId", user.get("userId"));
                // remove host from data as this is owner adding role
                eventData.remove("host");
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
