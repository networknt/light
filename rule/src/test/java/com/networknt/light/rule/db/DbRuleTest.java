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

package com.networknt.light.rule.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.user.SignInUserEvRule;
import com.networknt.light.rule.user.SignInUserRule;
import com.networknt.light.util.JwtUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.oauth.jsontoken.JsonToken;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve on 11/12/14.
 */
public class DbRuleTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();

    String signInOwner = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"stevehu\",\"password\":\"123456\",\"rememberMe\":true,\"clientId\":\"example@Browser\"}}";
    String signInUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"test\",\"password\":\"123456\",\"rememberMe\":true,\"clientId\":\"example@Browser\"}}";

    String addSchema = "{\"readOnly\":false,\"category\":\"db\",\"name\":\"execSchemaCmd\",\"data\":{\"script\":\"create class Test extends V;\\\\ncreate property Test.name STRING;\\\\ncreate index Test.name UNIQUE;\"}}";
    String addVertex = "{\"readOnly\":false,\"category\":\"db\",\"name\":\"execUpdateCmd\",\"data\":{\"script\":\"create vertex Test set name = 'fiat';\\\\ncreate vertex Test set name = 'fed';\"}}";
    String queryVertex = "{\"readOnly\":true,\"category\":\"db\",\"name\":\"execQueryCmd\",\"data\":{\"script\":\"select from Test;\"}}";
    String delVertex = "{\"readOnly\":false,\"category\":\"db\",\"name\":\"execUpdateCmd\",\"data\":{\"script\":\"delete vertex Test where name = 'fiat';\\\\ndelete vertex Test where name = 'fed';\"}}";
    String delSchema = "{\"readOnly\":false,\"category\":\"db\",\"name\":\"execSchemaCmd\",\"data\":{\"script\":\"drop class Test;\"}}";

    public DbRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(DbRuleTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }


    public void testExecute() throws Exception {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        boolean ruleResult = false;
        try {
            JsonToken ownerToken = null;
            JsonToken userToken = null;
            // signIn owner by userId
            {
                jsonMap = mapper.readValue(signInOwner,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                SignInUserRule valRule = new SignInUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>) jsonMap.get("eventMap");
                String result = (String) jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                String jwt = (String) jsonMap.get("accessToken");
                ownerToken = JwtUtil.Deserialize(jwt);
                SignInUserEvRule rule = new SignInUserEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);
            }
            // signIn user by userId
            {
                jsonMap = mapper.readValue(signInUser,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                SignInUserRule valRule = new SignInUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>) jsonMap.get("eventMap");
                String result = (String) jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                String jwt = (String) jsonMap.get("accessToken");
                userToken = JwtUtil.Deserialize(jwt);
                SignInUserEvRule rule = new SignInUserEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);
            }

            // execute commands by remove Test class to clean up first.
            {
                jsonMap = mapper.readValue(delSchema,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                ExecSchemaCmdRule rule = new ExecSchemaCmdRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>) jsonMap.get("eventMap");
                ExecSchemaCmdEvRule evRule = new ExecSchemaCmdEvRule();
                ruleResult = evRule.execute(eventMap);
                //assertTrue(ruleResult);
            }

            // execute commands to add Test class, property and index
            {
                jsonMap = mapper.readValue(addSchema,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                ExecSchemaCmdRule rule = new ExecSchemaCmdRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>) jsonMap.get("eventMap");
                ExecSchemaCmdEvRule evRule = new ExecSchemaCmdEvRule();
                ruleResult = evRule.execute(eventMap);
                assertTrue(ruleResult);
            }

            // execute commands to add Vertex
            {
                jsonMap = mapper.readValue(addVertex,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                ExecUpdateCmdRule rule = new ExecUpdateCmdRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>) jsonMap.get("eventMap");
                ExecUpdateCmdEvRule evRule = new ExecUpdateCmdEvRule();
                ruleResult = evRule.execute(eventMap);
                assertTrue(ruleResult);
            }

            /*
            TODO query doen't work yet.
            // execute commands to query Vertex
            {
                jsonMap = mapper.readValue(queryVertex,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                ExecQueryCmdRule rule = new ExecQueryCmdRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                System.out.println("result = " + result);
            }
            */

            // execute commands to delete Vertex
            {
                jsonMap = mapper.readValue(delVertex,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                ExecUpdateCmdRule rule = new ExecUpdateCmdRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>) jsonMap.get("eventMap");
                ExecUpdateCmdEvRule evRule = new ExecUpdateCmdEvRule();
                ruleResult = evRule.execute(eventMap);
                assertTrue(ruleResult);
            }
            // execute commands by remove Test class
            {
                jsonMap = mapper.readValue(delSchema,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                ExecSchemaCmdRule rule = new ExecSchemaCmdRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>) jsonMap.get("eventMap");
                ExecSchemaCmdEvRule evRule = new ExecSchemaCmdEvRule();
                ruleResult = evRule.execute(eventMap);
                assertTrue(ruleResult);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}