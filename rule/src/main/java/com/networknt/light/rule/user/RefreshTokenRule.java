package com.networknt.light.rule.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.HashUtil;
import com.networknt.light.util.JwtUtil;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import net.oauth.jsontoken.JsonToken;

import java.time.Instant;
import java.util.*;

/**
 * Created by steve on 19/09/14.
 */
public class RefreshTokenRule extends AbstractUserRule implements Rule {

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String error = null;

        String refreshToken = (String)data.get("refreshToken");
        String userId = (String)data.get("userId");
        String host = (String)data.get("host");
        if(refreshToken == null || userId == null) {
            inputMap.put("responseCode", 401);
            error = "Refresh token or userId is missing";
        } else {
            ODocument user = getUserByUserId(userId);
            if(user != null) {
                ODocument credential = (ODocument) user.field("credential");
                if (checkRefreshToken(credential, host, refreshToken)) {
                    String jwt = generateToken(user);
                    if (jwt != null) {
                        Map<String, String> tokens = new HashMap<String, String>();
                        tokens.put("accessToken", jwt);
                        inputMap.put("result", mapper.writeValueAsString(tokens));
                    }
                } else {
                    error = "Invalid refresh token";
                    inputMap.put("responseCode", 400);
                }
            } else {
                error = "The userId " + userId + " has not been registered";
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
