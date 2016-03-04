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

package com.networknt.light.rule.form;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.rule.AbstractRuleRule;
import com.networknt.light.rule.rule.GetRuleDropdownRule;
import com.networknt.light.rule.user.SignInUserEvRule;
import com.networknt.light.rule.user.SignInUserRule;
import com.networknt.light.util.JwtUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by husteve on 8/28/2014.
 */
public class FormRuleTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();

    String signInOwner = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"stevehu\",\"password\":\"123456\",\"rememberMe\":true,\"clientId\":\"example@Browser\"}}";
    String signInUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"test\",\"password\":\"123456\",\"rememberMe\":true,\"clientId\":\"example@Browser\"}}";

    String addForm = "{\"readOnly\":false,\"category\":\"form\",\"name\":\"addForm\",\"data\":{\"id\":\"com.networknt.light.common.test.json\",\"schema\":{\"type\":\"object\",\"title\":\"Comment\",\"properties\":{\"name\":{\"title\":\"Name\",\"type\":\"string\"},\"email\":{\"title\":\"Email\",\"type\":\"string\",\"pattern\":\"^\\\\S+@\\\\S+$\",\"description\":\"Email will be used for evil.\"},\"comment\":{\"title\":\"Comment\",\"type\":\"string\",\"maxLength\":20,\"validationMessage\":\"Don't be greedy!\"}},\"required\":[\"name\",\"email\",\"comment\"]},\"form\":[\"name\",\"email\",{\"key\":\"comment\",\"type\":\"textarea\"},{\"type\":\"submit\",\"style\":\"btn-info\",\"title\":\"OK\"}]}}";
    String getForm = "{\"readOnly\":true,\"category\":\"form\",\"name\":\"getForm\",\"data\":{\"id\":\"com.networknt.light.common.test.json\"}}";
    String updForm = "{\"readOnly\":false,\"category\":\"form\",\"name\":\"addForm\",\"data\":{\"id\":\"com.networknt.light.common.test.json\",\"schema\":{\"type\":\"object\",\"title\":\"Test\",\"properties\":{\"name\":{\"title\":\"Name\",\"type\":\"string\"},\"email\":{\"title\":\"Email Address\",\"type\":\"string\",\"pattern\":\"^\\\\S+@\\\\S+$\",\"description\":\"Email will be used for evil.\"},\"comment\":{\"title\":\"Comment\",\"type\":\"string\",\"maxLength\":20,\"validationMessage\":\"Don't be greedy!\"}},\"required\":[\"name\",\"email\",\"comment\"]},\"form\":[\"name\",\"email\",{\"key\":\"comment\",\"type\":\"textarea\"},{\"type\":\"submit\",\"style\":\"btn-info\",\"title\":\"OK\"}]}}";
    String getAllForm = "{\"readOnly\": true, \"category\": \"form\", \"name\": \"getAllForm\"}";
    String delForm = "{\"readOnly\":false,\"category\":\"form\",\"name\":\"delForm\",\"data\":{\"id\":\"com.networknt.light.common.test.json\"}}";

    String getDynaForm = "{\"readOnly\":true,\"category\":\"form\",\"name\":\"getForm\",\"data\":{\"host\":\"example\",\"id\":\"com.networknt.light.demo.uiselect_d\"}}";

    String enrichDynamicForm = "{\"id\":\"com.networknt.light.access.add_d\",\"version\":1,\"action\":[{\"category\":\"access\",\"name\":\"addAccess\",\"readOnly\":false,\"title\":\"Submit\",\"success\":\"/page/com-networknt-light-v-access-admin-home\"}],\"schema\":{\"type\":\"object\",\"title\":\"Add Access Control\",\"required\":[\"ruleClass\",\"accessLevel\"],\"properties\":{\"ruleClass\":{\"title\":\"Rule Class\",\"type\":\"string\",\"format\":\"uiselect\",\"items\":[{\"label\":\"dynamic\",\"value\":{\"category\":\"rule\",\"name\":\"getRuleDropdown\"}}]},\"accessLevel\":{\"title\":\"Access Level\",\"type\":\"string\",\"format\":\"uiselect\",\"items\":[{\"value\":\"C\",\"label\":\"Client Based\"},{\"value\":\"R\",\"label\":\"Role Based\"},{\"value\":\"U\",\"label\":\"User Based\"},{\"value\":\"CR\",\"label\":\"Client and Role Based\"},{\"value\":\"CU\",\"label\":\"Client and User Based\"},{\"value\":\"RU\",\"label\":\"Role and User Based\"},{\"value\":\"CRU\",\"label\":\"Client, Role and User Based\"}]},\"clients\":{\"title\":\"Clients\",\"type\":\"array\",\"format\":\"uiselect\",\"items\":[{\"label\":\"dynamic\",\"value\":{\"category\":\"client\",\"name\":\"getClientDropdown\"}}]},\"roles\":{\"title\":\"Roles\",\"type\":\"array\",\"format\":\"uiselect\",\"items\":[{\"value\":{\"category\":\"role\",\"name\":\"getRoleDropdown\"},\"label\":\"dynamic\"}]},\"users\":{\"title\":\"Users [Separate by comma if multiple]\",\"type\":\"string\"}}},\"form\":[\"ruleClass\",\"accessLevel\",\"clients\",\"roles\",{\"key\":\"users\",\"type\":\"textarea\"}]}";

    public FormRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(FormRuleTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }
    public void testVoid() throws Exception {
        return;
    }
    /*

    public void testEnrichForm() throws Exception {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        boolean ruleResult = false;

        JsonToken ownerToken = null;
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

        {
            // construct a fake inputMap.
            jsonMap = mapper.readValue(getDynaForm,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());

            GetFormRule rule = new GetFormRule();
            String json = rule.enrichForm(enrichDynamicForm, jsonMap);
            System.out.println("json = " + json);
            // check schema dropdown values here.

        }

    }

    public void testDynamicForm() throws Exception {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        boolean ruleResult = false;

        JsonToken ownerToken = null;
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

        {
            jsonMap = mapper.readValue(getDynaForm,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());
            GetFormRule rule = new GetFormRule();
            ruleResult = rule.execute(jsonMap);
            assertTrue(ruleResult);
            String result = (String) jsonMap.get("result");
            // make sure we have schema, form and id
            jsonMap = mapper.readValue(result,
                    new TypeReference<HashMap<String, Object>>() {
                    });

            assertTrue(jsonMap.containsKey("schema"));
            assertTrue(jsonMap.containsKey("form"));
            assertTrue(jsonMap.containsKey("formId"));
        }
    }
    */

    /*
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
            }

            // clear the test form
            {
                jsonMap = mapper.readValue(getForm,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                GetFormRule rule = new GetFormRule();
                ruleResult = rule.execute(jsonMap);
                if(ruleResult) {
                    String json = (String)jsonMap.get("result");
                    Map<String, Object> form = mapper.readValue(json,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap = mapper.readValue(delForm,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("payload", ownerToken.getPayload());
                    jsonMap.put("data", form);
                    DelFormRule valRule = new DelFormRule();
                    ruleResult = valRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    DelFormEvRule delRule = new DelFormEvRule();
                    ruleResult = delRule.execute(eventMap);
                    assertTrue(ruleResult);
                }
            }

            // add form
            {
                jsonMap = mapper.readValue(addForm,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                AddFormRule valRule = new AddFormRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                AddFormEvRule rule = new AddFormEvRule();
                ruleResult = rule.execute(eventMap);
                assertTrue(ruleResult);
            }
            // get form
            {
                jsonMap = mapper.readValue(getForm,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                GetFormRule rule = new GetFormRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                // make sure we have schema, form and id
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                assertTrue(jsonMap.containsKey("schema"));
                assertTrue(jsonMap.containsKey("form"));
                assertTrue(jsonMap.containsKey("formId"));
            }
            // get all form
            {
                jsonMap = mapper.readValue(getAllForm,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                GetAllFormRule rule = new GetAllFormRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                assertNotNull(result);

            }
            // upd form
            {
                jsonMap = mapper.readValue(getForm,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                GetFormRule rule = new GetFormRule();
                ruleResult = rule.execute(jsonMap);
                if(ruleResult) {
                    String result = (String) jsonMap.get("result");
                    Map<String, Object> form = mapper.readValue(result,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    Map<String, Object> schema = (Map<String, Object>) form.get("schema");
                    schema.put("title", "Test");
                    Map<String, Object> properties = (Map<String, Object>)schema.get("properties");
                    Map<String, Object> email = (Map<String, Object>) properties.get("email");
                    email.put("title", "Email Address");

                    jsonMap = mapper.readValue(updForm,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("payload", ownerToken.getPayload());
                    jsonMap.put("data", form);
                    UpdFormRule valRule = new UpdFormRule();
                    ruleResult = valRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    UpdFormEvRule updRule = new UpdFormEvRule();
                    ruleResult = updRule.execute(eventMap);
                    assertTrue(ruleResult);
                }

            }

            // get form again to check update
            {
                jsonMap = mapper.readValue(getForm,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                GetFormRule rule = new GetFormRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                // make sure we have schema, form and id
                assertTrue(jsonMap.containsKey("schema"));
                assertTrue(jsonMap.containsKey("form"));
                assertTrue(jsonMap.containsKey("formId"));

                Map<String, Object> schema = (Map<String, Object>)jsonMap.get("schema");
                String title = (String)schema.get("title");
                assertEquals("Test", title);

                Map<String, Object> properties = (Map<String, Object>) schema.get("properties");
                Map<String, Object> email = (Map<String, Object>) properties.get("email");
                assertEquals("Email Address", email.get("title"));
            }
            
            // get all form
            {
                jsonMap = mapper.readValue(getAllForm,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                GetAllFormRule rule = new GetAllFormRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                assertNotNull(result);
            }

            // del form
            {
                jsonMap = mapper.readValue(getForm,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                GetFormRule rule = new GetFormRule();
                ruleResult = rule.execute(jsonMap);
                if(ruleResult) {
                    String result = (String) jsonMap.get("result");
                    Map<String, Object> form = mapper.readValue(result,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap = mapper.readValue(delForm,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("payload", ownerToken.getPayload());
                    jsonMap.put("data", form);

                    DelFormRule valRule = new DelFormRule();
                    ruleResult = valRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    DelFormEvRule delRule = new DelFormEvRule();
                    ruleResult = delRule.execute(eventMap);
                    assertTrue(ruleResult);
                }
            }
            // get form again to check if it exists
            {
                jsonMap = mapper.readValue(getForm,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                GetFormRule rule = new GetFormRule();
                ruleResult = rule.execute(jsonMap);
                assertFalse(ruleResult);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
}
