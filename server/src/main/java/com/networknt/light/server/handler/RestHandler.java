package com.networknt.light.server.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.server.DbService;
import com.networknt.light.server.ServerConstants;
import com.networknt.light.rule.RuleEngine;
import com.networknt.light.util.JwtUtil;
import com.networknt.light.util.ServiceLocator;
import com.networknt.light.util.Util;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormEncodedDataDefinition;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import net.oauth.jsontoken.JsonToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        String json = new Scanner(exchange.getInputStream(),"UTF-8").useDelimiter("\\A").next();
        logger.debug("request json = {}", json);
        // TODO validate with json schema to make sure the input json is valid. return an error message otherwise.
        // you need to get the schema from db again for the one sent from browser might be modified.

        // get jwt payload, pass it to every rule and validate it if permission is required.
        // TODO check if login times in token is the same as in userMap.
        // this is to prevent stolen token after user is logged out.
        
        // if the token is expired, then you will have an exception here. angular should be responsible to renew token
        // during the session and in init method to make sure no expired token going to be passed here.
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

        // get jwt payload, pass it to every rule and validate it if permission is required.
        // TODO First check if token is expired, second check if login time in token is the same as in userMap.
        // this is to prevent stolen token after user is logged out.
        // Angular needs to make sure no expired token will be sent.

        //"payload": {
        //    "iss": "networknt.com",
        //            "aud": "networknt.com",
        //            "typ": "networknt.com/auth/v1",
        //            "iat": 1411136595,
        //            "exp": 1411140195,
        //            "user": {
        //        "email": "steve@gmail.com"
        //    }
        //}

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
            host = host.substring(0, host.indexOf(":"));
            Map<String, Object> data = (Map<String, Object>)jsonMap.get("data");
            if(data == null) {
                data = new HashMap<String, Object>();
                jsonMap.put("data", data);
            }
            data.put("host", host);
        }

        boolean readOnly = (boolean)jsonMap.get("readOnly");
        // two types of rules (Command Rule and Event Rule)
        // Command Rule is responsible for validation and return result to client and enrich the command to event that
        // can be replayed at anytime without side effect.Once this is done, return to client.
        //
        // Event rule is responsible to update domain model based on the event enriched by Command Rule.

        if(readOnly) {
            // get data from memory and return it, it call a command rule to get the data and result will be in the jsonMap as "result" key
            // call the right rule to execute the command, return error code and message if validation fails.
            RuleEngine.getInstance().executeRule(Util.getCommandRuleClass(jsonMap), jsonMap);
        } else {
            // this is a update command or impacting to external system.
            // TODO validate version number in itself and dependencies to make sure the event is valid. Reject it back if not.
            // TODO This is the command that need to be executed, and it should be done only once to eliminate side effect.
            // Once this is done, the jsonMap is enriched so that the event is created and can be replayed.
            // It is very possible that something needs to be returned, put it in "result" key in jsonMap.
            boolean valid = RuleEngine.getInstance().executeRule(Util.getCommandRuleClass(jsonMap), jsonMap);
            if(valid) {
                // persist event into event store.
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                DbService.persistEvent(eventMap);
                // This is the async call to update domain model after the fact. Nothing should be returned from here.
                // Basically, this is to apply event generated by the Command Rule
                RuleEngine.getInstance().executeRuleAsync(Util.getEventRuleClass(eventMap), eventMap);
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

}
