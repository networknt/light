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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.util.JwtUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.oauth.jsontoken.JsonToken;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by husteve on 8/29/2014.
 */
public class SignUpUserRuleTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();
    String signInAdmin = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"www.example.com\",\"userIdEmail\":\"stevehu\",\"password\":\"123456\",\"rememberMe\":true,\"clientId\":\"example@Browser\"}}";
    String signUp = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signUpUser\",\"data\":{\"host\":\"www.example.com\",\"userId\":\"testuser\",\"email\":\"testuser@gmail.com\",\"password\":\"abcdefg\",\"passwordConfirm\":\"abcdefg\",\"firstName\":\"test\",\"lastName\":\"user\"}}";
    String signUpJsonPasswordDiff = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signUpUser\",\"data\":{\"host\":\"www.example.com\",\"userId\":\"tstuser\",\"email\":\"tstuser@gmail.com\",\"password\":\"abcdefg\",\"passwordConfirm\":\"12345\",\"firstName\":\"tst\",\"lastName\":\"user\"}}";
    String delUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"delUser\",\"data\":{\"host\":\"www.example.com\",\"userId\":\"testuser\"}}";

    public SignUpUserRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(SignUpUserRuleTest.class);
        return suite;
    }

    public void setUp() throws Exception { super.setUp(); }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testExecute() throws Exception {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        boolean ruleResult = false;
        JsonToken adminToken = null;

        try {
            // signIn admin by userId
            {
                jsonMap = mapper.readValue(signInAdmin,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                SignInUserRule valRule = new SignInUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                String json = (String) jsonMap.get("result");
                jsonMap = mapper.readValue(json,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                assertNotNull(jsonMap.get("refreshToken"));
                adminToken = JwtUtil.Deserialize((String) jsonMap.get("accessToken"));
                SignInUserEvRule rule = new SignInUserEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);
            }

            // del user user if it exists in case previous test failed and the user is not removed.
            {
                jsonMap = mapper.readValue(delUser,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> payload = adminToken.getPayload();
                jsonMap.put("payload", payload);

                DelUserRule valRule = new DelUserRule();
                ruleResult = valRule.execute(jsonMap);
                if (ruleResult) {
                    Map<String, Object> eventMap = (Map<String, Object>) jsonMap.get("eventMap");
                    DelUserEvRule rule = new DelUserEvRule();
                    ruleResult = rule.execute(eventMap);
                    assertTrue(ruleResult);
                }
            }

            // signUp user no error
            {
                jsonMap = mapper.readValue(signUp,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                SignUpUserRule rule = new SignUpUserRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                Map<String, Object> data = (Map<String, Object>)eventMap.get("data");
                String password = (String)data.get("password");
                assertFalse("abcdefg".equals(password));
                SignUpUserEvRule evRule = new SignUpUserEvRule();
                ruleResult = evRule.execute(eventMap);
                assertTrue(ruleResult);
            }
            // signUp user with error
            {
                jsonMap = mapper.readValue(signUpJsonPasswordDiff,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                SignUpUserRule rule = new SignUpUserRule();
                ruleResult = rule.execute(jsonMap);
                assertFalse(ruleResult);
                String error = (String)jsonMap.get("result");
                assertEquals("password and password confirm are not the same", error);
                int responseCode = (int)jsonMap.get("responseCode");
                assertEquals(400, responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
