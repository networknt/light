package com.networknt.light.rule.payment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.user.SignInUserEvRule;
import com.networknt.light.rule.user.SignInUserRule;
import com.networknt.light.util.JwtUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve on 19/12/15.
 */
public class PaymentRuleTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();
    String signInUser = "{\"readOnly\":true,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"test\",\"password\":\"123456\",\"rememberMe\":true,\"clientId\":\"example@Browser\"}}";
    String generateClientToken = "{\"readOnly\":false,\"category\":\"payment\",\"name\":\"getClientToken\",\"data\":{\"host\":\"example\"}}";

    public PaymentRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(PaymentRuleTest.class);
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
    public void testExecute() throws Exception {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        boolean ruleResult = false;
        JsonToken userToken = null;
        try {
            // signIn test by userId
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

            // generate client token
            {
                jsonMap = mapper.readValue(generateClientToken,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                Map<String, Object> payload = userToken.getPayload();
                jsonMap.put("payload", payload);

                GetClientTokenRule rule = new GetClientTokenRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String)jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                String clientToken = (String)jsonMap.get("clientToken");
                assertNotNull(clientToken);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
}
