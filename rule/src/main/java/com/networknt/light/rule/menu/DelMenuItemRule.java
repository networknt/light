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

package com.networknt.light.rule.menu;

import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/29/2014.
 * AccessLevel R [owner, admin, menuAdmin]
 *
 */
public class DelMenuItemRule extends AbstractMenuRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String rid = (String)data.get("@rid");
        String error = null;
        String host = (String)user.get("host");
        if(host != null && !host.equals(data.get("host"))) {
            error = "User can only delete menuItem for host: " + host;
            inputMap.put("responseCode", 401);
        } else {
            OrientGraph graph = ServiceLocator.getInstance().getGraph();
            try {
                Vertex menuItem = DbService.getVertexByRid(graph, rid);
                if(menuItem == null) {
                    error = "MenuItem with @rid " + rid + " cannot be found";
                    inputMap.put("responseCode", 404);
                } else {
                    // find out if other menu or menuItem owns this menuItem
                    if(DbService.hasEdgeToClass(graph, (OrientVertex)menuItem, "Own") || DbService.hasEdgeToClass(graph, (OrientVertex)menuItem, "Own")) {
                        error = "MenuItem is referenced by other entities";
                        inputMap.put("responseCode", 400);
                    } else {
                        Map eventMap = getEventMap(inputMap);
                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                        inputMap.put("eventMap", eventMap);
                        eventData.put("menuItemId", menuItem.getProperty("menuItemId"));  // unique key
                    }
                }
            } catch (Exception e) {
                logger.error("Exception:", e);
                throw e;
            } finally {
                graph.shutdown();
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
