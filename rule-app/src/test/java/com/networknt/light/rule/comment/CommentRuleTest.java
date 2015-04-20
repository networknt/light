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
package com.networknt.light.rule.comment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve on 24/02/15.
 *
 * In order to test comment, a forum must be created and a post must be there to be the parent of
 * all comments create in this test suite.
 *
 */
public class CommentRuleTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();
    String signInOwner = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"stevehu\",\"password\":\"123456\",\"rememberMe\":true, \"clientId\":\"example@Browser\"}}";
    String signInUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"test\",\"password\":\"123456\",\"rememberMe\":true, \"clientId\":\"example@Browser\"}}";

    String getCommentTree = "{\"readOnly\":true,\"category\":\"comment\",\"name\":\"getCommentTree\",\"data\":{\"@rid\":\"#29:0\"}}";

    String addPost1 = "{\"readOnly\":false,\"category\":\"post\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"Test blog1\",\"source\":\"http://www.networknt.com/blog/1\",\"content\":\"This is just a test1\"}}";
    String addPost2 = "{\"readOnly\":false,\"category\":\"post\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"Test blog2\",\"source\":\"http://www.networknt.com/blog/1\",\"content\":\"This is just a test2\",\"tags\":\"test, blog, java\"}}";
    String addPost3 = "{\"readOnly\":false,\"category\":\"post\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"Test blog3\",\"source\":\"http://www.networknt.com/blog/1\",\"content\":\"This is just a test3\",\"tags\":\"test, blog, java\"}}";
    String addPost4 = "{\"readOnly\":false,\"category\":\"post\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"Test blog4\",\"source\":\"http://www.networknt.com/blog/1\",\"content\":\"This is just a test4\",\"tags\":\"test, blog, java\"}}";
    String addPost5 = "{\"readOnly\":false,\"category\":\"post\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"Test blog5\",\"source\":\"http://www.networknt.com/blog/1\",\"content\":\"This is just a test5\",\"tags\":\"test, blog, java\"}}";
    String addPost6 = "{\"readOnly\":false,\"category\":\"post\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"Test blog6\",\"source\":\"http://www.networknt.com/blog/1\",\"content\":\"This is just a test6\",\"tags\":\"test, blog, java\"}}";


    String getPost = "{\"readOnly\":true,\"category\":\"post\",\"name\":\"getPost\",\"data\":{\"host\":\"www.example.com\"}}";
    String getPost1 = "{\"readOnly\":true,\"category\":\"post\",\"name\":\"getPost\",\"data\":{\"host\":\"www.example.com\",\"pageSize\":2,\"pageNo\":1}}";
    String getPost2 = "{\"readOnly\":true,\"category\":\"post\",\"name\":\"getPost\",\"data\":{\"host\":\"www.example.com\",\"pageSize\":2,\"pageNo\":2}}";
    String getPost3 = "{\"readOnly\":true,\"category\":\"post\",\"name\":\"getPost\",\"data\":{\"host\":\"www.example.com\",\"pageSize\":2,\"pageNo\":3}}";

    String delPost = "{\"readOnly\":false,\"category\":\"post\",\"name\":\"delPost\",\"data\":{\"host\":\"www.example.com\"}}";

    String updPost = "{\"readOnly\":false,\"category\":\"post\",\"name\":\"updPost\",\"data\":{\"host\":\"www.example.com\"}}";

    String upPost = "{\"readOnly\":false,\"category\":\"post\",\"name\":\"upPost\",\"data\":{\"host\":\"www.example.com\"}}";
    String downPost = "{\"readOnly\":false,\"category\":\"post\",\"name\":\"downPost\",\"data\":{\"host\":\"www.example.com\"}}";

    public CommentRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(CommentRuleTest.class);
        return suite;
    }

    public void setUp() throws Exception { super.setUp(); }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testExecute() throws Exception {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        boolean ruleResult = false;
        /*
        try {
            // get all comments from post rid #29:0
            {
                jsonMap = mapper.readValue(getCommentTree,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                GetCommentTreeRule rule = new GetCommentTreeRule();
                ruleResult = rule.execute(jsonMap);
                if(ruleResult) {
                    String result = (String) jsonMap.get("result");
                    System.out.println("result = " + result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

}
