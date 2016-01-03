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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.RuleEngine;
import com.networknt.light.util.ServiceLocator;
import com.networknt.light.util.Util;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 14/12/14.
 *
 * Replay event file to create or recreate aggregation. This rule does update db but
 * there is no EvRule available. This is a very special rule or endpoint.
 *
 * Remember, the update has been done long ago and this is just replaying them again to rebuilt database.
 *
 * AccessLevel R [owner, admin, dbAdmin]
 *
 * Current AccessLevel R [owner] TODO access control for host
 */
public class ReplayEventRule implements Rule {
    static final org.slf4j.Logger logger = LoggerFactory.getLogger(ReplayEventRule.class);
    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains("dbAdmin")) {
                error = "Role owner or admin or dbAdmin is required to replay events";
                inputMap.put("responseCode", 403);
            } else {
                String content = (String)data.get("content");
                // content may contains several events, parse it.
                List<Map<String, Object>> events = mapper.readValue(content,
                    new TypeReference<List<HashMap<String, Object>>>() {});
                // clear all cache before replay. in the future it might be clear only the category
                // that the events involved. TODO
                ServiceLocator.getInstance().clearMemoryImage();
                // replay event one by one.
                for(Map<String, Object> event: events) {
                    RuleEngine.getInstance().executeRule(Util.getEventRuleId(event), event);
                }
            }
        }
        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            return true;
        }
    }
}
