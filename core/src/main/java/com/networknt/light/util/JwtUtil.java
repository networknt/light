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

import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.resolvers.X509VerificationKeyResolver;
import org.jose4j.lang.JoseException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Created by steve on 14/09/14.
 *
 * The following command is used to create private key and public key certificate
 *
 * keytool -genkey -alias jwt -keyalg RSA -keystore jwt.jks -validity 720 -keysize 2048
 *
 * jwt token
 * www.networknt.com
 * Network New Technologies Inc.
 * Toronto
 * Ontario
 * CA
 *
 *
 * keytool -certreq -alias jwt -keystore jwt.jks -file jwt.crt
 * keytool -exportcert -rfc -alias jwt -keystore jwt.jks -file jwt.crt
 *
 * to verify the certificate
 * keytool -printcert -v -file jwt.crt
 */
public class JwtUtil {
    static final XLogger logger = XLoggerFactory.getXLogger(JwtUtil.class);

    public static String TOKEN_EXPIRED_MESSAGE = "Invalid iat and/or exp.";
    static final String OAUTH2_CONFIG = "oauth2";
    static final String KEYSTORE_NAME = "keystoreName";
    static final String KEYSTORE_PASS = "keystorePass";
    static final String PRIVATE_KEY_NAME = "privateKeyName";
    static final String PRIVATE_KEY_PASS = "privateKeyPass";
    static final String CERTIFICATE = "certificate";
    static final String ISSUER = "issuer";
    static final String CLOCK_SKEW_IN_MINUTE = "clockSkewInMinute";
    static final String AUDIENCE = "audience";
    static final String SUBJECT = "subject";
    static final String EXPIRE_IN_MINUTE = "expireInMinute";
    static final String REMEMBER_ME_MINUTE = "rememberMeMinute";

    static final Map<String, Object> config = ServiceLocator.getInstance().getJsonMapConfig(OAUTH2_CONFIG);
    static final String issuer = (String)config.get(ISSUER);
    static final int clockSkewMin = (Integer)config.get(CLOCK_SKEW_IN_MINUTE);
    static final String audience = (String)config.get(AUDIENCE);
    static final String subject = (String)config.get(SUBJECT);
    static final int expireMin = (Integer)config.get(EXPIRE_IN_MINUTE);
    static final int rememberMin = (Integer)config.get(REMEMBER_ME_MINUTE);

    static X509Certificate certificate = null;
    static PrivateKey privateKey = null;

    public static X509Certificate readCertificate(String filename) {
        logger.entry(filename);
        InputStream inStream = null;
        X509Certificate cert = null;
        try {
            inStream = JwtUtil.class.getResourceAsStream(filename);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate)cf.generateCertificate(inStream);
        } catch (Exception e) {
            logger.error("Exception: ", e);
            System.exit(-1);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException ioe) {
                    logger.error("Exception: ", ioe);
                }
            }
        }
        logger.exit(cert);
        return cert;
    }

    public static PrivateKey readPrivateKey(String storeName, String storePass, String keyName, String keyPass) {
        PrivateKey privateKey = null;
        try {
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(JwtUtil.class.getResourceAsStream(storeName),
                    storePass.toCharArray());
            privateKey = (PrivateKey) keystore.getKey(keyName,
                    keyPass.toCharArray());
        } catch (Exception e) {
            logger.error("Exception: ", e);
            System.exit(-1);
        }

        if (privateKey == null) {
            System.err.println("Failed to retrieve private key " + keyName + " from keystore " + storeName);
            System.exit(-1);
        }
        return privateKey;
    }

    static {
        // we need both private keys and certificates to be cached. If primary one doesn't work,
        // use the secondary ones.
        String storeName = (String)config.get(KEYSTORE_NAME);
        String storePass = (String)config.get(KEYSTORE_PASS);
        String keyName = (String)config.get(PRIVATE_KEY_NAME);
        String keyPass = (String)config.get(PRIVATE_KEY_PASS);
        String certName = (String)config.get(CERTIFICATE);

        certificate = readCertificate(certName);
        privateKey = readPrivateKey(storeName, storePass, keyName, keyPass);

    }

    public static void main(String[] args) throws Exception {
        String jwt = null;
        if(args != null && args.length == 1) {
            // there is a token passed in, deserializer it.
            jwt = args[0];
            Map<String, Object> user = verifyJwt(jwt);
            System.out.println("user =" + user);
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
            jwt = getJwt(userMap, false);
            System.out.println("jwt = " + jwt);
        }
    }

    public static String getJwt(Map<String, Object> userMap, Boolean rememberMe) throws JoseException {
        String jwt = null;
        JwtClaims claims = new JwtClaims();
        claims.setIssuer(issuer);
        claims.setAudience(audience);
        claims.setExpirationTimeMinutesInTheFuture(rememberMe ? rememberMin : expireMin);
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();
        claims.setNotBeforeMinutesInThePast(clockSkewMin);
        claims.setSubject(subject);

        claims.setClaim("userId", userMap.get("userId"));
        claims.setClaim("clientId", userMap.get("clientId"));
        claims.setStringListClaim("roles", (List<String>)userMap.get("roles"));
        if(userMap.get("host") != null) claims.setClaim("host", userMap.get("host"));
        JsonWebSignature jws = new JsonWebSignature();

        // The payload of the JWS is JSON content of the JWT Claims
        jws.setPayload(claims.toJson());

        // The JWT is signed using the sender's private key
        jws.setKey(privateKey);

        // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        // Sign the JWS and produce the compact serialization, which will be the inner JWT/JWS
        // representation, which is a string consisting of three dot ('.') separated
        // base64url-encoded parts in the form Header.Payload.Signature
        jwt = jws.getCompactSerialization();
        //System.out.println("JWT: " + jwt);

        return jwt;
    }

    public static Map<String, Object> verifyJwt(String jwt) throws InvalidJwtException, MalformedClaimException {
        Map<String, Object> user = null;
        X509VerificationKeyResolver x509VerificationKeyResolver = new X509VerificationKeyResolver(certificate);
        x509VerificationKeyResolver.setTryAllOnNoThumbHeader(true);

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime() // the JWT must have an expiration time
                .setAllowedClockSkewInSeconds((Integer) config.get(CLOCK_SKEW_IN_MINUTE)*60) // allow some leeway in validating time based claims to account for clock skew
                .setRequireSubject() // the JWT must have a subject claim
                .setExpectedIssuer(issuer)
                .setExpectedAudience(audience)
                .setVerificationKeyResolver(x509VerificationKeyResolver) // verify the signature with the certificates
                .build(); // create the JwtConsumer instance

        //  Validate the JWT and process it to the Claims
        JwtClaims claims = jwtConsumer.processToClaims(jwt);
        if(claims != null) {
            user = new HashMap<String, Object>();
            user.put("userId", claims.getClaimValue("userId"));
            user.put("clientId", claims.getClaimValue("clientId"));
            List roles = claims.getStringListClaimValue("roles");
            user.put("roles", roles);
            Object host = claims.getClaimValue("host");
            if(host != null) user.put("host", host);
        }
        return user;
    }
}
