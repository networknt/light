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

import java.util.Map;

/**
 * Created by steve on 01/03/15.
 *
 * This is the rule that create or update schema during new application initial setup.
 * Note that unlike update command we can try it out without commit, this one we cannot.
 *
 */
public class ExecSchemaCmdRule extends AbstractDbRule implements Rule {

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        // make sure we have content payload here.
        String script = (String)data.get("script");
        Map eventMap = getEventMap(inputMap);
        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
        inputMap.put("eventMap", eventMap);
        eventData.put("script", script);
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
