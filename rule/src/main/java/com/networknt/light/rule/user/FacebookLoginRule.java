package com.networknt.light.rule.user;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.config.GetConfigRule;
import com.networknt.light.util.HashUtil;
import com.networknt.light.util.ServiceLocator;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.types.User;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve on 13/03/16.
 *
 * AccessLevel A
 */
public class FacebookLoginRule extends AbstractUserRule implements Rule {
    static final String FACEBOOK = "facebook";

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String host = (String)data.get("host");
        String clientId = (String)data.get("clientId");
        String token = (String)data.get("accessToken");
        String error = null;

        // https://developers.google.com/identity/sign-in/web/backend-auth#using-a-google-api-client-library
        // first need to verify and get user profile from access token
        GetConfigRule getConfigRule = new GetConfigRule();
        //String facebookAppId = getConfigRule.getConfig(host, FACEBOOK, "$.properties.appId");
        FacebookClient fbClient = new DefaultFacebookClient(token, Version.VERSION_2_5);
        User me = fbClient.fetchObject("me", User.class);

        if (me != null) {
            String email = me.getEmail();
            String firstName = me.getFirstName();
            String lastName = me.getLastName();
            String userId = me.getId();
            String name = me.getName();

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
                    // generate jwt token
                    String jwt = generateToken(user, clientId, false);
                    if(jwt != null) {
                        Map<String, Object> tokens = new HashMap<String, Object>();
                        tokens.put("accessToken", jwt);
                        tokens.put("rid", user.getIdentity().toString());
                        inputMap.put("result", mapper.writeValueAsString(tokens));
                    }

                    // save user
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);

                    eventData.put("clientId", clientId);
                    eventData.put("host", data.get("host"));
                    eventData.put("userId", userId + "@f");
                    eventData.put("email", email);
                    eventData.put("firstName", firstName);
                    eventData.put("lastName", lastName);
                }
            } catch (Exception e) {
                logger.error("Exception:", e);
                throw e;
            } finally {
                graph.shutdown();
            }
        } else {
            error = "Invalid facebook accessToken";
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