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
 * Created by steve on 14/02/15.
 *
 * This rule is used by the rule:load plugin in maven-plugin project to check if source code
 * has been changed. It returns a map from ruleClass to sourceCode for easy comparison.
 *
 * accessLevel is owner by default
 *
 */
public class GetRuleMapRule extends AbstractRuleRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>) payload.get("user");
        String host = (String) user.get("host");
        String hostRuleMap = getRuleMap(host);
        if(hostRuleMap != null) {
            inputMap.put("result", hostRuleMap);
            return true;
        } else {
            inputMap.put("result", "No rule can be found.");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
