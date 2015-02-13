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
import com.networknt.light.rule.RuleEngine;
import com.networknt.light.server.DbService;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 08/10/14.
 */
public class UpdRuleRule extends AbstractRuleRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        int inputVersion = (int)data.get("@version");
        String rid = (String)data.get("@rid");
        String ruleClass = (String)data.get("ruleClass");
        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains("ruleAdmin")) {
                error = "Role owner or admin or ruleAdmin is required to update rule";
                inputMap.put("responseCode", 403);
            } else {
                String host = (String)user.get("host");
                if(host != null) {
                    if(!host.equals(data.get("host"))) {
                        error = "User can only update rule for host: " + host;
                        inputMap.put("responseCode", 403);
                    } else {
                        // make sure the ruleClass contains the host.
                        if(host != null && !ruleClass.contains(host)) {
                            // you are not allowed to update rule as it is not owned by the host.
                            error = "ruleClass is not owned by the host: " + host;
                            inputMap.put("responseCode", 403);
                        } else {
                            ODocument rule = DbService.getODocumentByRid(rid);
                            if(rule == null) {
                                error = "Rule with @rid " + rid + " cannot be found";
                                inputMap.put("responseCode", 404);
                            } else {
                                int storedVersion = rule.field("@version");
                                if(inputVersion != storedVersion) {
                                    error = "Updating version " + inputVersion + " doesn't match stored version " + storedVersion;
                                    inputMap.put("responseCode", 400);
                                } else {
                                    // remove the rule instance from Rule Engine Cache
                                    RuleEngine.getInstance().removeRule(ruleClass);
                                    Map eventMap = getEventMap(inputMap);
                                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                                    inputMap.put("eventMap", eventMap);
                                    eventData.put("ruleClass", ruleClass);
                                    eventData.put("sourceCode", data.get("sourceCode"));
                                    eventData.put("updateDate", new java.util.Date());
                                    eventData.put("updateUserId", user.get("userId"));
                                }
                            }
                        }
                    }
                } else {
                    ODocument rule = DbService.getODocumentByRid(rid);
                    if(rule == null) {
                        error = "Rule with @rid " + rid + " cannot be found";
                        inputMap.put("responseCode", 404);
                    } else {
                        int storedVersion = rule.field("@version");
                        if(inputVersion != storedVersion) {
                            error = "Updating version " + inputVersion + " doesn't match stored version " + storedVersion;
                            inputMap.put("responseCode", 400);
                        } else {
                            // remove the rule instance from Rule Engine Cache
                            RuleEngine.getInstance().removeRule(ruleClass);

                            Map eventMap = getEventMap(inputMap);
                            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                            inputMap.put("eventMap", eventMap);
                            eventData.put("ruleClass", ruleClass);
                            eventData.put("sourceCode", data.get("sourceCode"));
                            eventData.put("updateDate", new java.util.Date());
                            eventData.put("updateUserId", user.get("userId"));
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
