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

package com.networknt.light.rule.injector.main.feed;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.util.JwtUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.oauth.jsontoken.JsonToken;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by husteve on 9/19/2014.
 */
public class InjClassFeedRuleTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();

    String injClassFeed = "{\"host\":\"injector\",\"name\":\"injClassFeed\",\"readOnly\":false,\"category\":\"feed\",\"app\":\"main\",\"data\":{\"loanCategroy\":\"CHMC\",\"debtConsolidation\":\"N\",\"incomeVerification\":\"N\",\"propertyProvCode\":\"ON\",\"centreCode\":\"TO\",\"customerName\":\"New Injector\",\"postNumber\":\"123\",\"transit\":\"00001\",\"cid\":\"100\",\"fundsDisbursed\":1000,\"relationship\":\"1\",\"marketingSourceCode\":\"RP99\",\"provinceCode\":\"ON\",\"newConstructionInd\":\"N\",\"environment\":\"LOCAL\",\"requestId\":1,\"messageNumber\":\"E0450\",\"hppInd\":\"0\",\"loanNumber\":\"1111111111\"}}";
    String getFeedByUser = "{\"host\":\"injector\",\"name\":\"getFeed\",\"readOnly\":true,\"category\":\"feed\",\"app\":\"main\",\"data\":{\"pageNo\":1,\"pageSize\":10,\"createUserId\":\"stevehu\",\"sortedBy\":\"createDate\",\"sortDir\":\"desc\"}}";
    String getFeedOrderByCreateDate = "{\"host\":\"injector\",\"name\":\"getFeed\",\"readOnly\":true,\"category\":\"feed\",\"app\":\"main\",\"data\":{\"pageNo\":1,\"pageSize\":10,\"sortedBy\":\"createDate\",\"sortDir\":\"desc\"}}";
    String getFeed = "{\"host\":\"injector\",\"name\":\"getFeed\",\"readOnly\":true,\"category\":\"feed\",\"app\":\"main\",\"data\":{}}";
    String getFeedByNullFilter = "{\"host\":\"injector\",\"name\":\"getFeed\",\"readOnly\":true,\"category\":\"feed\",\"app\":\"main\",\"data\":{\"pageNo\":1,\"pageSize\":10,\"createUserId\":null,\"sortedBy\":\"createDate\",\"sortDir\":\"desc\"}}";
    String getFeedByMultipleFilter = "{\"host\":\"injector\",\"name\":\"getFeed\",\"readOnly\":true,\"category\":\"feed\",\"app\":\"main\",\"data\":{\"pageNo\":1,\"pageSize\":10,\"createUserId\":\"stevehu\",\"dataFeedType\":\"CLASS\",\"sortedBy\":\"createDate\",\"sortDir\":\"desc\"}}";


    public InjClassFeedRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(InjClassFeedRuleTest.class);
        return suite;
    }

    public void setUp() throws Exception { super.setUp(); }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    /*
    Commented out. Don't waste counter:)
    public void testRequestId() throws Exception {
        InjClassFeedEvRule rule = new InjClassFeedEvRule();
        String id1 = rule.getRequestId();
        String id2 = rule.getRequestId();
        assertNotSame(id1, id2);
        assertTrue(id1.startsWith("INJN"));
        assertTrue(id2.startsWith("INJN"));
        long l1 = Long.valueOf(id1.substring(4));
        long l2 = Long.valueOf(id2.substring(4));
        assertTrue(l2 > l1);
        assertTrue(l2 == l1 + 1);
    }
    */

    public void testExecute() throws Exception {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        boolean ruleResult = false;
        try {
            // inject class feed with error, not logged in
            {
                jsonMap = mapper.readValue(injClassFeed,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                InjClassFeedRule rule = new InjClassFeedRule();
                ruleResult = rule.execute(jsonMap);
                assertFalse(ruleResult);
                assertEquals(401, jsonMap.get("responseCode"));
            }
            // inject class feed no error
            {
                jsonMap = mapper.readValue(injClassFeed,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                // get a valid token and put it into jsonMap.
                Map<String, Object> user = new LinkedHashMap<String, Object>();
                user.put("userId", "steve");
                JsonToken token = JwtUtil.createToken(user);
                jsonMap.put("payload", token.getPayload());
                InjClassFeedRule valRule = new InjClassFeedRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                InjClassFeedEvRule rule = new InjClassFeedEvRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
            }
            // get all feed
            {
                jsonMap = mapper.readValue(getFeed,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                GetFeedRule rule = new GetFeedRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String)jsonMap.get("result");
                assertNotNull(result);
                System.out.println("result = " + result);
            }

            // get feed by userId
            {
                jsonMap = mapper.readValue(getFeedByUser,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                GetFeedRule rule = new GetFeedRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                assertNotNull(jsonMap.get("result"));
            }

            // get feed order by createDate
            {
                jsonMap = mapper.readValue(getFeedOrderByCreateDate,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                GetFeedRule rule = new GetFeedRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                assertNotNull(jsonMap.get("result"));
            }

            // get feed by null filter
            {
                jsonMap = mapper.readValue(getFeedByNullFilter,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                GetFeedRule rule = new GetFeedRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                assertNotNull(jsonMap.get("result"));
            }

            // get feed by multiple filters
            {
                jsonMap = mapper.readValue(getFeedByMultipleFilter,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                GetFeedRule rule = new GetFeedRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                assertNotNull(jsonMap.get("result"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
