package com.networknt.light.rule.user;

import com.networknt.light.rule.Rule;
import com.networknt.light.util.HashUtil;
import com.networknt.light.util.ServiceLocator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 8/29/2014.
 * need to check both email and displayName uniqueness.
 *
 */
public class SignUpUserRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String email = (String) data.get("email");
        String userId = (String) data.get("userId");
        String error = null;

        // need to make sure that email and userId are unique.
        // skip the cache and go to db directly?
        if(isUserInDbByEmail(email)) {
            error = "The email address " + email + " has been signed up. Please login or recover your password.";
            inputMap.put("responseCode", 400);
        } else {
            if(isUserInDbByUserId(userId)) {
                error = "The userId " + userId + " has been used by another user.";
                inputMap.put("responseCode", 400);
            } else {
                // check if password and password_confirm are the same.
                String password = (String) data.get("password");
                String passwordConfirm = (String)data.remove("passwordConfirm");
                // after schema validation in the backend password and password_confirm should not be empty.
                if(!password.equals(passwordConfirm)) {
                    error = "password and password confirm are not the same";
                    inputMap.put("responseCode", 400);
                } else {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.putAll(data);
                    // replace the password with the hashed password.
                    password = HashUtil.generateStorngPasswordHash(password);
                    eventData.put("password", password);
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
