package com.networknt.light.server.handler;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.util.JwtUtil;
import com.networknt.light.util.ServiceLocator;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.Pattern;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;


/**
 * Created by steve on 08/08/14.
 */
public class GraphqlHandler implements HttpHandler {
    static final Logger logger = LoggerFactory.getLogger(GraphqlHandler.class);
    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();

    public GraphqlHandler() {
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
        Map<String, Object> jsonMap = null;
        Map<String, Object> result = new LinkedHashMap<>();

        if(Methods.GET.equals(exchange.getRequestMethod())) {
            Map params = exchange.getQueryParameters();
            logger.debug("GET is called");
            //String cmd = ((Deque<String>)params.get("cmd")).getFirst();
            //json = URLDecoder.decode(cmd, "UTF8");
        } else if (Methods.POST.equals(exchange.getRequestMethod())) {
            logger.debug("POST is called");
            json = new Scanner(exchange.getInputStream(),"UTF-8").useDelimiter("\\A").next();
        } else if (Methods.OPTIONS.equals(exchange.getRequestMethod())) {
            // This is CORS preflight request and it will be handled here instead of forward to
            // the individual rule.
            exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");
            exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Methods"), "GET, POST");
            exchange.getResponseHeaders().put(new HttpString("Access-Control-Max-Age"), "3600");
            exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Headers"),
                    "Authorization,Content-Type,Accept,Origin,User-Agent,DNT,Cache-Control,X-Mx-ReqToken,Keep-Alive,X-Requested-With,If-Modified-Since");
            exchange.getResponseHeaders().put(new HttpString("Content-Type"), "application/json; charset=utf-8");
            return;
        } else {
            //sendErrorResponse(exchange, 400, "Invalid Request Method");
            logger.error("Invalid Request Method: " + exchange.getRequestMethod());
            return;
        }

        if(logger.isDebugEnabled()) {
            logger.debug("request json = {}", json);
        }

        // convert json string to map here. It it cannot be converted, then it is invalid command.
        try {
            jsonMap = ServiceLocator.getInstance().getMapper()
                    .readValue(json, new TypeReference<HashMap<String, Object>>() {
                    });
        } catch (Exception e) {
            logger.error("Invalid Command {} ", json);
            return;
        }
        String query = (String)jsonMap.get("query");
        String variables = (String)jsonMap.get("variables");
        if(variables != null && variables.length() > 0) {

        }

        GraphQLObjectType queryType = newObject()
                .name("helloWorldQuery")
                .field(newFieldDefinition()
                        .type(GraphQLString)
                        .name("gogo")
                        .staticValue("world is in")
                        .build())
                .build();

        GraphQLSchema schema = GraphQLSchema.newSchema()
                .query(queryType)
                .build();
        Object obj = new GraphQL(schema).execute(query).getData();
        result.put("data", obj);

        if(result != null && !exchange.isResponseComplete()) {
            exchange.getResponseSender().send(ByteBuffer.wrap(mapper.writeValueAsBytes(result)));
        }
    }


    private Map<String, Object> getTokenUser(HeaderMap headerMap) throws Exception {

        Map<String, Object> user = null;

        String authorization = headerMap.getFirst("authorization");
        if (authorization != null) {
            String[] parts = authorization.split(" ");
            if (parts.length == 2) {
                String scheme = parts[0];
                String credentials = parts[1];

                Pattern pattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);
                if (pattern.matcher(scheme).matches()) {
                    logger.debug("jwt = " + credentials);
                    user = JwtUtil.verifyJwt(credentials);
                }
            }
        }
        return user;
    }

    private String getIpAddress(HttpServerExchange exchange) {
        String ipAddress = null;
        HeaderMap headerMap = exchange.getRequestHeaders();
        ipAddress = headerMap.getFirst(Headers.X_FORWARDED_FOR_STRING);
        if(ipAddress == null) {
            ipAddress = exchange.getSourceAddress().getAddress().getHostAddress();
        }
        return ipAddress;
    }

}
