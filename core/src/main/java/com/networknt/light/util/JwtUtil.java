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
    final static String ISSUER = "networknt.com";
    final static String SIGNING_KEY = "1293089278894893893";
    public static String TOKEN_EXPIRED_MESSAGE = "Invalid iat and/or exp.";

    static VerifierProviders verifierProviders = null;
    static{
        try {
            final Verifier hmacVerifier = new HmacSHA256Verifier(SIGNING_KEY.getBytes());
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
        Map<String, Object> userMap = new LinkedHashMap<String, Object>();
        userMap.put("email", "stevehu@gmail.com");
        String jwt = getJwt(userMap);
        System.out.println("jwt = " + jwt);
        JsonToken token = Deserialize(jwt);
        System.out.println("token = " + token);
        token = VerifyAndDeserialize(jwt);
        System.out.println("token = " + token);
    }


    public static String getJwt(Map<String, Object> userMap) throws InvalidKeyException, SignatureException {
        JsonToken token = createToken(userMap);
        return token.serializeAndSign();
    }

    public static JsonToken createToken(Map<String, Object> userMap) throws InvalidKeyException {
        // Current time and signing algorithm
        HmacSHA256Signer signer = new HmacSHA256Signer(ISSUER, null, SIGNING_KEY.getBytes());

        // Configure JSON token with signer and SystemClock
        JsonToken token = new JsonToken(signer);
        token.setAudience("networknt.com");
        token.setParam("typ", "networknt.com/auth/v1");
        token.setIssuedAt(Instant.now());
        //token.setExpiration(Instant.now().plusSeconds(3600));  // 1 hour
        // TODO test only
        token.setExpiration(Instant.now().plusSeconds(1));  // 1 second + 2 minutes

        Map<String, Object> payload = token.getPayload();
        payload.put("user", userMap);
        return token;
    }

    public static JsonToken Deserialize(String jwt) throws Exception {
        JsonTokenParser parser = new JsonTokenParser(verifierProviders, new SignedTokenAudienceChecker("networknt.com"));
        return parser.deserialize(jwt);
    }
    public static JsonToken VerifyAndDeserialize(String jwt) throws Exception {
        JsonTokenParser parser = new JsonTokenParser(verifierProviders, new SignedTokenAudienceChecker("networknt.com"));
        return parser.verifyAndDeserialize(jwt);
    }
}
