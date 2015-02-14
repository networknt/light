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

package com.networknt.light.rule.host;

import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Steve Hu on 2015-01-19.
 */
public class GetAllHostRule extends AbstractHostRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>) payload.get("user");
        List roles = (List)user.get("roles");
        if(roles.contains("owner")) {
            // flatten the set to array with all the elements of the host.
            List hosts = new ArrayList<Map<String, Object>>();
            Map hostMap = ServiceLocator.getInstance().getHostMap();
            Set<String> keys = hostMap.keySet();
            for(String key : keys) {
                Map valueMap = (Map<String, Object>)hostMap.get(key);
                valueMap.put("id", key);
                hosts.add(valueMap);
            }
            inputMap.put("result", mapper.writeValueAsString(hosts));
            return true;
        } else {
            inputMap.put("result", "Permission denied");
            inputMap.put("responseCode", 403);
            return false;
        }
    }
}
