package com.networknt.light.rule.user;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.config.GetConfigRule;
import com.networknt.light.util.HashUtil;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve on 13/03/16.
 *
 * AccessLevel A
 */
public class GoogleLoginRule extends AbstractUserRule implements Rule {
    static final String GOOGLE = "google";

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String host = (String)data.get("host");
        String clientId = (String)data.get("clientId");
        String token = (String)data.get("id_token");
        String error = null;

        // https://developers.google.com/identity/sign-in/web/backend-auth#using-a-google-api-client-library
        // first need to verify and get user profile from access token
        GetConfigRule getConfigRule = new GetConfigRule();
        String googleClientId = getConfigRule.getConfig(host, GOOGLE, "$.properties.client_id");

        // Set up the HTTP transport and JSON factory
        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Arrays.asList(googleClientId))
                // If you retrieved the token on Android using the Play Services 8.3 API or newer, set
                // the issuer to "https://accounts.google.com". Otherwise, set the issuer to
                // "accounts.google.com". If you need to verify tokens from multiple sources, build
                // a GoogleIdTokenVerifier for each issuer and try them both.
                .setIssuer("https://accounts.google.com")
                .build();

        GoogleIdToken idToken = verifier.verify(token);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            // generate networknt access token here.
            OrientGraph graph = ServiceLocator.getInstance().getGraph();
            try {
                OrientVertex user = null;
                user = (OrientVertex)getUserByEmail(graph, email);
                if(user != null) {
                    // check if the user is locked or not
                    if (user.getProperty("locked") != null && (boolean)user.getProperty("locked")) {
                        error = "Account is locked";
                        inputMap.put("responseCode", 400);
                    } else {
                        // for third party login, won't remember it.
                        String jwt = generateToken(user, clientId, false);
                        if(jwt != null) {
                            Map<String, Object> tokens = new HashMap<String, Object>();
                            tokens.put("accessToken", jwt);
                            tokens.put("rid", user.getIdentity().toString());
                            inputMap.put("result", mapper.writeValueAsString(tokens));
                        }
                    }
                } else {
                    // Generate access token
                    String jwt = generateToken(user, clientId, false);
                    if(jwt != null) {
                        Map<String, Object> tokens = new HashMap<String, Object>();
                        tokens.put("accessToken", jwt);
                        tokens.put("rid", user.getIdentity().toString());
                        inputMap.put("result", mapper.writeValueAsString(tokens));
                    }
                    // save user info into db.
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("clientId", clientId);
                    eventData.put("host", data.get("host"));
                    eventData.put("userId", userId + "@g");
                    eventData.put("email", email);
                    eventData.put("firstName", givenName);
                    eventData.put("lastName", familyName);
                }
            } catch (Exception e) {
                logger.error("Exception:", e);
                throw e;
            } finally {
                graph.shutdown();
            }
        } else {
            error = "Invalid google id_token";
            inputMap.put("responseCode", 401);
        }
        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            return true;
        }
    }
}
