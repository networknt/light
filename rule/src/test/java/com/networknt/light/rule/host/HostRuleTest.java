package com.networknt.light.rule.host;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.role.*;
import com.networknt.light.rule.user.SignInUserEvRule;
import com.networknt.light.rule.user.SignInUserRule;
import com.networknt.light.util.JwtUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.oauth.jsontoken.JsonToken;

import java.util.*;

/**
 * Created by Steve Hu on 2015-01-19.
 */
public class HostRuleTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();
    String signInOwner = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"stevehu\",\"password\":\"123456\",\"rememberMe\":true}}";
    String signInUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"test\",\"password\":\"123456\",\"rememberMe\":true}}";

    String getAllHost = "{\"readOnly\":true,\"category\":\"host\",\"name\":\"getAllHost\"}";
    String getHostDropdown = "{\"readOnly\":true,\"category\":\"host\",\"name\":\"getHostDropdown\"}";

    String addHost = "{\"readOnly\":false,\"category\":\"host\",\"name\":\"addHost\",\"data\":{\"id\":\"www.test.com\",\"host\":\"example\",\"base\":\"/home/test/web\"}}";
    String updHost = "{\"readOnly\": false, \"category\": \"host\", \"name\": \"updHost\"}";
    String delHost = "{\"readOnly\": false, \"category\": \"host\", \"name\": \"delHost\"}";

    public HostRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(HostRuleTest.class);
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

            // get all hosts by owner
            {
                jsonMap = mapper.readValue(getAllHost,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());

                GetAllHostRule rule = new GetAllHostRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                System.out.println("result = " + result);
            }

            // del the new host added in the test case in case test case failed.
            /*
            {
                DelHostRule delHostRule = new DelHostRule();

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
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
