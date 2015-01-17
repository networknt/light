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

package com.networknt.light.rule.page;

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
 * Created by husteve on 10/24/2014.
 */
public class PageRuleTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();

    String signInOwner = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"userIdEmail\":\"stevehu\",\"password\":\"123456\",\"rememberMe\":true}}";
    String signInTest = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"userIdEmail\":\"test\",\"password\":\"123456\",\"rememberMe\":true}}";

    String addPage = "{\"readOnly\":false,\"category\":\"page\",\"name\":\"addPage\",\"data\":{\"id\":\"com.networknt.light.common.test.html\",\"content\":\"<div>This is just a test html</div>\"}}";
    String getPage = "{\"readOnly\":true,\"category\":\"page\",\"name\":\"getPage\",\"data\":{\"id\":\"com.networknt.light.common.test.html\"}}";
    String updPage = "{\"readOnly\":false,\"category\":\"page\",\"name\":\"updPage\",\"data\":{\"id\":\"com.networknt.light.common.test.html\",\"content\":\"<div>Updated content</div>\"}}";
    String getAllPage = "{\"readOnly\": true, \"category\": \"page\", \"name\": \"getAllPage\"}";
    String delPage = "{\"readOnly\":false,\"category\":\"page\",\"name\":\"delPage\",\"data\":{\"id\":\"com.networknt.light.common.test.html\"}}";

    public PageRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(PageRuleTest.class);
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
            // signIn test by userId
            {
                jsonMap = mapper.readValue(signInTest,
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
            }

            // clear the test page
            {
                jsonMap = mapper.readValue(getPage,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                GetPageRule rule = new GetPageRule();
                ruleResult = rule.execute(jsonMap);
                if(ruleResult) {
                    String json = (String)jsonMap.get("result");
                    Map<String, Object> page = mapper.readValue(json,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap = mapper.readValue(delPage,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("payload", ownerToken.getPayload());
                    jsonMap.put("data", page);
                    DelPageRule valRule = new DelPageRule();
                    ruleResult = valRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    DelPageEvRule delRule = new DelPageEvRule();
                    ruleResult = delRule.execute(eventMap);
                    assertTrue(ruleResult);
                }
            }

            // add page
            {
                jsonMap = mapper.readValue(addPage,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                AddPageRule valRule = new AddPageRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                AddPageEvRule rule = new AddPageEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);
            }
            // get page
            {
                jsonMap = mapper.readValue(getPage,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                GetPageRule rule = new GetPageRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                // make sure we have content and id
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                assertTrue(jsonMap.containsKey("content"));
                assertTrue(jsonMap.containsKey("id"));
            }
            // get all pages
            {
                jsonMap = mapper.readValue(getAllPage,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                GetAllPageRule rule = new GetAllPageRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                assertNotNull(result);

            }
            // upd page
            {
                jsonMap = mapper.readValue(getPage,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                GetPageRule rule = new GetPageRule();
                ruleResult = rule.execute(jsonMap);
                if(ruleResult) {
                    String result = (String) jsonMap.get("result");
                    Map<String, Object> page = mapper.readValue(result,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    page.put("content", "<div>Updated content</div>");

                    jsonMap = mapper.readValue(updPage,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("payload", ownerToken.getPayload());
                    jsonMap.put("data", page);
                    UpdPageRule valRule = new UpdPageRule();
                    ruleResult = valRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    UpdPageEvRule updRule = new UpdPageEvRule();
                    ruleResult = updRule.execute(eventMap);
                    assertTrue(ruleResult);
                }
            }

            // get page again to check update
            {
                jsonMap = mapper.readValue(getPage,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                GetPageRule rule = new GetPageRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                // make sure we have content and id
                assertTrue(jsonMap.containsKey("content"));
                assertTrue(jsonMap.containsKey("id"));

                String content = (String)jsonMap.get("content");
                assertEquals("<div>Updated content</div>", content);

            }

            // get all pages
            {
                jsonMap = mapper.readValue(getAllPage,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                GetAllPageRule rule = new GetAllPageRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                assertNotNull(result);
            }

            // del page
            {
                jsonMap = mapper.readValue(getPage,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                GetPageRule rule = new GetPageRule();
                ruleResult = rule.execute(jsonMap);
                if(ruleResult) {
                    String result = (String) jsonMap.get("result");
                    Map<String, Object> page = mapper.readValue(result,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap = mapper.readValue(delPage,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("payload", ownerToken.getPayload());
                    jsonMap.put("data", page);

                    DelPageRule valRule = new DelPageRule();
                    ruleResult = valRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    DelPageEvRule delRule = new DelPageEvRule();
                    ruleResult = delRule.execute(eventMap);
                    assertTrue(ruleResult);
                }
            }
            // get page again to check if it exists
            {
                jsonMap = mapper.readValue(getPage,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                GetPageRule rule = new GetPageRule();
                ruleResult = rule.execute(jsonMap);
                assertFalse(ruleResult);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
