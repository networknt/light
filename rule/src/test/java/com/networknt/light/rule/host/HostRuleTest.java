package com.networknt.light.rule.host;

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
 * Created by Steve Hu on 2015-01-19.
 */
public class HostRuleTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();
    String signInOwner = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"stevehu\",\"password\":\"123456\",\"rememberMe\":true}}";
    String signInUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"test\",\"password\":\"123456\",\"rememberMe\":true}}";

    String getAllHost = "{\"readOnly\":true,\"category\":\"host\",\"name\":\"getAllHost\"}";
    String getHostDropdown = "{\"readOnly\":true,\"category\":\"host\",\"name\":\"getHostDropdown\"}";

    String addHost = "{\"readOnly\":false,\"category\":\"host\",\"name\":\"addHost\",\"data\":{\"id\":\"www.test.com\",\"host\":\"example\",\"base\":\"/home/test/web\",\"transferMinSize\":\"100\"}}";
    String updHost = "{\"readOnly\":false,\"category\":\"host\",\"name\":\"updHost\",\"data\":{\"id\":\"www.test.com\",\"base\":\"/home/test1/web\",\"transferMinSize\":\"50\",\"host\":\"example\"}}";
    String delHost = "{\"readOnly\":false,\"category\":\"host\",\"name\":\"delHost\",\"data\":{\"id\":\"www.test.com\",\"host\":\"example\"}}";

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

            // get all host drop down by user
            {
                jsonMap = mapper.readValue(getHostDropdown,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());

                GetHostDropdownRule rule = new GetHostDropdownRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                System.out.println("result = " + result);
            }

            // del the new host added in the test case in case test case failed.
            {
                jsonMap = mapper.readValue(delHost,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());

                DelHostRule delHostRule = new DelHostRule();
                ruleResult = delHostRule.execute(jsonMap);
                if(ruleResult) {
                    Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    DelHostEvRule delHostEvRule = new DelHostEvRule();
                    ruleResult = delHostEvRule.execute(eventMap);
                    assertTrue(ruleResult);
                }
            }

            // add host by owner
            {
                jsonMap = mapper.readValue(addHost,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());

                AddHostRule addHostRule = new AddHostRule();
                ruleResult = addHostRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                AddHostEvRule addHostEvRule = new AddHostEvRule();
                ruleResult = addHostEvRule.execute(eventMap);
                assertTrue(ruleResult);
            }

            // update host by owner
            {
                jsonMap = mapper.readValue(updHost,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());

                UpdHostRule updHostRule = new UpdHostRule();
                ruleResult = updHostRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                UpdHostEvRule updHostEvRule = new UpdHostEvRule();
                ruleResult = updHostEvRule.execute(eventMap);
                assertTrue(ruleResult);
            }

            // delete host by owner
            {
                jsonMap = mapper.readValue(delHost,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                DelHostRule delHostRule = new DelHostRule();
                ruleResult = delHostRule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                DelHostEvRule delHostEvRule = new DelHostEvRule();
                ruleResult = delHostEvRule.execute(eventMap);
                assertTrue(ruleResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
