package com.networknt.light.rule.user;

import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.HashUtil;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 8/29/2014.
 */
public class UpdPasswordRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String error = null;
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload == null) {
            error = "Login is required";
            inputMap.put("responseCode", 401);
        } else {
            Map<String, Object> user = (Map<String, Object>)payload.get("user");
            String rid = (String)user.get("@rid");
            ODocument updateUser = DbService.getODocumentByRid(rid);
            if(updateUser != null) {
                String password = (String) data.get("password");
                String newPassword = (String)data.get("newPassword");
                String passwordConfirm = (String)data.get("passwordConfirm");

                // check if the password match
                boolean match = checkPassword(updateUser, password);
                if(match) {
                    if(newPassword.equals(passwordConfirm)) {
                        newPassword = HashUtil.generateStorngPasswordHash(newPassword);
                        Map eventMap = getEventMap(inputMap);
                        Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                        inputMap.put("eventMap", eventMap);
                        eventData.put("userId", updateUser.field("userId"));
                        eventData.put("password", newPassword);
                        eventData.put("updateDate", new java.util.Date());
                    } else {
                        error = "New password and password confirm are not the same.";
                        inputMap.put("responseCode", 400);
                    }
                } else {
                    error = "The old password is incorrect.";
                    inputMap.put("responseCode", 400);
                }
            } else {
                error = "User with rid " + rid + " cannot be found.";
                inputMap.put("responseCode", 404);
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
