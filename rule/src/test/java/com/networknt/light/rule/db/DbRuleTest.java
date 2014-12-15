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

    String signInOwner = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"userIdEmail\":\"stevehu\",\"password\":\"123456\",\"rememberMe\":true}}";
    String signInTest = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"userIdEmail\":\"test\",\"password\":\"123456\",\"rememberMe\":true}}";

    String addSchema = "{\"readOnly\":false,\"category\":\"db\",\"name\":\"cmdDb\",\"data\":{\"script\":\"DROP CLASS Test;\\\\nCREATE CLASS Test;\\\\nCREATE PROPERTY Test.id STRING;\\\\nCREATE PROPERTY Test.name STRING;\\\\nCREATE INDEX Test.id UNIQUE;\"}}";

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
            // signIn test by userId
            {
                jsonMap = mapper.readValue(signInTest,
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

            // execute commands anonymous
            {
                jsonMap = mapper.readValue(addSchema,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                ExecCommandRule rule = new ExecCommandRule();
                ruleResult = rule.execute(jsonMap);
                assertFalse(ruleResult);
            }

            // execute commands by user
            {
                jsonMap = mapper.readValue(addSchema,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", userToken.getPayload());
                ExecCommandRule rule = new ExecCommandRule();
                ruleResult = rule.execute(jsonMap);
                assertFalse(ruleResult);
            }

            // execute commands by owner to create Test and id with index on it.
            {
                jsonMap = mapper.readValue(addSchema,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());
                ExecCommandRule rule = new ExecCommandRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                Map<String, Object> eventMap = (Map<String, Object>) jsonMap.get("eventMap");
                ExecCommandEvRule evRule = new ExecCommandEvRule();
                ruleResult = evRule.execute(eventMap);
                assertTrue(ruleResult);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}