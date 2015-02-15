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
 * Created by steve on 9/4/2014.
 *
 * Is there a way to verify that the memoryImage is in sync with db?
 * In that case, we don't need to reload from db every time this rule is executed.
 * What we can do is to load all forms in the beginning when server starts, and make
 * sure all the form updates are gone through these set of rules.
 *
 * AccessLevel R [user, admin, formAdmin]
 *
 */
public class GetAllFormRule extends AbstractFormRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>) payload.get("user");
        String host = (String) user.get("host");
        String hostForms = getAllForm(host);
        if(hostForms != null) {
            inputMap.put("result", hostForms);
            return true;
        } else {
            inputMap.put("result", "No form can be found.");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
