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
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Map;

/**
 * Created by steve on 03/12/14.
 */
public class AddCommentRule extends AbstractCommentRule implements Rule {

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String host = (String)data.get("host");
        String parentRid = (String)data.get("@rid");
        String comment = (String)data.get("comment");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(host == null || parentRid == null || comment == null) {
            error = "Host, parentRid and comment are required";
            inputMap.put("responseCode", 400);
        } else {
            if(payload == null) {
                error = "Login is required";
                inputMap.put("responseCode", 401);
            } else {
                Map<String, Object> user = (Map<String, Object>)payload.get("user");
                // make sure that the parent exists.
                ODocument parent = DbService.getODocumentByRid(parentRid);
                if(parent != null) {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    // identify parent.
                    eventData.put("host", host);
                    eventData.put("parentClassName", parent.getClassName());
                    eventData.put("parentId", parent.field("id"));
                    // generate unique identifier
                    eventData.put("id", DbService.incrementCounter("commentId"));
                    eventData.put("comment", comment);
                    eventData.put("createDate", new java.util.Date());
                    eventData.put("createUserId", user.get("userId"));
                } else {
                    error = "Parent with @rid " + parentRid + " doesn't exist";
                    inputMap.put("responseCode", 404);
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
