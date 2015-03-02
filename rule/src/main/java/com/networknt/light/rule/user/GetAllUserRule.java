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
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by steve on 26/10/14.
 *
 * Get all users in a list for user admin page. pagination is supported in db level
 *
 * AccessLevel R [owner, admin, userAdmin]
 *
 */
public class GetAllUserRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String host = (String)user.get("host");
        if(host != null) {
            if(!host.equals(data.get("host"))) {
                inputMap.put("error", "You can only get all users from host: " + host);
                inputMap.put("responseCode", 403);
                return false;
            }
        } else {
            // retrieve everything as this is the owner
            data.remove("host"); // removed the host added by RestHandler.
        }
        OrientGraphNoTx graph = ServiceLocator.getInstance().getNoTxGraph();
        try {
            long total = getTotalNumberUserFromDb(graph, data);
            if(total > 0) {
                String json = getUserFromDb(graph, data);
                List<Map<String, Object>> users
                        = mapper.readValue(json, new TypeReference<List<HashMap<String, Object>>>() {});
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("total", total);
                result.put("users", users);
                inputMap.put("result", mapper.writeValueAsString(result));
                return true;
            } else {
                inputMap.put("error", "No user can be found.");
                inputMap.put("responseCode", 404);
                return false;
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
    }
}
