package com.networknt.light.rule.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.HashUtil;
import com.networknt.light.util.JwtUtil;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.time.Instant;
import java.util.*;

/**
 * Created by steve on 14/09/14.
 *
 */
public class SignInUserRule extends AbstractUserRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String userIdEmail = (String) data.get("userIdEmail");
        String inputPassword = (String) data.get("password");
        Boolean rememberMe = (Boolean)data.get("rememberMe");

        String error = null;
        ODocument user = null;
        if(isEmail(userIdEmail)) {
            user = getUserByEmail(userIdEmail);
        } else {
            user = getUserByUserId(userIdEmail);
        }
        if(user != null) {
            if(checkPassword(user, inputPassword)) {
                String jwt = generateToken(user);
                if(jwt != null) {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    Map<String, String> tokens = new HashMap<String, String>();
                    tokens.put("accessToken", jwt);
                    if(rememberMe != null && rememberMe) {
                        // generate refreshToken
                        String refreshToken = HashUtil.generateUUID();
                        tokens.put("refreshToken", refreshToken);
                        String hashedRefreshToken = HashUtil.generateStorngPasswordHash(refreshToken);
                        eventData.put("hashedRefreshToken", hashedRefreshToken);
                        eventData.put("issueDate", new java.util.Date());
                        Instant expireDate = Instant.now().plusSeconds(60*60*24*365); // default to 1 year.
                        eventData.put("expireDate", java.util.Date.from(expireDate));
                    }
                    inputMap.put("result", mapper.writeValueAsString(tokens));
                    eventData.put("userId", user.field("userId"));
                    eventData.put("logInDate", new java.util.Date());
                }
            } else {
                error = "Invalid password";
                inputMap.put("responseCode", 400);
            }
        } else {
            error = "Invalid userId or email";
            inputMap.put("responseCode", 400);
        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }
}
