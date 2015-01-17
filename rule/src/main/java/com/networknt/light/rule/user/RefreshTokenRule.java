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
