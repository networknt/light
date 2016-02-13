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

import java.util.List;
import java.util.Map;

/**
 * Created by Steve Hu on 2015-01-19.
 *
 * This is only give you a way to update virtualhost.json in users directory, it won't
 * inject the new host into the virtualhosthandler. The server must be restarted in order
 * to load the newly added site.
 * TODO dynamically add a new host into virtualhosthandler without shutdonw server.
 *
 */
public class AddHostRule extends AbstractHostRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String error = null;

        // check if the host exists or not.
        Map<String, Object> hostMap = ServiceLocator.getInstance().getHostMap();
        if(hostMap.containsKey(data.get("hostId"))) {
            // host exists
            error = "HostId exists";
            inputMap.put("responseCode", 400);
            inputMap.put("result", error);
            return false;
        } else {
            Map eventMap = getEventMap(inputMap);
            Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
            inputMap.put("eventMap", eventMap);
            eventData.put("hostId", data.get("hostId"));
            eventData.put("base", data.get("base"));
            eventData.put("transferMinSize", data.get("transferMinSize"));
            eventData.put("createDate", new java.util.Date());
            eventData.put("createUserId", user.get("userId"));
            return true;
        }
    }
}
