package com.networknt.light.rule.post;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.blog.AbstractBlogRule;
import com.networknt.light.server.DbService;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Map;

/**
 * Created by steve on 28/11/14.
 */
public class UpPostRule extends AbstractPostRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        String error = null;
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode",401);
        } else {
            String rid = (String)data.get("@rid");
            if(rid != null) {
                ODocument post = DbService.getODocumentByRid(rid);
                if(post == null) {
                    error ="Post with @rid " + rid + " cannot be found";
                    inputMap.put("responseCode", 404);
                } else {
                    Map<String,Object> user = (Map<String, Object>)payload.get("user");
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("id", post.field("id"));
                    eventData.put("updateDate", new java.util.Date());
                    eventData.put("updateUserId", user.get("userId"));
                }
            } else {
                error = "@rid is required";
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
}
