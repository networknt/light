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

package com.networknt.light.rule.page;

import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/24/2014.
 *
 * AccessLevel R [owner, admin, pageAdmin]
 *
 */
public class DelPageRule extends AbstractPageRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String rid = (String) data.get("@rid");
        String host = (String) data.get("host");
        String error = null;
        String userHost = (String)user.get("host");
        if(userHost != null && !userHost.equals(host)) {
            error = "You can only delete page from host: " + host;
            inputMap.put("responseCode", 403);
        } else {
            OrientGraphNoTx graph = ServiceLocator.getInstance().getNoTxGraph();
            try {
                Vertex page = DbService.getVertexByRid(graph, rid);
                if(page != null) {
                    int inputVersion = (int)data.get("@version");
                    int storedVersion = page.getProperty("@version");
                    if(inputVersion != storedVersion) {
                        inputMap.put("responseCode", 400);
                        error = "Deleting version " + inputVersion + " doesn't match stored version " + storedVersion;
                    } else {
                        Map eventMap = getEventMap(inputMap);
                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                        inputMap.put("eventMap", eventMap);
                        eventData.put("pageId", page.getProperty("pageId"));
                    }
                } else {
                    error = "Page with @rid " + rid + " doesn't exist";
                    inputMap.put("responseCode", 400);
                }
            } catch (Exception e) {
                logger.error("Exception:", e);
                throw e;
            } finally {
                graph.shutdown();
            }
        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            inputMap.put("result", "success");
            return true;
        }
    }
}
