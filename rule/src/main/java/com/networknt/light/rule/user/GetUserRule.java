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

package com.networknt.light.rule.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve on 8/28/2014.
 * The password field will be remove before returning to the client.
 *
 */
public class GetUserRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String email = (String) data.get("email");
        String userId = (String) data.get("userId");
        if(rid != null) {
            ODocument doc = DbService.getODocumentByRid(rid);
            if(doc != null) {
                inputMap.put("result", doc.toJSON());
                return true;
            } else {
                inputMap.put("result", "User with rid " + rid + " cannot be found.");
                inputMap.put("responseCode", 404);
                return false;
            }
        } else if(userId != null) {
            ODocument user = getUserByUserId(userId);
            if(user != null) {
                inputMap.put("result", user.toJSON());
                return true;
            } else {
                inputMap.put("result", "User with userId " + userId + " cannot be found.");
                inputMap.put("responseCode", 404);
                return false;
            }
        } else if(email != null) {
            ODocument user = getUserByEmail(email);
            if(user != null) {
                inputMap.put("result", user.toJSON());
                return true;
            } else {
                inputMap.put("result", "User with email " + email + " cannot be found.");
                inputMap.put("responseCode", 404);
                return false;
            }
        } else {
            inputMap.put("result", "@rid or userId or email is required.");
            inputMap.put("responseCode", 400);
            return false;
        }
    }
}
