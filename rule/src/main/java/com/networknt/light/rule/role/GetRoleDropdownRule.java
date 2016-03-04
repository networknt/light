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

package com.networknt.light.rule.role;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by steve on 21/11/14.
 *
 * AccessLevel R [user]
 */
public class GetRoleDropdownRule extends AbstractRoleRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        String host = (String) user.get("host");
        String hostRoleDropdown = getRoleDropdown(host);
        if(hostRoleDropdown != null) {
            inputMap.put("result", hostRoleDropdown);
            return true;
        } else {
            inputMap.put("result", "No role can be found.");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
