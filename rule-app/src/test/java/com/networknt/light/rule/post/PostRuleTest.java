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

package com.networknt.light.rule.post;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.user.SignInUserEvRule;
import com.networknt.light.rule.user.SignInUserRule;
import com.networknt.light.util.JwtUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.oauth.jsontoken.JsonToken;

import java.util.*;

/**
 * Created by steve on 28/11/14.
 */
public class PostRuleTest extends TestCase {
    ObjectMapper mapper = new ObjectMapper();
    String signInOwner = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"stevehu\",\"password\":\"123456\",\"rememberMe\":true, \"clientId\":\"example@Browser\"}}";
    String signInUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"test\",\"password\":\"123456\",\"rememberMe\":true, \"clientId\":\"example@Browser\"}}";

    String getExamBlog = "{\"readOnly\":true,\"category\":\"blog\",\"name\":\"getBlog\",\"data\":{\"host\":\"www.example.com\"}}";
    String delExamBlog = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"delBlog\",\"data\":{\"host\":\"www.example.com\"}}";
    String addExamBlog1 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addBlog\",\"data\":{\"host\":\"www.example.com\",\"blogId\":\"blog1\",\"description\":\"blog1\"}}";


    String addPost1 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"Test blog1\",\"source\":\"http://www.networknt.com/blog/1\",\"content\":\"This is just a test1\",\"parentId\":\"blog1\"}}";
    String addPost2 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"Test blog2\",\"source\":\"http://www.networknt.com/blog/1\",\"content\":\"This is just a test2\",\"tags\":\"test, blog, java\",\"parentId\":\"blog1\"}}";
    String addPost3 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"Test blog3\",\"source\":\"http://www.networknt.com/blog/1\",\"content\":\"This is just a test3\",\"tags\":\"test, blog, java\",\"parentId\":\"blog1\"}}";
    String addPost4 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"Test blog4\",\"source\":\"http://www.networknt.com/blog/1\",\"content\":\"This is just a test4\",\"tags\":\"test, blog, java\",\"parentId\":\"blog1\"}}";
    String addPost5 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"Test blog5\",\"source\":\"http://www.networknt.com/blog/1\",\"content\":\"This is just a test5\",\"tags\":\"test, blog, java\",\"parentId\":\"blog1\"}}";
    String addPost6 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"Test blog6\",\"source\":\"http://www.networknt.com/blog/1\",\"content\":\"This is just a test6\",\"tags\":\"test, blog, java\",\"parentId\":\"blog1\"}}";


    String getPost = "{\"readOnly\":true,\"category\":\"post\",\"name\":\"getPost\",\"data\":{\"host\":\"www.example.com\"}}";
    String getPost1 = "{\"readOnly\":true,\"category\":\"post\",\"name\":\"getPost\",\"data\":{\"host\":\"www.example.com\",\"pageSize\":2,\"pageNo\":1}}";
    String getPost2 = "{\"readOnly\":true,\"category\":\"post\",\"name\":\"getPost\",\"data\":{\"host\":\"www.example.com\",\"pageSize\":2,\"pageNo\":2}}";
    String getPost3 = "{\"readOnly\":true,\"category\":\"post\",\"name\":\"getPost\",\"data\":{\"host\":\"www.example.com\",\"pageSize\":2,\"pageNo\":3}}";

    String delPost = "{\"readOnly\":false,\"category\":\"post\",\"name\":\"delPost\",\"data\":{\"host\":\"www.example.com\"}}";

    String updPost = "{\"readOnly\":false,\"category\":\"post\",\"name\":\"updPost\",\"data\":{\"host\":\"www.example.com\"}}";

    String upPost = "{\"readOnly\":false,\"category\":\"post\",\"name\":\"upPost\",\"data\":{\"host\":\"www.example.com\"}}";
    String downPost = "{\"readOnly\":false,\"category\":\"post\",\"name\":\"downPost\",\"data\":{\"host\":\"www.example.com\"}}";

    public PostRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(PostRuleTest.class);
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
            Map<String, Object> blogAdminPayload = null;
            Map<String, Object> forumAdminPayload = null;
            Map<String, Object> newsAdminPayload = null;

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

                blogAdminPayload = new HashMap<String, Object>();
                Map<String, Object> blogAdminUser = new LinkedHashMap<String, Object>((Map)userToken.getPayload().get("user"));
                List roles = new ArrayList();;
                roles.add("blogAdmin");
                blogAdminUser.put("roles", roles);
                blogAdminUser.put("host", "www.example.com");
                blogAdminPayload.put("user", blogAdminUser);

                forumAdminPayload = new HashMap<String, Object>();
                Map<String, Object> forumAdminUser = new LinkedHashMap<String, Object>((Map)userToken.getPayload().get("user"));
                roles = new ArrayList();;
                roles.add("forumAdmin");
                forumAdminUser.put("roles", roles);
                forumAdminUser.put("host", "www.example.com");
                forumAdminPayload.put("user", forumAdminUser);

                newsAdminPayload = new HashMap<String, Object>();
                Map<String, Object> newsAdminUser = new LinkedHashMap<String, Object>((Map)userToken.getPayload().get("user"));
                roles = new ArrayList();;
                roles.add("newsAdmin");
                newsAdminUser.put("roles", roles);
                newsAdminUser.put("host", "www.example.com");
                newsAdminPayload.put("user", newsAdminUser);

            }

            // get all posts without for host www.example.com and delete them all for clean up.
            {
                jsonMap = mapper.readValue(getPost,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", ownerToken.getPayload());

                GetPostRule rule = new GetPostRule();
                ruleResult = rule.execute(jsonMap);
                if(ruleResult) {
                    // remove them all.
                    String result = (String) jsonMap.get("result");
                    System.out.println("result = " + result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
