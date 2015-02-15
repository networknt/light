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

import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.RuleEngine;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 30/12/14.
 * This is the rule that will be loaded by the db script in initDatabase to bootstrap rule
 * loading for others. Also, it can be used to import rules developed and tested locally from
 * Rule Admin interface.
 *
 * Warning: it will replace any existing rules if Rule Class is the same.
 *
 * AccessLevel R [owner, admin, ruleAdmin]
 *
 * current R [owner] until workflow is done.
 *
 */
public class ImpRuleRule extends AbstractRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String ruleClass = (String)data.get("ruleClass");
        String error = null;
        String host = (String)user.get("host");
        if(host != null) {
            if(!host.equals(data.get("host"))) {
                error = "User can only import rule from host: " + host;
                inputMap.put("responseCode", 403);
            } else {
                // make sure the ruleClass contains the host.
                if(host != null && !ruleClass.contains(host)) {
                    // you are not allowed to update rule as it is not owned by the host.
                    error = "ruleClass is not owned by the host: " + host;
                    inputMap.put("responseCode", 403);
                } else {
                    // remove the rule instance from Rule Engine Cache
                    RuleEngine.getInstance().removeRule(ruleClass);

                    // Won't check if rule exists or not here.
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("host", host);

                    eventData.put("ruleClass", ruleClass);
                    eventData.put("sourceCode", data.get("sourceCode"));
                    eventData.put("createDate", new java.util.Date());
                    eventData.put("createUserId", user.get("userId"));
                }
            }
        } else {
            // check if access exist for the rule exists or not. If exists, then there is
            // remove the rule instance from Rule Engine Cache
            RuleEngine.getInstance().removeRule(ruleClass);

            // This is owner to import rule, notice that no host is passed in.
            Map eventMap = getEventMap(inputMap);
            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
            inputMap.put("eventMap", eventMap);

            eventData.put("ruleClass", ruleClass);
            eventData.put("sourceCode", data.get("sourceCode"));
            eventData.put("createDate", new java.util.Date());
            eventData.put("createUserId", user.get("userId"));
        }

        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }
}
