package com.networknt.light.rule.forum;

import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 02/12/14.
 */
public class AddPostRule extends AbstractForumRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String parentId = (String) data.get("parentId");
        String host = (String) data.get("host");
        String title = (String) data.get("title");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            if(parentId == null || host == null || title == null) {
                error = "ParentId, Host and Title are requrired";
                inputMap.put("responseCode", 400);
            } else {
                //  make sure parent exists.
                ODocument forum = getODocumentByHostId("forumHostIdIdx", host, parentId);
                if(forum == null) {
                    error = "Forum with id " + parentId + " doesn't exist on host " + host;
                    inputMap.put("responseCode", 400);
                } else {
                    Map<String, Object> user = (Map<String, Object>)payload.get("user");
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.putAll((Map<String, Object>) inputMap.get("data"));
                    eventData.put("id", DbService.incrementCounter("postId"));
                    eventData.put("createDate", new java.util.Date());
                    eventData.put("createUserId", user.get("userId"));
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
