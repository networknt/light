package com.networknt.light.rule.user;

import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/17/2014.
 */
public class UnlockUserRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String rid = (String) data.get("@rid");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(!roles.contains("owner") && !roles.contains("admin") && !roles.contains("userAdmin")) {
                error = "Role owner or admin or userAdmin is required to unlock user";
                inputMap.put("responseCode", 401);
            } else {
                String host = (String)user.get("host");
                if(host != null && !host.equals(data.get("host"))) {
                    error = "User can only unlock user from host: " + host;
                    inputMap.put("responseCode", 401);
                } else {
                    ODocument lockUser = null;
                    if(rid != null) {
                        lockUser = DbService.getODocumentByRid(rid);
                        if(lockUser != null) {
                            if(lockUser.field("locked") != null && !(Boolean)lockUser.field("locked")) {
                                error = "User with @rid " + rid + " is not locked";
                                inputMap.put("responseCode", 400);
                            } else {
                                Map eventMap = getEventMap(inputMap);
                                Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                                inputMap.put("eventMap", eventMap);
                                eventData.put("userId", lockUser.field("userId"));
                                eventData.put("locked", false);
                                eventData.put("updateDate", new java.util.Date());
                                eventData.put("updateUserId", user.get("userId"));
                            }
                        } else {
                            error = "User with @rid " + rid + " cannot be found.";
                            inputMap.put("responseCode", 404);
                        }
                    } else {
                        error = "@rid is required";
                        inputMap.put("responseCode", 400);
                    }
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
