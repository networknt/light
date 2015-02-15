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
package com.networknt.light.rule.client;

import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;

import java.util.*;

/**
 * Created by steve on 31/01/15.
 * Now, we only support Browser, Android and iOS
 *
 * AccessLevel R [user]
 */
public class GetClientDropdownRule extends AbstractRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Set<String> keys = ServiceLocator.getInstance().getHostMap().keySet();
        for(String key: keys) {
            Map<String, Object> hostMap = (Map<String, Object>)ServiceLocator.getInstance().getHostMap().get(key);
            List<String> supportDevices = (List)hostMap.get("supportDevices");
            for(String device: supportDevices) {
                String client = key + "@" + device;
                Map<String, String> map = new HashMap<String, String>();
                map.put("label", client);
                map.put("value", client);
                list.add(map);
            }
        }
        String clientDropdown = mapper.writeValueAsString(list);
        if(clientDropdown != null) {
            inputMap.put("result", clientDropdown);
            return true;
        } else {
            inputMap.put("result", "No client can be found.");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
