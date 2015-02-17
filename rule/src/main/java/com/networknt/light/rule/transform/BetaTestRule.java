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

package com.networknt.light.rule.transform;

import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 16/02/15.
 *
 * This is a transform rule that support Beta testing on production. When you change the API that is backward
 * compatible, you can test it on your local and then deploy it on production for testing. For certain loyal users
 * or employees, you can give them a role called betaTester in their profile. This rule will be applied before
 * API end point is reached, so that the end point can be version 1 which has no betaTester role and version 2 which
 * has betaTester role. In normal case, it will route to rule class HelloWorld.class or HelloWorld1.class if the user
 * is betaTester. Once beta testing is done, you can route all traffic to HelloWorld1.class. Later on, you want to change
 * the rule again you can create HelloWorld2.class and route betaTester to it. This can goes on and on.
 *
 * Please note: Beta testing is only for backward compatible changes. If not, one should have two versions of clients
 * to connect to two different API class in different packages.
 *
 * When set up this rule, you should have two entries in transformData beta and production to point to the right
 * class name of the rules.
 *
 * AccessLevel N as it is internal.
 *
 */
public class BetaTestRule extends AbstractRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(BetaTestRule.class);
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> transformData = (Map<String, Object>)inputMap.remove("transformData");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload != null) {
            Map<String, Object> user = (Map<String, Object>) payload.get("user");
            List roles = (List) user.get("roles");
            if (!roles.contains("betaTester")) {
                inputMap.put("name", transformData.get("beta"));
            } else {
                inputMap.put("name", transformData.get("production"));
            }
        } else {
            inputMap.put("name", transformData.get("production"));
        }
        return true;
    }

}
