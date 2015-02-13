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

package com.networknt.light.server.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.RuleEngine;
import com.networknt.light.rule.access.AbstractAccessRule;
import com.networknt.light.rule.access.GetAccessRule;
import com.networknt.light.server.DbService;
import com.networknt.light.server.ServerConstants;
import com.networknt.light.util.JwtUtil;
import com.networknt.light.util.ServiceLocator;
import com.networknt.light.util.Util;
import com.orientechnologies.orient.core.record.impl.ODocument;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import net.oauth.jsontoken.JsonToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by steve on 08/08/14.
 */
public class RestHandler implements HttpHandler {
    static final Logger logger = LoggerFactory.getLogger(RestHandler.class);

    public RestHandler() {
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if(exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        exchange.startBlocking();
        // check if it is get or post
        String json = null;
        if(Methods.GET.equals(exchange.getRequestMethod())) {
            Map params = exchange.getQueryParameters();
            String cmd = ((Deque<String>)params.get("cmd")).getFirst();
            json = URLDecoder.decode(cmd, "UTF8");
        } else if (Methods.POST.equals(exchange.getRequestMethod())) {
            json = new Scanner(exchange.getInputStream(),"UTF-8").useDelimiter("\\A").next();
        } else {
            logger.error("Invalid Request Method");
            exchange.setResponseCode(400);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
            exchange.getResponseSender().send((ByteBuffer.wrap("Invalid Request Method".getBytes("utf-8"))));
            return;
        }
        logger.debug("request json = {}", json);
        // TODO validate with json schema to make sure the input json is valid. return an error message otherwise.
        // you need to get the schema from db again for the one sent from browser might be modified.

        // get jwt payload, pass it to every rule and validate it if permission is required.
        // TODO check if login times in token is the same as in userMap.
        // this is to prevent stolen token after user is logged out.
        
        // if the token is expired, then you will have 401 token_expired sent back to angular.
        // Angular should intercept the response code and refresh the token and replay the same
        // request with renewed access token.
        HeaderMap headerMap = exchange.getRequestHeaders();
        Map<String, Object> payload = null;
        try {
            payload = getTokenPayload(headerMap);
        } catch (IllegalStateException e) {
            //e.printStackTrace();
            String msg = e.getMessage();
            if(msg != null && msg.startsWith(JwtUtil.TOKEN_EXPIRED_MESSAGE)) {
                // return 401 status and let client to refresh the token.
                exchange.setResponseCode(401);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                exchange.getResponseSender().send((ByteBuffer.wrap("token_expired".getBytes("utf-8"))));
                return;
            }
        }

        // convert json string to map here.
        Map<String, Object> jsonMap =
                ServiceLocator.getInstance().getMapper()
                        .readValue(json, new TypeReference<HashMap<String, Object>>() {});
        // TODO rewrite with case now check if the request is authorized to access the command rule.
        String cmdRuleClass = Util.getCommandRuleId(jsonMap);
        GetAccessRule rule = new GetAccessRule();
        Map<String, Object> access = rule.getAccessByRuleClass(cmdRuleClass);
        if(access != null) {
            // authorization rule available, check it.
            String accessLevel = (String)access.get("accessLevel");
            switch (accessLevel) {
                case "A":
                    // Access by anyone.
                    break;
                case "N":
                    // Not accessible
                    exchange.setResponseCode(400);
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                    exchange.getResponseSender().send((ByteBuffer.wrap("Not accessible".getBytes("utf-8"))));
                    return;
                case "C":
                    // client id is in the jwt token like userId and roles.
                    if(payload == null) {
                        exchange.setResponseCode(401);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                        exchange.getResponseSender().send((ByteBuffer.wrap("Login is required".getBytes("utf-8"))));
                        return;
                    } else {
                        Map<String, Object> user = (Map<String, Object>) payload.get("user");
                        String clientId = (String) user.get("clientId");
                        List<String> clients = (List)access.get("clients");
                        if(!clients.contains(clientId)) {
                            exchange.setResponseCode(403);
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                            exchange.getResponseSender().send((ByteBuffer.wrap("Client permission denied".getBytes("utf-8"))));
                            return;
                        }
                    }
                    break;
                case "R":
                    // role only
                    if(payload == null) {
                        exchange.setResponseCode(401);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                        exchange.getResponseSender().send((ByteBuffer.wrap("Login is required".getBytes("utf-8"))));
                        return;
                    } else {
                        Map<String, Object> user = (Map<String, Object>) payload.get("user");
                        List userRoles = (List) user.get("roles");
                        List<String> accessRoles = (List)access.get("roles");
                        boolean found = false;
                        for (String role : accessRoles) {
                            if (userRoles.contains(role)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            exchange.setResponseCode(403);
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                            exchange.getResponseSender().send((ByteBuffer.wrap("Role permission denied".getBytes("utf-8"))));
                            return;
                        }
                    }
                    break;
                case "U":
                    //user only
                    if(payload == null) {
                        exchange.setResponseCode(401);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                        exchange.getResponseSender().send((ByteBuffer.wrap("Login is required".getBytes("utf-8"))));
                        return;
                    } else {
                        Map<String, Object> user = (Map<String, Object>) payload.get("user");
                        String userId = (String) user.get("userId");
                        List<String> users = (List)access.get("users");
                        if(!users.contains(userId)) {
                            exchange.setResponseCode(403);
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                            exchange.getResponseSender().send((ByteBuffer.wrap("User permission denied".getBytes("utf-8"))));
                            return;
                        }
                    }
                    break;
                case "CR":
                    // client and role
                    if(payload == null) {
                        exchange.setResponseCode(401);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                        exchange.getResponseSender().send((ByteBuffer.wrap("Login is required".getBytes("utf-8"))));
                        return;
                    } else {
                        Map<String, Object> user = (Map<String, Object>) payload.get("user");
                        String clientId = (String) user.get("clientId");
                        List<String> clients = (List)access.get("clients");
                        if(!clients.contains(clientId)) {
                            exchange.setResponseCode(403);
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                            exchange.getResponseSender().send((ByteBuffer.wrap("Client permission denied".getBytes("utf-8"))));
                            return;
                        }
                        // client is ok, check roles
                        List userRoles = (List) user.get("roles");
                        List<String> accessRoles = (List)access.get("roles");
                        boolean found = false;
                        for (String role : accessRoles) {
                            if (userRoles.contains(role)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            exchange.setResponseCode(403);
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                            exchange.getResponseSender().send((ByteBuffer.wrap("Role permission denied".getBytes("utf-8"))));
                            return;
                        }
                    }
                    break;
                case "CU":
                    // client and user
                    if(payload == null) {
                        exchange.setResponseCode(401);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                        exchange.getResponseSender().send((ByteBuffer.wrap("Login is required".getBytes("utf-8"))));
                        return;
                    } else {
                        Map<String, Object> user = (Map<String, Object>) payload.get("user");
                        String clientId = (String) user.get("clientId");
                        List<String> clients = (List)access.get("clients");
                        if(!clients.contains(clientId)) {
                            exchange.setResponseCode(403);
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                            exchange.getResponseSender().send((ByteBuffer.wrap("Client permission denied".getBytes("utf-8"))));
                            return;
                        }
                        // client is ok, check user
                        String userId = (String) user.get("userId");
                        List<String> users = (List)access.get("users");
                        if(!users.contains(userId)) {
                            exchange.setResponseCode(403);
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                            exchange.getResponseSender().send((ByteBuffer.wrap("User permission denied".getBytes("utf-8"))));
                            return;
                        }
                    }
                    break;
                case "RU":
                    // role and user
                    if(payload == null) {
                        exchange.setResponseCode(401);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                        exchange.getResponseSender().send((ByteBuffer.wrap("Login is required".getBytes("utf-8"))));
                        return;
                    } else {
                        Map<String, Object> user = (Map<String, Object>) payload.get("user");
                        List userRoles = (List) user.get("roles");
                        List<String> accessRoles = (List)access.get("roles");
                        boolean found = false;
                        for (String role : accessRoles) {
                            if (userRoles.contains(role)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            exchange.setResponseCode(403);
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                            exchange.getResponseSender().send((ByteBuffer.wrap("Role permission denied".getBytes("utf-8"))));
                            return;
                        }
                        // role is OK, now check userId
                        String userId = (String) user.get("userId");
                        List<String> users = (List)access.get("users");
                        if(!users.contains(userId)) {
                            exchange.setResponseCode(403);
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                            exchange.getResponseSender().send((ByteBuffer.wrap("User permission denied".getBytes("utf-8"))));
                            return;
                        }
                   }
                    break;
                case "CRU":
                    // client, role and user
                    if(payload == null) {
                        exchange.setResponseCode(401);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                        exchange.getResponseSender().send((ByteBuffer.wrap("Login is required".getBytes("utf-8"))));
                        return;
                    } else {
                        Map<String, Object> user = (Map<String, Object>) payload.get("user");
                        String clientId = (String) user.get("clientId");
                        List<String> clients = (List)access.get("clients");
                        if(!clients.contains(clientId)) {
                            exchange.setResponseCode(403);
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                            exchange.getResponseSender().send((ByteBuffer.wrap("Client permission denied".getBytes("utf-8"))));
                            return;
                        }
                        // client is ok, check roles
                        List userRoles = (List) user.get("roles");
                        List<String> accessRoles = (List)access.get("roles");
                        boolean found = false;
                        for (String role : accessRoles) {
                            if (userRoles.contains(role)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            exchange.setResponseCode(403);
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                            exchange.getResponseSender().send((ByteBuffer.wrap("Role permission denied".getBytes("utf-8"))));
                            return;
                        }
                        // role is OK, now check userId
                        String userId = (String) user.get("userId");
                        List<String> users = (List)access.get("users");
                        if(!users.contains(userId)) {
                            exchange.setResponseCode(403);
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
                            exchange.getResponseSender().send((ByteBuffer.wrap("User permission denied".getBytes("utf-8"))));
                            return;
                        }
                    }
                    break;
                default:
                    logger.error("Invalid Access Level: " + accessLevel);
            }
        }

        if(payload != null) {
            // put payload as part of the map
            jsonMap.put("payload", payload);
        }


        // TODO should I remove the port number here?
        // inject host into the data in command if there is no host in the json map.
        // that means the rules to be accessed are common and host should be part of the data.
        // if there is host in the command, then the corresponding rule should be host specific
        // and table(s) to be accessed should be host specific as well.
        if(jsonMap.get("host") == null) {
            String host = headerMap.getFirst("host");
            if(host.indexOf(':') != -1) {
                host = host.substring(0, host.indexOf(":"));
            }
            Map<String, Object> data = (Map<String, Object>)jsonMap.get("data");
            if(data == null) {
                data = new HashMap<String, Object>();
                jsonMap.put("data", data);
                data.put("host", host);
            } else {
                // do not replace host as it might be owner doing something for one host from another.
                if(data.get("host") == null) {
                    data.put("host", host);
                }
            }
        }

        // inject ip address into the command here and saved as part of event in order to  identify
        // users that are not logged in.
        jsonMap.put("ipAddress", getIpAddress(exchange));

        boolean readOnly = (boolean)jsonMap.get("readOnly");
        // two types of rules (Command Rule and Event Rule)
        // Command Rule is responsible for validation and return result to client and enrich the command to event that
        // can be replayed at anytime without side effect.Once this is done, return to client. Also, it there are any
        // calls to the outside system, they should be performed here in order to avoid side effects.
        //
        // Event rule is responsible to update domain model based on the event enriched by Command Rule.

        if(readOnly) {
            // get data from memory and return it, it call a command rule to get the data and result will be in the jsonMap as "result" key
            // call the right rule to execute the command, return error code and message if validation fails.
            RuleEngine.getInstance().executeRule(Util.getCommandRuleId(jsonMap), jsonMap);
        } else {
            // this is a update command or impacting to external system.
            // TODO validate version number in itself and dependencies to make sure the event is valid. Reject it back if not.
            // TODO This is the command that need to be executed, and it should be done only once to eliminate side effect.
            // Once this is done, the jsonMap is enriched so that the event is created and can be replayed.
            // It is very possible that something needs to be returned, put it in "result" key in jsonMap.
            boolean valid = RuleEngine.getInstance().executeRule(Util.getCommandRuleId(jsonMap), jsonMap);
            if(valid) {
                // persist event into event store.
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                DbService.persistEvent(eventMap);
                // This is the async call to update domain model after the fact. Nothing should be returned from here.
                // Basically, this is to apply event generated by the Command Rule
                RuleEngine.getInstance().executeRuleAsync(Util.getEventRuleId(eventMap), eventMap);
            }
        }
        // orientdb most likely return ODocument and ODocument collection, convert to json string by rules
        String result  = null;
        Integer responseCode = (Integer)jsonMap.get("responseCode");
        if(responseCode != null) {
            // there is an error
            exchange.setResponseCode(responseCode);
            result = (String)jsonMap.get("error");
            logger.debug("response error: {}", result);
            // TODO should I log the response error here or in the response interceptor?
            // do I have all the info here?

        } else {
            //  no error
            result = (String)jsonMap.get("result");
            logger.debug("response success: {} ", result);
        }
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, ServerConstants.JSON_UTF8);
        if(result != null) {
            exchange.getResponseSender().send(ByteBuffer.wrap(result.getBytes("utf-8")));
        }
    }

    private JsonToken getToken(HeaderMap headerMap) throws Exception {

        JsonToken token = null;
        String authorization = headerMap.getFirst("authorization");
        if (authorization != null) {
            String[] parts = authorization.split(" ");
            if (parts.length == 2) {
                String scheme = parts[0];
                String credentials = parts[1];

                Pattern pattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);
                if (pattern.matcher(scheme).matches()) {
                    token = JwtUtil.VerifyAndDeserialize(credentials);
                }
            }
        }
        return token;
    }

    private Map<String, Object> getTokenPayload(HeaderMap headerMap) throws Exception {

        Map<String, Object> payload = null;

        String authorization = headerMap.getFirst("authorization");
        if (authorization != null) {
            String[] parts = authorization.split(" ");
            if (parts.length == 2) {
                String scheme = parts[0];
                String credentials = parts[1];

                Pattern pattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);
                if (pattern.matcher(scheme).matches()) {
                    payload = JwtUtil.VerifyAndDeserialize(credentials).getPayload();
                }
            }
        }
        return payload;
    }

    private String getIpAddress(HttpServerExchange exchange) {
        String ipAddress = null;
        HeaderMap headerMap = exchange.getRequestHeaders();
        ipAddress = headerMap.getFirst(Headers.X_FORWARDED_FOR_STRING);
        if(ipAddress == null) {
            logger.debug("could not get ip address from x forward for, try sourceAddress in exchange");
            ipAddress = exchange.getSourceAddress().getAddress().getHostAddress();
        }
        logger.debug("ip = {}", ipAddress);
        return ipAddress;
    }
}
