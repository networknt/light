package com.networknt.light.rule.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.host.*;
import com.networknt.light.rule.user.SignInUserEvRule;
import com.networknt.light.rule.user.SignInUserRule;
import com.networknt.light.util.JwtUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve on 31/01/15.
 */
public class ClientRuleTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();

    String getClientDropdown = "{\"readOnly\":true,\"category\":\"client\",\"name\":\"getClientDropdown\"}";

    public ClientRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(ClientRuleTest.class);
        return suite;
    }

    public void setUp() throws Exception { super.setUp(); }

    public void tearDown() throws Exception { super.tearDown(); }

    public void testExecute() throws Exception {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        boolean ruleResult = false;
        try {
            // get client drop down list
            {
                jsonMap = mapper.readValue(getClientDropdown,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                GetClientDropdownRule rule = new GetClientDropdownRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String) jsonMap.get("result");
                System.out.println("result = " + result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
