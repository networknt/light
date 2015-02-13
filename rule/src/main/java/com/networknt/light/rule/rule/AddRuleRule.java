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

package com.networknt.light.rule.rule;

import com.networknt.light.rule.Rule;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 08/10/14.
 *
 * This is the rule that allow user to add brand new rule from rule admin interface. It will fail
 * if the rule exist in database. And normally, you only construct simple rules on the fly. Most of
 * the time, you should use impRuleRule instead.
 *
 *
 */
public class AddRuleRule extends AbstractRuleRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains("ruleAdmin")) {
                error = "Role owner or admin or ruleAdmin is required to add rule";
                inputMap.put("responseCode", 403);
            } else {
                String host = (String)user.get("host");
                if(host != null) {
                    if(!host.equals(data.get("host"))) {
                        error = "User can only add rule from host: " + host;
                        inputMap.put("responseCode", 403);
                    } else {
                        // check if the rule exists or not
                        String json = getRuleByRuleClass((String)data.get("ruleClass"));
                        if(json != null) {
                            error = "ruleClass for the rule exists";
                            inputMap.put("responseCode", 400);
                        } else {
                            Map eventMap = getEventMap(inputMap);
                            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                            inputMap.put("eventMap", eventMap);
                            eventData.put("host", host);

                            eventData.put("ruleClass", data.get("ruleClass"));
                            eventData.put("sourceCode", data.get("sourceCode"));
                            eventData.put("createDate", new java.util.Date());
                            eventData.put("createUserId", user.get("userId"));
                        }
                    }
                } else {
                    // check if the rule exists or not.
                    String json = getRuleByRuleClass((String)data.get("ruleClass"));
                    if(json != null) {
                        error = "ruleClass for the rule exists";
                        inputMap.put("responseCode", 400);
                    } else {
                        // This is owner to import rule, notice that no host is passed in.
                        Map eventMap = getEventMap(inputMap);
                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                        inputMap.put("eventMap", eventMap);

                        eventData.put("ruleClass", data.get("ruleClass"));
                        eventData.put("sourceCode", data.get("sourceCode"));
                        eventData.put("createDate", new java.util.Date());
                        eventData.put("createUserId", user.get("userId"));
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
