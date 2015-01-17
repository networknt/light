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
import com.networknt.light.server.DbService;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.List;
import java.util.Map;

/**
 * Created by steve on 27/11/14.
 */
public class UpdPostRule extends AbstractPostRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String host = (String) data.get("host");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if (payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            ODocument post = DbService.getODocumentByRid(rid);
            if (post != null) {
                ODocument parent = post.field("parent");
                String className = parent.getClassName();
                Map<String, Object> user = (Map<String, Object>) payload.get("user");
                List roles = (List) user.get("roles");
                String userHost = (String) user.get("host");
                if (userHost != null && !userHost.equals(host)) {
                    error = "User can only update post from host: " + host;
                    inputMap.put("responseCode", 401);
                } else {
                    if ("Forum".equals(className)) {
                        // owner, admin, forumAdmin, createUser can update post
                        if (roles.contains("owner") || roles.contains("admin") || roles.contains("forumAdmin") || user.get("userId").equals(post.field("createUserId"))) {
                            createEventMap(inputMap, user, parent);
                        } else {
                            error = "Cannot update post";
                            inputMap.put("responseCode", 401);
                        }
                    } else if ("Blog".equals(className)) {
                        if (roles.contains("owner") || roles.contains("admin") || roles.contains("blogAdmin") || user.get("userId").equals(post.field("createUserId"))) {
                            createEventMap(inputMap, user, parent);
                        } else {
                            error = "Cannot update post";
                            inputMap.put("responseCode", 401);
                        }
                    } else if ("News".equals(className)) {
                        if (roles.contains("owner") || roles.contains("admin") || roles.contains("newsAdmin") || user.get("userId").equals(post.field("createUserId"))) {
                            createEventMap(inputMap, user, parent);
                        } else {
                            error = "Cannot update post";
                            inputMap.put("responseCode", 401);
                        }
                    } else {
                        error = "Invalid parent class for post";
                        inputMap.put("responseCode", 400);
                    }
                }
            } else {
                error = "Post with @rid " + rid + " doesn't exists";
                inputMap.put("responseCode", 404);
            }
        }
        if (error != null) {
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
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        eventData.putAll(data);
        eventData.put("parentClassName", parent.getClassName());
        eventData.put("parentHost", parent.field("host"));
        eventData.put("parentId", parent.field("id"));
        eventData.put("updateDate", new java.util.Date());
        eventData.put("updateUserId", user.get("userId"));
        eventData.remove("@rid");
    }

}
