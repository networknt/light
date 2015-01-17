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

package com.networknt.light.rule.injector.main.feed;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/3/2014.
 */
public class InjAllFeedRule extends FeedRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        // make sure payload is not null. If you have payload that means the token is valid and not expired.
        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode",401);
        } else {
            String environment = (String)data.get("environment");
            String strFeeds = (String)data.get("feeds");
            ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
            List<Map<String, Object>> feeds = mapper.readValue(strFeeds,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            // put the json objects for async rule to save the feeds.
            data.put("feeds", feeds);
            for (Map<String, Object> feed : feeds) {
                feed.put("environment",environment);
                if("CLASS".equals(feed.get("dataFeedType"))) {
                    InjClassFeedRule valRule = new InjClassFeedRule();
                    Map<String, Object> map = new HashMap<>();
                    map.put("data", feed);
                    map.put("payload", payload);
                    valRule.execute(map);
                } else {
                    error = "dataFeedType " + feed.get("dataFeedType") + " is not supported.";
                    inputMap.put("responseCode", 400);
                }
            }
        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }
}
