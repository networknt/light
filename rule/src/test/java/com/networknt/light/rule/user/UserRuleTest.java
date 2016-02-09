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
import com.networknt.light.server.DbService;
import com.networknt.light.util.JwtUtil;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.oauth.jsontoken.JsonToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 8/28/2014.
 */
public class UserRuleTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();
    String signInAdmin = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"www.example.com\",\"userIdEmail\":\"stevehu\",\"password\":\"123456\",\"rememberMe\":true,\"clientId\":\"example@Browser\"}}";

    String signUpUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signUpUser\",\"data\":{\"host\":\"www.example.com\",\"userId\":\"steve\",\"email\":\"steve@gmail.com\",\"password\":\"abcdefg\",\"passwordConfirm\":\"abcdefg\",\"firstName\":\"steve\",\"lastName\":\"hu\"}}";
    String activateUser = "{\"readOnly\":true,\"category\":\"user\",\"name\":\"activateUser\",\"data\":{\"host\":\"www.example.com\",\"userId\":\"steve\"}}";
    String getUserByEmail = "{\"readOnly\":true,\"category\":\"user\",\"name\":\"getUser\",\"data\":{\"host\":\"www.example.com\",\"email\":\"steve@gmail.com\"}}";
    String getUserByUserId = "{\"readOnly\":true,\"category\":\"user\",\"name\":\"getUser\",\"data\":{\"host\":\"www.example.com\",\"userId\":\"steve\"}}";
    String getUserByRid = "{\"readOnly\":true,\"category\":\"user\",\"name\":\"getUser\",\"data\":{\"host\":\"www.exmaple.com\"}}";

    String signInUserByEmail = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"www.example.com\",\"userIdEmail\":\"steve@gmail.com\",\"password\":\"abcdefg\",\"rememberMe\":false,\"clientId\":\"example@Browser\"}}";
    String signInUserByUserId = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"www.example.com\",\"userIdEmail\":\"steve\",\"password\":\"abcdefg\",\"rememberMe\":true,\"clientId\":\"example@Browser\"}}";
    String logOutUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"logOutUser\",\"data\":{\"host\":\"www.example.com\"}}";

    String updUserPassword = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"updPassword\",\"data\":{\"host\":\"www.example.com\",\"userId\":\"steve\",\"password\":\"abcdefg\",\"newPassword\":\"123456\",\"passwordConfirm\":\"123456\"}}";
    String signInUserNewPass = "{\"readOnly\":true,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"www.example.com\",\"userIdEmail\":\"steve@gmail.com\",\"password\":\"123456\",\"clientId\":\"example@Browser\"}}";

    String updUserProfile = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"updProfile\",\"data\":{\"host\":\"www.example.com\",\"userId\":\"steve\",\"firstName\":\"Steve\",\"lastName\":\"Hu\"}}";
    String updUserProfileAddShippingAddress = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"updProfile\",\"data\":{\"host\":\"www.example.com\",\"userId\":\"steve\",\"shippingAddress\":{\"city\":\"Mississauga\",\"postCode\":\"L5K2M3\"}}}";
    String updUserProfileUpdShippingAddress = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"updProfile\",\"data\":{\"host\":\"www.example.com\",\"userId\":\"steve\",\"shippingAddress\":{\"city\":\"Toronto\",\"province\":\"Ontario\",\"country\":\"Canada\",\"postCode\":\"L2L2L2\"}}}";
    String updUserProfileDelShippingAddressCountry = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"updProfile\",\"data\":{\"host\":\"www.example.com\",\"userId\":\"steve\",\"shippingAddress\":{\"city\":\"Toronto\",\"province\":\"Ontario\",\"postCode\":\"L2L2L2\"}}}";
    String updUserProfileAddPaymentAddress = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"updProfile\",\"data\":{\"host\":\"www.example.com\",\"userId\":\"steve\",\"paymentAddress\":{\"city\":\"Mississauga\",\"province\":\"Ontario\",\"postCode\":\"L5K2M3\"}}}";

    String delUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"delUser\",\"data\":{\"host\":\"www.example.com\",\"userId\":\"steve\"}}";

    String lockUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"lockUser\",\"data\":{\"host\":\"www.example.com\",\"userId\":\"steve\"}}";
    String unlockUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"unlockUser\",\"data\":{\"host\":\"www.example.com\",\"userId\":\"steve\"}}";
    String addRole = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"addRole\",\"data\":{\"host\":\"www.example.com\",\"role\":\"tester\"}}";
    String delRole = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"delRole\",\"data\":{\"host\":\"www.example.com\",\"role\":\"tester\"}}";

    String upUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"upUser\",\"data\":{\"host\":\"www.example.com\"}}";
    String downUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"downUser\",\"data\":{\"host\":\"www.example.com\"}}";

    public UserRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(UserRuleTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testVoid() {

    }
    /*
    public void testExecute() throws Exception {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        boolean ruleResult = false;
        JsonToken adminToken = null;
        JsonToken userToken = null;
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
                adminToken = JwtUtil.Deserialize((String)jsonMap.get("accessToken"));
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

            // signUp user
            {
                jsonMap = mapper.readValue(signUpUser,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                SignUpUserRule valRule = new SignUpUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>) jsonMap.get("eventMap");
                SignUpUserEvRule rule = new SignUpUserEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);
            }
            // get activate code
            String activationCode = null;
            {
                ActivateUserRule rule = new ActivateUserRule();
                Map<String, Object> data = (Map)jsonMap.get("data");
                String userId = (String)data.get("userId");
                activationCode = rule.getActivationCode(userId);
            }
            // activate user
            {
                jsonMap = mapper.readValue(activateUser,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> data = (Map)jsonMap.get("data");
                data.put("code", activationCode);
                ActivateUserRule valRule = new ActivateUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
            }
            // get user by email
            {
                jsonMap = mapper.readValue(getUserByEmail,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                GetUserRule rule = new GetUserRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                System.out.println("result = " + result);
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                assertEquals("steve@gmail.com", jsonMap.get("email"));
                assertEquals("steve", jsonMap.get("userId"));
            }
            // get user by userId
            {
                jsonMap = mapper.readValue(getUserByUserId,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                GetUserRule rule = new GetUserRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                System.out.println("result = " + result);
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                assertEquals("steve@gmail.com", jsonMap.get("email"));
                assertEquals("steve", jsonMap.get("userId"));
            }

            // signIn user by email
            {
                jsonMap = mapper.readValue(signInUserByEmail,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                SignInUserRule valRule = new SignInUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                assertNotNull(jsonMap.get("result"));
                String result = (String) jsonMap.get("result");
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                userToken = JwtUtil.Deserialize((String)jsonMap.get("accessToken"));
                SignInUserEvRule rule = new SignInUserEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);
                result = (String) jsonMap.get("result");
            }

            // get user by rid
            {
                jsonMap = mapper.readValue(getUserByRid,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> data = (Map<String, Object>) jsonMap.get("data");
                Map<String, Object> payload = userToken.getPayload();
                Map<String, Object> user = (Map<String, Object>) payload.get("user");
                data.put("@rid", user.get("@rid"));
                GetUserRule rule = new GetUserRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                System.out.println("result = " + result);
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                assertEquals("steve@gmail.com", jsonMap.get("email"));
                assertEquals("steve", jsonMap.get("userId"));
            }

            // logout user
            {
                jsonMap = mapper.readValue(logOutUser,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> data = (Map<String, Object>) jsonMap.get("data");
                Map<String, Object> payload = userToken.getPayload();
                Map<String, Object> user = (Map<String, Object>) payload.get("user");
                data.put("@rid", user.get("@rid"));
                LogOutUserRule valRule = new LogOutUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                LogOutUserEvRule rule = new LogOutUserEvRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);

            }

            // signIn user by userId
            {
                jsonMap = mapper.readValue(signInUserByUserId,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                SignInUserRule valRule = new SignInUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                assertNotNull(jsonMap.get("result"));
                String result = (String) jsonMap.get("result");
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                userToken = JwtUtil.Deserialize((String)jsonMap.get("accessToken"));
                assertNotNull(jsonMap.get("refreshToken"));
                SignInUserEvRule rule = new SignInUserEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);
            }

            // upd password
            {
                jsonMap = mapper.readValue(updUserPassword,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> data = (Map<String, Object>) jsonMap.get("data");
                Map<String, Object> payload = userToken.getPayload();
                jsonMap.put("payload", payload);
                UpdPasswordRule valRule = new UpdPasswordRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                UpdPasswordEvRule rule = new UpdPasswordEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);

            }
            // signIn user with new password
            {
                jsonMap = mapper.readValue(signInUserNewPass,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                SignInUserRule valRule = new SignInUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                userToken = JwtUtil.Deserialize((String) jsonMap.get("accessToken"));
                assertNull(jsonMap.get("refreshToken"));
                SignInUserEvRule rule = new SignInUserEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);

            }
            // upd profile
            {
                jsonMap = mapper.readValue(updUserProfile,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> data = (Map<String, Object>) jsonMap.get("data");
                Map<String, Object> payload = userToken.getPayload();
                jsonMap.put("payload", payload);
                UpdProfileRule valRule = new UpdProfileRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                UpdProfileEvRule rule = new UpdProfileEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);

            }
            // get user to check firstName and lastName updated
            {
                jsonMap = mapper.readValue(getUserByUserId,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                GetUserRule rule = new GetUserRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                System.out.println("result = " + result);
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                assertEquals("Steve", jsonMap.get("firstName"));
                assertEquals("Hu", jsonMap.get("lastName"));
            }
            // Update profile with shipping address added
            {
                jsonMap = mapper.readValue(updUserProfileAddShippingAddress,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> data = (Map<String, Object>) jsonMap.get("data");
                Map<String, Object> payload = userToken.getPayload();
                jsonMap.put("payload", payload);
                UpdProfileRule valRule = new UpdProfileRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                UpdProfileEvRule rule = new UpdProfileEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);

            }
            // get user to check shipping address
            {
                jsonMap = mapper.readValue(getUserByUserId,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                GetUserRule rule = new GetUserRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                System.out.println("result = " + result);
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> shippingAddress = (Map)jsonMap.get("shippingAddress");
                assertEquals("Mississauga", shippingAddress.get("city"));
                assertEquals("L5K2M3", shippingAddress.get("postCode"));
            }

            // update profile with shipping address updated
            {
                jsonMap = mapper.readValue(updUserProfileUpdShippingAddress,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> data = (Map<String, Object>) jsonMap.get("data");
                Map<String, Object> payload = userToken.getPayload();
                jsonMap.put("payload", payload);
                UpdProfileRule valRule = new UpdProfileRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                UpdProfileEvRule rule = new UpdProfileEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);

            }
            // get user to check shipping address
            {
                jsonMap = mapper.readValue(getUserByUserId,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                GetUserRule rule = new GetUserRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                System.out.println("result = " + result);
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> shippingAddress = (Map)jsonMap.get("shippingAddress");
                assertEquals("Toronto", shippingAddress.get("city"));
                assertEquals("L2L2L2", shippingAddress.get("postCode"));
                assertEquals("Ontario", shippingAddress.get("province"));
                assertEquals("Canada", shippingAddress.get("country"));
            }

            // update profile with shipping address removed country
            {
                jsonMap = mapper.readValue(updUserProfileDelShippingAddressCountry,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> data = (Map<String, Object>) jsonMap.get("data");
                Map<String, Object> payload = userToken.getPayload();
                jsonMap.put("payload", payload);
                UpdProfileRule valRule = new UpdProfileRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                UpdProfileEvRule rule = new UpdProfileEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);

            }
            // get user to check shipping address
            {
                jsonMap = mapper.readValue(getUserByUserId,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                GetUserRule rule = new GetUserRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                System.out.println("result = " + result);
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> shippingAddress = (Map)jsonMap.get("shippingAddress");
                assertEquals("Toronto", shippingAddress.get("city"));
                assertEquals("L2L2L2", shippingAddress.get("postCode"));
                assertEquals("Ontario", shippingAddress.get("province"));
                assertNull(shippingAddress.get("country"));
            }

            // update profile with payment address added
            {
                jsonMap = mapper.readValue(updUserProfileAddPaymentAddress,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> data = (Map<String, Object>) jsonMap.get("data");
                Map<String, Object> payload = userToken.getPayload();
                jsonMap.put("payload", payload);
                UpdProfileRule valRule = new UpdProfileRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                UpdProfileEvRule rule = new UpdProfileEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);

            }
            // get user to check payment address
            {
                jsonMap = mapper.readValue(getUserByUserId,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                GetUserRule rule = new GetUserRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                System.out.println("result = " + result);
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> paymentAddress = (Map)jsonMap.get("paymentAddress");
                assertEquals("Mississauga", paymentAddress.get("city"));
                assertEquals("Ontario", paymentAddress.get("province"));
                assertEquals("L5K2M3", paymentAddress.get("postCode"));
            }


            // lock user
            {
                jsonMap = mapper.readValue(lockUser,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> data = (Map<String, Object>) jsonMap.get("data");
                Map<String, Object> payload = userToken.getPayload();
                Map<String, Object> user = (Map<String, Object>) payload.get("user");
                data.put("@rid", user.get("@rid"));
                payload = adminToken.getPayload();
                jsonMap.put("payload", payload);

                LockUserRule rule = new LockUserRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                LockUserEvRule evRule = new LockUserEvRule();
                ruleResult = evRule.execute(eventMap);
                assertTrue(ruleResult);

            }
            // lock user again that fails
            {
                jsonMap = mapper.readValue(lockUser,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> data = (Map<String, Object>) jsonMap.get("data");
                Map<String, Object> payload = userToken.getPayload();
                Map<String, Object> user = (Map<String, Object>) payload.get("user");
                data.put("@rid", user.get("@rid"));
                payload = adminToken.getPayload();
                jsonMap.put("payload", payload);

                LockUserRule valRule = new LockUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertFalse(ruleResult);
            }

            // unlock user
            {
                jsonMap = mapper.readValue(unlockUser,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> data = (Map<String, Object>) jsonMap.get("data");
                Map<String, Object> payload = userToken.getPayload();
                Map<String, Object> user = (Map<String, Object>) payload.get("user");
                data.put("@rid", user.get("@rid"));
                payload = adminToken.getPayload();
                jsonMap.put("payload", payload);

                UnlockUserRule valRule = new UnlockUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                UnlockUserEvRule rule = new UnlockUserEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);

            }

            // unlock user again that fails
            {
                jsonMap = mapper.readValue(unlockUser,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> data = (Map<String, Object>) jsonMap.get("data");
                Map<String, Object> payload = userToken.getPayload();
                Map<String, Object> user = (Map<String, Object>) payload.get("user");
                data.put("@rid", user.get("@rid"));
                payload = adminToken.getPayload();
                jsonMap.put("payload", payload);

                UnlockUserRule valRule = new UnlockUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertFalse(ruleResult);

            }

            // up vote by admin
            {

                jsonMap = mapper.readValue(upUser,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> data = (Map<String, Object>) jsonMap.get("data");
                Map<String, Object> user = (Map<String, Object>) userToken.getPayload().get("user");
                data.put("@rid", user.get("@rid"));

                Map<String, Object> payload = adminToken.getPayload();
                jsonMap.put("payload", payload);

                UpUserRule valRule = new UpUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                UpUserEvRule rule = new UpUserEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);

                // check upUsers and downUsers
                String b = DbService.getJsonByRid((String) user.get("@rid"));
                jsonMap = mapper.readValue(b,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                List upList = (List) jsonMap.get("in_UpVote");
                assertEquals(1, upList.size());
                List downList = (List) jsonMap.get("in_DownVote");
                assertNull(downList);

                // up vote again and it fails
                jsonMap = mapper.readValue(upUser,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                data = (Map<String, Object>) jsonMap.get("data");
                user = (Map<String, Object>) userToken.getPayload().get("user");
                data.put("@rid", user.get("@rid"));
                payload = adminToken.getPayload();
                jsonMap.put("payload", payload);

                valRule = new UpUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertFalse(ruleResult);

                // check admin upUsers and downUsers
                b = DbService.getJsonByRid((String) user.get("@rid"));
                jsonMap = mapper.readValue(b,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                upList = (List) jsonMap.get("in_UpVote");
                assertEquals(1, upList.size());
                downList = (List) jsonMap.get("in_DownVote");
                assertNull(downList);
            }

            // down vote by admin
            {

                jsonMap = mapper.readValue(downUser,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> data = (Map<String, Object>) jsonMap.get("data");
                Map<String, Object> user = (Map<String, Object>) userToken.getPayload().get("user");
                data.put("@rid", user.get("@rid"));

                Map<String, Object> payload = adminToken.getPayload();
                jsonMap.put("payload", payload);

                DownUserRule valRule = new DownUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                DownUserEvRule rule = new DownUserEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);

                // check upUsers and downUsers
                String b = DbService.getJsonByRid((String) user.get("@rid"));
                jsonMap = mapper.readValue(b,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                List upList = (List) jsonMap.get("in_UpVote");
                assertEquals(0, upList.size());
                List downList = (List) jsonMap.get("in_DownVote");
                assertEquals(1, downList.size());

                // down vote again and nothing should be changed.
                jsonMap = mapper.readValue(downUser,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                data = (Map<String, Object>) jsonMap.get("data");
                user = (Map<String, Object>) userToken.getPayload().get("user");
                data.put("@rid", user.get("@rid"));
                payload = adminToken.getPayload();
                jsonMap.put("payload", payload);

                valRule = new DownUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertFalse(ruleResult);

                // check upUsers and downUsers
                b = DbService.getJsonByRid((String) user.get("@rid"));
                jsonMap = mapper.readValue(b,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                upList = (List) jsonMap.get("in_UpVote");
                assertEquals(0, upList.size());
                downList = (List) jsonMap.get("in_DownVote");
                assertEquals(1, downList.size());
            }

            // del user
            {
                jsonMap = mapper.readValue(delUser,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                Map<String, Object> payload = adminToken.getPayload();
                jsonMap.put("payload", payload);

                DelUserRule valRule = new DelUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                DelUserEvRule rule = new DelUserEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);

            }
            // get user to check it is gone
            {
                jsonMap = mapper.readValue(getUserByUserId,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                GetUserRule rule = new GetUserRule();
                ruleResult = rule.execute(jsonMap);
                assertFalse(ruleResult);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

}