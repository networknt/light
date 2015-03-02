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

package com.networknt.light.rule.role;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.user.SignInUserEvRule;
import com.networknt.light.rule.user.SignInUserRule;
import com.networknt.light.util.JwtUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.oauth.jsontoken.JsonToken;

import java.util.*;

/**
 * Created by steve on 01/11/14.
 */
public class RoleRuleTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();
    String signInOwner = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"stevehu\",\"password\":\"123456\",\"rememberMe\":true,\"clientId\":\"example@Browser\"}}";
    String signInUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"test\",\"password\":\"123456\",\"rememberMe\":true,\"clientId\":\"example@Browser\"}}";

    String getRole = "{\"readOnly\":true,\"category\":\"role\",\"name\":\"getRole\"}";

    String addRoleCommon = "{\"readOnly\":false,\"category\":\"role\",\"name\":\"addRole\",\"data\":{\"roleId\":\"commonRole\",\"host\":\"injector\",\"desc\":\"This is just a test role\"}}";
    String addRoleExample = "{\"readOnly\":false,\"category\":\"role\",\"name\":\"addRole\",\"data\":{\"roleId\":\"exampleRole\",\"host\":\"www.example.com\",\"desc\":\"This is just a test role\"}}";
    String addRoleInjector = "{\"readOnly\":false,\"category\":\"role\",\"name\":\"addRole\",\"data\":{\"roleId\":\"injectorRole\",\"host\":\"injector\",\"desc\":\"This is just a test role\"}}";
    String updRole = "{\"readOnly\": false, \"category\": \"role\", \"name\": \"updRole\"}";
    String delRole = "{\"readOnly\": false, \"category\": \"role\", \"name\": \"delRole\"}";

    public RoleRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(RoleRuleTest.class);
        return suite;
    }

    public void setUp() throws Exception { super.setUp(); }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testExecute() throws Exception {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        boolean ruleResult = false;
        try {
            JsonToken ownerToken = null;
            JsonToken userToken = null;
            Map<String, Object> adminExamplePayload = null;
            Map<String, Object> adminInjectorPayload = null;
            // signIn owner by userId
            {
                jsonMap = mapper.readValue(signInOwner,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                SignInUserRule valRule = new SignInUserRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                String result = (String)jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                String jwt = (String)jsonMap.get("accessToken");
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
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                String result = (String)jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                String jwt = (String)jsonMap.get("accessToken");
                userToken = JwtUtil.Deserialize(jwt);
                SignInUserEvRule rule = new SignInUserEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);

                adminExamplePayload = new HashMap<String, Object>();
                Map<String, Object> adminExampleUser = new LinkedHashMap<String, Object>((Map)userToken.getPayload().get("user"));
                List roles = new ArrayList();;
                roles.add("admin");
                adminExampleUser.put("roles", roles);
                adminExampleUser.put("host", "www.example.com");
                adminExamplePayload.put("user", adminExampleUser);

                adminInjectorPayload = new HashMap<String, Object>();
                Map<String, Object> adminInjectorUser = new LinkedHashMap<String, Object>((Map)userToken.getPayload().get("user"));
                roles = new ArrayList();;
                roles.add("admin");
                adminInjectorUser.put("roles", roles);
                adminInjectorUser.put("host", "injector");
                adminInjectorPayload.put("user", adminInjectorUser);

            }

            // get roles
            {
                jsonMap = mapper.readValue(getRole,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());

                GetRoleRule rule = new GetRoleRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                System.out.println("result = " + result);
            }

            // del commonRole, injectorRole and exampleRole if they are in db to prepare test re-run if failed.
            {
                DelRoleRule delRoleRule = new DelRoleRule();
                DelRoleEvRule delRoleEvRule = new DelRoleEvRule();
                String commonRole = delRoleRule.getRoleById("commonRole");
                if(commonRole != null) {
                    jsonMap = mapper.readValue(delRole,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("payload", ownerToken.getPayload());
                    jsonMap.put("data", mapper.readValue(commonRole,
                            new TypeReference<HashMap<String, Object>>() {
                            }));
                    ruleResult = delRoleRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    ruleResult = delRoleEvRule.execute(eventMap);
                    assertTrue(ruleResult);
                }

                String injectorRole = delRoleRule.getRoleById("injectorRole");
                if(injectorRole != null) {
                    jsonMap = mapper.readValue(delRole,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("payload", ownerToken.getPayload());
                    jsonMap.put("data", mapper.readValue(injectorRole,
                            new TypeReference<HashMap<String, Object>>() {
                            }));
                    ruleResult = delRoleRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    ruleResult = delRoleEvRule.execute(eventMap);
                    assertTrue(ruleResult);
                }

                String exampleRole = delRoleRule.getRoleById("exampleRole");
                if(exampleRole != null) {
                    jsonMap = mapper.readValue(delRole,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("payload", ownerToken.getPayload());
                    jsonMap.put("data", mapper.readValue(exampleRole,
                            new TypeReference<HashMap<String, Object>>() {
                            }));
                    ruleResult = delRoleRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    ruleResult = delRoleEvRule.execute(eventMap);
                    assertTrue(ruleResult);
                }
            }

            // add commonRole by owner
            {
                jsonMap = mapper.readValue(addRoleCommon,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                AddRoleRule addRoleRule = new AddRoleRule();
                ruleResult = addRoleRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                AddRoleEvRule addRoleEvRule = new AddRoleEvRule();
                ruleResult = addRoleEvRule.execute(eventMap);
                assertTrue(ruleResult);
            }

            // add injectorRole by adminExample and it failed
            // add injectorRole by adminInjector
            {
                jsonMap = mapper.readValue(addRoleInjector,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminExamplePayload);
                AddRoleRule addRoleRule = new AddRoleRule();
                ruleResult = addRoleRule.execute(jsonMap);
                assertFalse(ruleResult);
                jsonMap = mapper.readValue(addRoleInjector,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminInjectorPayload);
                addRoleRule = new AddRoleRule();
                ruleResult = addRoleRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                AddRoleEvRule addRoleEvRule = new AddRoleEvRule();
                ruleResult = addRoleEvRule.execute(eventMap);
                assertTrue(ruleResult);
            }

            // add exampleRole by adminExample
            {
                jsonMap = mapper.readValue(addRoleExample,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminExamplePayload);
                AddRoleRule addRoleRule = new AddRoleRule();
                ruleResult = addRoleRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                AddRoleEvRule addRoleEvRule = new AddRoleEvRule();
                ruleResult = addRoleEvRule.execute(eventMap);
                assertTrue(ruleResult);

            }

            // update commonRole by adminExample and it failed
            {
                UpdRoleRule updRoleRule = new UpdRoleRule();
                UpdRoleEvRule updRoleEvRule = new UpdRoleEvRule();
                String commonRole = updRoleRule.getRoleById("commonRole");
                if(commonRole != null) {
                    jsonMap = mapper.readValue(updRole,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("payload", adminExamplePayload);
                    jsonMap.put("data", mapper.readValue(commonRole,
                            new TypeReference<HashMap<String, Object>>() {
                            }));
                    ruleResult = updRoleRule.execute(jsonMap);
                    assertFalse(ruleResult);
                }
            }

            // update exampleRole by adminExample
            {
                UpdRoleRule updRoleRule = new UpdRoleRule();
                UpdRoleEvRule updRoleEvRule = new UpdRoleEvRule();
                String exampleRole = updRoleRule.getRoleById("exampleRole");
                if(exampleRole != null) {
                    jsonMap = mapper.readValue(updRole,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("payload", adminExamplePayload);
                    jsonMap.put("data", mapper.readValue(exampleRole,
                            new TypeReference<HashMap<String, Object>>() {
                            }));
                    ruleResult = updRoleRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    ruleResult = updRoleEvRule.execute(eventMap);
                    assertTrue(ruleResult);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
