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

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.blog.AbstractBlogRule;
import com.networknt.light.server.DbService;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 27/11/14.
 */
public class AddPostRule extends AbstractPostRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String host = (String)data.get("host");
        String parentRid = (String)data.get("@rid");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            // make sure parent rid is valid.
            ODocument parent = DbService.getODocumentByRid(parentRid);
            if (parent != null) {
                String className = parent.getClassName();
                Map<String, Object> user = (Map<String, Object>) payload.get("user");
                List roles = (List) user.get("roles");
                String userHost = (String) user.get("host");
                if (userHost != null && !userHost.equals(host)) {
                    error = "User can only add post from host: " + host;
                    inputMap.put("responseCode", 401);
                } else {
                    if ("Forum".equals(className)) {
                        // every login user can post in forum
                        createEventMap(inputMap, user, parent);
                    } else if ("Blog".equals(className)) {
                        if (!roles.contains("owner") && !roles.contains("admin") && !roles.contains("blogAdmin") && !roles.contains("blogUser")) {
                            error = "Role owner or admin or blogAdmin or blogUser is required to add post";
                            inputMap.put("responseCode", 401);
                        } else {
                            createEventMap(inputMap, user, parent);
                        }
                    } else if ("News".equals(className)) {
                        if (!roles.contains("owner") && !roles.contains("admin") && !roles.contains("newsAdmin") && !roles.contains("newsUser")) {
                            error = "Role owner or admin or newsAdmin or newsUser is required to add post";
                            inputMap.put("responseCode", 401);
                        } else {
                            createEventMap(inputMap, user, parent);
                        }
                    } else {
                        error = "Invalid parent class for post";
                        inputMap.put("responseCode", 400);
                    }
                }
            } else {
                error = "Parent doesn't exist";
                inputMap.put("responseCode", 400);
            }
        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }

    void createEventMap(Map<String, Object> inputMap, Map<String, Object> user, ODocument parent) {
        Map eventMap = getEventMap(inputMap);
        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
        inputMap.put("eventMap", eventMap);
        eventData.putAll((Map<String, Object>)inputMap.get("data"));
        // generate post Id from counter.
        eventData.put("id", DbService.incrementCounter("postId"));
        eventData.put("createDate", new java.util.Date());
        eventData.put("createUserId", user.get("userId"));
        // replace parent @rid with host and id
        eventData.remove("@rid");
        eventData.put("parentClassName", parent.getClassName());
        eventData.put("parentHost", parent.field("host"));
        eventData.put("parentId", parent.field("id"));
    }
}
