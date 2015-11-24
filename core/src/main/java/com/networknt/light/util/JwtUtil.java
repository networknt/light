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

package com.networknt.light.util;

import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.JsonTokenParser;
import net.oauth.jsontoken.crypto.HmacSHA256Signer;
import net.oauth.jsontoken.crypto.HmacSHA256Verifier;
import net.oauth.jsontoken.crypto.SignatureAlgorithm;
import net.oauth.jsontoken.crypto.Verifier;
import net.oauth.jsontoken.discovery.VerifierProvider;
import net.oauth.jsontoken.discovery.VerifierProviders;
import net.oauth.signatures.SignedTokenAudienceChecker;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steve on 14/09/14.
 */
public class JwtUtil {
    public static String TOKEN_EXPIRED_MESSAGE = "Invalid iat and/or exp.";
    static final String OAUTH2_CONFIG = "oauth2";
    static final Map<String, Object> oauth2Config = ServiceLocator.getInstance().getConfig(OAUTH2_CONFIG);

    static VerifierProviders verifierProviders = null;
    static{
        try {
            final Verifier hmacVerifier = new HmacSHA256Verifier(((String)oauth2Config.get("signingKey")).getBytes());
            VerifierProvider hmacLocator = new VerifierProvider() {
                @Override
                public List<Verifier> findVerifier(String signerId, String keyId) {
                    List<Verifier> list = new ArrayList<Verifier>();
                    list.add(hmacVerifier);
                    return list;
                }
            };
            verifierProviders = new VerifierProviders();
            verifierProviders.setVerifierProvider(SignatureAlgorithm.HS256, hmacLocator);

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String jwt = null;
        if(args != null && args.length == 1) {
            // there is a token passed in, deserializer it.
            jwt = args[0];
            JsonToken token = VerifyAndDeserialize(jwt);
            System.out.println("token=" + token);
        } else {
            // there is no parameter then generate a new token.
            Map<String, Object> userMap = new LinkedHashMap<String, Object>();
            userMap.put("userId", "stevehu");
            userMap.put("host", "www.networknt.com");
            userMap.put("client", "browser");
            // roles array
            ArrayList roles = new ArrayList();
            roles.add("user");
            roles.add("admin");
            userMap.put("roles", roles);
            jwt = getJwt(userMap);
            System.out.println("jwt = " + jwt);
        }
    }


    public static String getJwt(Map<String, Object> userMap) throws InvalidKeyException, SignatureException {
        JsonToken token = createToken(userMap);
        return token.serializeAndSign();
    }

    public static JsonToken createToken(Map<String, Object> userMap) throws InvalidKeyException {
        // Current time and signing algorithm
        HmacSHA256Signer signer = new HmacSHA256Signer((String)oauth2Config.get("issuer"),
            null, ((String)oauth2Config.get("signingKey")).getBytes());

        // Configure JSON token with signer and SystemClock
        JsonToken token = new JsonToken(signer);
        token.setAudience((String) oauth2Config.get("audience"));
        token.setParam("typ", (String)oauth2Config.get("typ"));
        token.setIssuedAt(Instant.now());
        token.setExpiration(Instant.now().plusSeconds((Integer)oauth2Config.get("expireInSecond")));
        Map<String, Object> payload = token.getPayload();
        payload.put("user", userMap);
        return token;
    }

    public static JsonToken Deserialize(String jwt) throws Exception {
        JsonTokenParser parser = new JsonTokenParser(verifierProviders, new SignedTokenAudienceChecker((String)oauth2Config.get("audience")));
        return parser.deserialize(jwt);
    }
    public static JsonToken VerifyAndDeserialize(String jwt) throws Exception {
        JsonTokenParser parser = new JsonTokenParser(verifierProviders, new SignedTokenAudienceChecker((String)oauth2Config.get("audience")));
        return parser.verifyAndDeserialize(jwt);
    }
}
