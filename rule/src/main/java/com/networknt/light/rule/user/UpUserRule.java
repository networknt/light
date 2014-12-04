package com.networknt.light.rule.user;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.blog.AbstractBlogRule;
import com.networknt.light.server.DbService;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/17/2014.
 */
public class UpUserRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode",401);
        } else {
            Map<String,Object> voteUser = (Map<String, Object>)payload.get("user");
            String voteUserId = (String)voteUser.get("userId");
            String userRid = (String)data.get("@rid");
            ODocument user = DbService.getODocumentByRid(userRid);
            if(user == null) {
                error = "User with @rid " + userRid + " cannot be found";
                inputMap.put("responseCode", 404);
            } else {
                Map eventMap = getEventMap(inputMap);
                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                inputMap.put("eventMap", eventMap);
                eventData.put("userId", user.field("userId").toString());
                eventData.put("voteUserId", voteUserId);
                eventData.put("updateDate", new java.util.Date());
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
