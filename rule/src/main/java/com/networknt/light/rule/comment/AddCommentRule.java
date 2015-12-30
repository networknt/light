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

import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.HashUtil;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by steve on 03/12/14.
 *
 * AccessLevel R [user]
 *
 */
public class AddCommentRule extends AbstractCommentRule implements Rule {
    static final Logger logger = LoggerFactory.getLogger(AddCommentRule.class);

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String host = (String)data.get("host");
        String parentRid = (String)data.get("@rid");
        String error = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            OrientVertex parent = (OrientVertex)DbService.getVertexByRid(graph, parentRid);
            if(parent == null ) {
                error = "Parent @rid " + parentRid + " cannot be found";
                inputMap.put("responseCode", 404);
            } else {
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.put("host", host);
                eventData.put("comment", data.get("comment"));
                String parentClassName = parent.getProperty("@class");
                eventData.put("parentClassName", parentClassName); // parent can be a post or a comment
                if("Post".equals(parentClassName)) {
                    eventData.put("parentId", parent.getProperty("postId"));
                } else {
                    eventData.put("parentId", parent.getProperty("commentId"));
                }
                // generate unique identifier
                eventData.put("commentId", HashUtil.generateUUID());
                eventData.put("createDate", new java.util.Date());
                eventData.put("createUserId", user.get("userId"));
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            return true;
        }
    }
}
