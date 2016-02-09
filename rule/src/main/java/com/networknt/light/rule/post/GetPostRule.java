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

package com.networknt.light.rule.post;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 27/11/14.
 *
 * Get a post from post table by entityId
 *
 * AccessLevel A  Anybody
 *
 */
public class GetPostRule extends AbstractPostRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String entityId = (String)data.get("entityId");
        if(entityId != null) {
            // convert entityId to entityRid first
            String entityRid = getEntityRid("Post", entityId);
            Map<String, Object> result = getCategoryEntity(entityRid);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        } else {
            inputMap.put("result", "entityId is required.");
            inputMap.put("responseCode", 400);
            return false;
        }
    }
}
