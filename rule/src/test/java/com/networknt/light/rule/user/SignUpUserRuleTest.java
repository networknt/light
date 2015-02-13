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
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by husteve on 8/29/2014.
 */
public class SignUpUserRuleTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();
    String signUp = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signUpUser\",\"data\":{\"userId\":\"testuser\",\"email\":\"testuser@gmail.com\",\"password\":\"abcdefg\",\"passwordConfirm\":\"abcdefg\",\"firstName\":\"test\",\"lastName\":\"user\"}}";

    String signUpJsonPasswordDiff = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signUpUser\",\"data\":{\"userId\":\"tstuser\",\"email\":\"tstuser@gmail.com\",\"password\":\"abcdefg\",\"passwordConfirm\":\"12345\",\"firstName\":\"tst\",\"lastName\":\"user\"}}";

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
        try {
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
                String error = (String)jsonMap.get("error");
                assertEquals("password and password confirm are not the same", error);
                int responseCode = (int)jsonMap.get("responseCode");
                assertEquals(400, responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
