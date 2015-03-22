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

package com.networknt.light.rule.db;

import com.networknt.light.rule.Rule;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 15/02/15.
 *
 * Export db into a json file and download to local hard drive for backup. This
 * is for the entire database.
 *
 * TODO complete it.
 *
 * AccessLevel R [owner]
 *
 */
public class ExpDbRule extends AbstractDbRule implements Rule {

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");

        // export into a json in memory
        //
        // send to client for download

        // once it is done, then create an event for persistence

        Map eventMap = getEventMap(inputMap);
        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
        inputMap.put("eventMap", eventMap);
        eventData.put("createDate", new java.util.Date());
        eventData.put("createUserId", user.get("userId"));

        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            return true;
        }
    }
}
