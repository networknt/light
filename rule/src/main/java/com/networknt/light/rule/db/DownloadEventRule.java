package com.networknt.light.rule.db;

import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.sun.net.httpserver.Headers;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 21/12/14.
 */
public class DownloadEventRule extends AbstractDbRule implements Rule {

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            // everyone is allowed to download events performed by himself and replay it on
            // other site build with the same framework.

            // make sure that both from datetime and to datetime are in the past. And to datetime
            // can be optional which means get everything after from datetime.

            // Now let's build a criteria for db search.

            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(roles.contains("owner")) {
                // only owner can generate events for common components without host.
                data.remove("host");
            }
            data.put("createUserId", user.get("userId"));
            String json = DbService.getData("Event", data);
            if(json != null) {
                inputMap.put("result", json);
            } else {
                error = "No event can be found";
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
