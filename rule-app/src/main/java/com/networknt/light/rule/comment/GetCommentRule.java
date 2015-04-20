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

package com.networknt.light.rule.comment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.Rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 03/12/14.
 *
 * Not sure if this is used or not. Do we have a comment admin page to list all of them?
 *
 */
public class GetCommentRule extends AbstractCommentRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("@class", "Comment");
        long total = getTotal(data, criteria);
        if(total > 0) {
            String json = getComment(data, criteria);
            List<Map<String, Object>> comments
                    = mapper.readValue(json, new TypeReference<List<HashMap<String, Object>>>() {});
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", total);
            result.put("comments", comments);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        } else {
            inputMap.put("result", "No user can be found.");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
