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

package com.networknt.light.rule.blog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.forum.*;
import com.networknt.light.rule.user.SignInUserEvRule;
import com.networknt.light.rule.user.SignInUserRule;
import com.networknt.light.util.JwtUtil;
import com.networknt.light.util.ServiceLocator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.oauth.jsontoken.JsonToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/8/2014.
 */
public class BlogRuleTest extends TestCase {
    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
    String signInOwner = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"stevehu\",\"password\":\"123456\",\"rememberMe\":true, \"clientId\":\"example@Browser\"}}";
    String signInUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"test\",\"password\":\"123456\",\"rememberMe\":true, \"clientId\":\"example@Browser\"}}";

    // for admin
    String getExamBlog = "{\"readOnly\":true,\"category\":\"blog\",\"name\":\"getBlog\",\"data\":{\"host\":\"www.example.com\"}}";
    String getDemoBlog = "{\"readOnly\":true,\"category\":\"blog\",\"name\":\"getBlog\",\"data\":{\"host\":\"demo.networknt.com\"}}";

    // for blog home
    String getExamTree = "{\"readOnly\":true,\"category\":\"blog\",\"name\":\"getBlogTree\",\"data\":{\"host\":\"www.example.com\"}}";
    String getDemoTree = "{\"readOnly\":true,\"category\":\"blog\",\"name\":\"getBlogTree\",\"data\":{\"host\":\"demo.networknt.com\"}}";

    String delExamBlog = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"delBlog\",\"data\":{\"host\":\"www.example.com\"}}";
    String delDemoBlog = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"delBlog\",\"data\":{\"host\":\"demo.networknt.com\"}}";


    String addExamBlog1 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addBlog\",\"data\":{\"host\":\"www.example.com\",\"blogId\":\"blog1\",\"description\":\"blog1\"}}";
    String addExamBlog11 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addBlog\",\"data\":{\"host\":\"www.example.com\",\"blogId\":\"blog11\",\"description\":\"blog11\"}}";
    String addExamBlog12 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addBlog\",\"data\":{\"host\":\"www.example.com\",\"blogId\":\"blog12\",\"description\":\"blog12\"}}";
    String addExamBlog111 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addBlog\",\"data\":{\"host\":\"www.example.com\",\"blogId\":\"blog111\",\"description\":\"blog111\"}}";
    String addExamBlog121 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addBlog\",\"data\":{\"host\":\"www.example.com\",\"blogId\":\"blog121\",\"description\":\"blog121\"}}";
    String addExamBlog112 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addBlog\",\"data\":{\"host\":\"www.example.com\",\"blogId\":\"blog112\",\"description\":\"blog112\"}}";
    String addExamBlog2 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addBlog\",\"data\":{\"host\":\"www.example.com\",\"blogId\":\"blog2\",\"description\":\"blog2\"}}";

    String addDemoBlog1 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addBlog\",\"data\":{\"host\":\"demo.networknt.com\",\"blogId\":\"blog1\",\"description\":\"blog1\"}}";
    String addDemoBlog2 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addBlog\",\"data\":{\"host\":\"demo.networknt.com\",\"blogId\":\"blog2\",\"description\":\"blog2\"}}";


    String updExamBlog = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"updBlog\",\"data\":{\"host\":\"www.example.com\"}}";
    String updDemoBlog = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"updBlog\",\"data\":{\"host\":\"demo.networknt.com\"}}";


    String addExamPost1 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"post1\",\"content\":\"content1\",\"parentId\":\"blog1\"}}";
    String addExamPost11 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"post11\",\"content\":\"content11\",\"parentId\":\"blog11\"}}";
    String addExamPost12 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"post12\",\"content\":\"content12\",\"parentId\":\"blog12\"}}";
    String addExamPost111 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"post111\",\"content\":\"content111\",\"parentId\":\"blog111\"}}";
    String addExamPost121 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"post121\",\"content\":\"content121\",\"parentId\":\"blog121\"}}";
    String addExamPost112 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"post112\",\"content\":\"content112\",\"parentId\":\"blog112\"}}";
    String addExamPost2 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addPost\",\"data\":{\"host\":\"www.example.com\",\"title\":\"post2\",\"content\":\"content2\",\"parentId\":\"blog2\"}}";

    String addDemoPost1 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addPost\",\"data\":{\"host\":\"demo.networknt.com\",\"title\":\"post1\",\"content\":\"content1\",\"parentId\":\"blog1\"}}";
    String addDemoPost2 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addPost\",\"data\":{\"host\":\"demo.networknt.com\",\"title\":\"post2\",\"content\":\"content2\",\"parentId\":\"blog2\"}}";

    String getExamBlogPost1 = "{\"readOnly\":true,\"category\":\"blog\",\"name\":\"getBlogPost\",\"data\":{\"host\":\"www.example.com\",\"pageSize\":3,\"pageNo\":1}}";
    String getExamBlogPost2 = "{\"readOnly\":true,\"category\":\"blog\",\"name\":\"getBlogPost\",\"data\":{\"host\":\"www.example.com\",\"pageSize\":3,\"pageNo\":2}}";
    String getExamBlogPost3 = "{\"readOnly\":true,\"category\":\"blog\",\"name\":\"getBlogPost\",\"data\":{\"host\":\"www.example.com\",\"pageSize\":3,\"pageNo\":3}}";

    String getDemoBlogPost1 = "{\"readOnly\":true,\"category\":\"blog\",\"name\":\"getBlogPost\",\"data\":{\"host\":\"demo.networknt.com\",\"pageSize\":3,\"pageNo\":1}}";

    public BlogRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(BlogRuleTest.class);
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

        Map<String, Object> examBlog1 = null;
        Map<String, Object> examBlog11 = null;
        Map<String, Object> examBlog12 = null;
        Map<String, Object> examBlog111 = null;
        Map<String, Object> examBlog121 = null;
        Map<String, Object> examBlog112 = null;
        Map<String, Object> examBlog2 = null;
        Map<String, Object> demoBlog1 = null;
        Map<String, Object> demoBlog2 = null;



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

        // get all blogs from example and delete them all for cleaning up
        {
            jsonMap = mapper.readValue(getExamBlog,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());

            GetBlogRule rule = new GetBlogRule();
            ruleResult = rule.execute(jsonMap);
            if(ruleResult) {
                String result = (String)jsonMap.get("result");
                List<Map<String, Object>> blogs = mapper.readValue(result,
                        new TypeReference<ArrayList<HashMap<String, Object>>>() {
                        });
                for(Map<String, Object> blog: blogs) {
                    String rid = (String)blog.get("@rid");
                    jsonMap = mapper.readValue(delExamBlog,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("payload", ownerToken.getPayload());
                    Map<String, Object> data = (Map<String,Object>)jsonMap.get("data");
                    data.put("@rid", rid);
                    DelBlogRule delRule = new DelBlogRule();
                    ruleResult = delRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    DelBlogEvRule delEvRule = new DelBlogEvRule();
                    ruleResult = delEvRule.execute(eventMap);
                    assertTrue(ruleResult);
                }
            }
        }

        // get all blogs from demo and delete them all for cleaning up
        {
            jsonMap = mapper.readValue(getDemoBlog,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());

            GetBlogRule rule = new GetBlogRule();
            ruleResult = rule.execute(jsonMap);
            if(ruleResult) {
                String result = (String)jsonMap.get("result");
                List<Map<String, Object>> blogs = mapper.readValue(result,
                        new TypeReference<ArrayList<HashMap<String, Object>>>() {
                        });
                for(Map<String, Object> blog: blogs) {
                    String rid = (String)blog.get("@rid");
                    jsonMap = mapper.readValue(delDemoBlog,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("payload", ownerToken.getPayload());
                    Map<String, Object> data = (Map<String,Object>)jsonMap.get("data");
                    data.put("@rid", rid);
                    DelBlogRule delRule = new DelBlogRule();
                    ruleResult = delRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    DelBlogEvRule delEvRule = new DelBlogEvRule();
                    ruleResult = delEvRule.execute(eventMap);
                    assertTrue(ruleResult);
                }
            }
        }

        // add example blogs and demo blogs
        {
            addBlog(addExamBlog1, ownerToken);
            addBlog(addExamBlog11, ownerToken);
            addBlog(addExamBlog12, ownerToken);
            addBlog(addExamBlog111, ownerToken);
            addBlog(addExamBlog121, ownerToken);
            addBlog(addExamBlog112, ownerToken);
            addBlog(addExamBlog2, ownerToken);

            addBlog(addDemoBlog1, ownerToken);
            addBlog(addDemoBlog2, ownerToken);
        }

        // get all blogs from example to make sure 7 in total. save rids
        {
            jsonMap = mapper.readValue(getExamBlog,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());

            GetBlogRule rule = new GetBlogRule();
            ruleResult = rule.execute(jsonMap);
            if(ruleResult) {
                String result = (String) jsonMap.get("result");
                List<Map<String, Object>> blogs = mapper.readValue(result,
                        new TypeReference<ArrayList<HashMap<String, Object>>>() {
                        });
                assertEquals(7, blogs.size());

                examBlog1 = blogs.get(0);
                examBlog11 = blogs.get(1);
                examBlog12 = blogs.get(2);
                examBlog111 = blogs.get(3);
                examBlog121 = blogs.get(4);
                examBlog112 = blogs.get(5);
                examBlog2 = blogs.get(6);
            }
        }

        // update examBlog1 to add two children
        {
            jsonMap = mapper.readValue(updExamBlog,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());
            Map<String, Object> data = (Map<String,Object>)jsonMap.get("data");
            data.putAll(examBlog1);
            List children = new ArrayList<String>();
            children.add(examBlog11.get("@rid"));
            children.add(examBlog12.get("@rid"));
            data.put("out_Own", children);

            UpdBlogRule rule = new UpdBlogRule();
            ruleResult = rule.execute(jsonMap);
            assertTrue(ruleResult);
            Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
            UpdBlogEvRule evRule = new UpdBlogEvRule();
            ruleResult = evRule.execute(eventMap);
            assertTrue(ruleResult);

        }

        // get all blogs from example to make sure 7 in total. save rids again.
        {
            jsonMap = mapper.readValue(getExamBlog,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());

            GetBlogRule rule = new GetBlogRule();
            ruleResult = rule.execute(jsonMap);
            if(ruleResult) {
                String result = (String) jsonMap.get("result");
                List<Map<String, Object>> blogs = mapper.readValue(result,
                        new TypeReference<ArrayList<HashMap<String, Object>>>() {
                        });
                assertEquals(7, blogs.size());

                examBlog1 = blogs.get(0);
                examBlog11 = blogs.get(1);
                examBlog12 = blogs.get(2);
                examBlog111 = blogs.get(3);
                examBlog121 = blogs.get(4);
                examBlog112 = blogs.get(5);
                examBlog2 = blogs.get(6);
            }
        }

        // update examBlog11 to add two children
        {
            jsonMap = mapper.readValue(updExamBlog,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());
            Map<String, Object> data = (Map<String,Object>)jsonMap.get("data");
            data.putAll(examBlog11);
            List children = new ArrayList<String>();
            children.add(examBlog111.get("@rid"));
            children.add(examBlog112.get("@rid"));
            data.put("out_Own", children);

            UpdBlogRule rule = new UpdBlogRule();
            ruleResult = rule.execute(jsonMap);
            assertTrue(ruleResult);
            Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
            UpdBlogEvRule evRule = new UpdBlogEvRule();
            ruleResult = evRule.execute(eventMap);
            assertTrue(ruleResult);

        }

        // update examBlog12 to add two children
        {
            jsonMap = mapper.readValue(updExamBlog,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());
            Map<String, Object> data = (Map<String,Object>)jsonMap.get("data");
            data.putAll(examBlog12);
            List children = new ArrayList<String>();
            children.add(examBlog121.get("@rid"));
            data.put("out_Own", children);

            UpdBlogRule rule = new UpdBlogRule();
            ruleResult = rule.execute(jsonMap);
            assertTrue(ruleResult);
            Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
            UpdBlogEvRule evRule = new UpdBlogEvRule();
            ruleResult = evRule.execute(eventMap);
            assertTrue(ruleResult);

        }

        // get all blogs from demo to make sure 2 in total. save rids
        {
            jsonMap = mapper.readValue(getDemoBlog,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());

            GetBlogRule rule = new GetBlogRule();
            ruleResult = rule.execute(jsonMap);
            if(ruleResult) {
                String result = (String) jsonMap.get("result");
                List<Map<String, Object>> blogs = mapper.readValue(result,
                        new TypeReference<ArrayList<HashMap<String, Object>>>() {
                        });
                assertEquals(2, blogs.size());

                demoBlog1 = blogs.get(0);
                demoBlog2 = blogs.get(1);
            }
        }

        // update demo blogs to form a tree structure
        {
            jsonMap = mapper.readValue(updDemoBlog,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());
            Map<String, Object> data = (Map<String,Object>)jsonMap.get("data");
            data.putAll(demoBlog1);
            List children = new ArrayList<String>();
            children.add(demoBlog2.get("@rid"));
            data.put("out_Own", children);

            UpdBlogRule rule = new UpdBlogRule();
            ruleResult = rule.execute(jsonMap);
            assertTrue(ruleResult);
            Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
            UpdBlogEvRule evRule = new UpdBlogEvRule();
            ruleResult = evRule.execute(eventMap);
            assertTrue(ruleResult);
        }

        // get all exam blogs as a tree structure.
        {
            jsonMap = mapper.readValue(getExamTree,
                    new TypeReference<HashMap<String, Object>>() {
                    });

            GetBlogTreeRule rule = new GetBlogTreeRule();
            ruleResult = rule.execute(jsonMap);
            assertTrue(ruleResult);
            String result = (String)jsonMap.get("result");
            System.out.println("example blog tree = " + result);
        }

        // get all demo blogs as a tree structure.
        {
            jsonMap = mapper.readValue(getDemoTree,
                    new TypeReference<HashMap<String, Object>>() {
                    });

            GetBlogTreeRule rule = new GetBlogTreeRule();
            ruleResult = rule.execute(jsonMap);
            assertTrue(ruleResult);
            String result = (String)jsonMap.get("result");
            System.out.println("demo blog tree = " + result);
        }

        // get blog post for DemoBlog1 before adding posts
        {
            String json = getBlogPost(getDemoBlogPost1, (String)demoBlog1.get("@rid"), ownerToken);
            System.out.println("blogPost for DemoBlog1 before adding posts" + json);
        }

        // add example posts
        {
            addPost(addExamPost1, ownerToken);
            addPost(addExamPost11, ownerToken);
            addPost(addExamPost12, ownerToken);
            addPost(addExamPost111, ownerToken);
            addPost(addExamPost121, ownerToken);
            addPost(addExamPost112, ownerToken);
            addPost(addExamPost2, ownerToken);

            addPost(addDemoPost1, ownerToken);
            addPost(addDemoPost2, ownerToken);
        }

        // get blog post for ExamBlog1
        {
            String json = getBlogPost(getExamBlogPost1, (String)examBlog1.get("@rid"), ownerToken);
            System.out.println("blogPost for ExamBlog1" + json);

            json = getBlogPost(getExamBlogPost2, (String)examBlog1.get("@rid"), ownerToken);
            System.out.println("blogPost for ExamBlog2" + json);

            json = getBlogPost(getExamBlogPost3, (String)examBlog1.get("@rid"), ownerToken);
            System.out.println("blogPost for ExamBlog3" + json);
        }

        // get blog post for DemoBlog1
        {
            String json = getBlogPost(getDemoBlogPost1, (String)demoBlog1.get("@rid"), ownerToken);
            System.out.println("blogPost for DemoBlog1 after adding posts" + json);
        }

    }

    private void addBlog(String json, JsonToken token) throws Exception {
        Map<String, Object> jsonMap = null;
        boolean ruleResult = false;
        jsonMap = mapper.readValue(json,
                new TypeReference<HashMap<String, Object>>() {
                });
        jsonMap.put("payload", token.getPayload());

        AddBlogRule rule = new AddBlogRule();
        ruleResult = rule.execute(jsonMap);
        assertTrue(ruleResult);
        Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
        AddBlogEvRule evRule = new AddBlogEvRule();
        ruleResult = evRule.execute(eventMap);
        assertTrue(ruleResult);
    }

    private void addPost(String json, JsonToken token) throws Exception {
        Map<String, Object> jsonMap = null;
        boolean ruleResult = false;
        jsonMap = mapper.readValue(json,
                new TypeReference<HashMap<String, Object>>() {
                });
        jsonMap.put("payload", token.getPayload());

        AddPostRule rule = new AddPostRule();
        ruleResult = rule.execute(jsonMap);
        assertTrue(ruleResult);
        Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
        AddPostEvRule evRule = new AddPostEvRule();
        ruleResult = evRule.execute(eventMap);
        assertTrue(ruleResult);
    }

    private String getBlogPost(String json, String rid, JsonToken token) throws Exception {
        Map<String, Object> jsonMap = null;
        boolean ruleResult = false;
        jsonMap = mapper.readValue(json,
                new TypeReference<HashMap<String, Object>>() {
                });
        jsonMap.put("payload", token.getPayload());
        Map<String, Object> data = (Map<String, Object>)jsonMap.get("data");
        data.put("@rid", rid);
        GetBlogPostRule rule = new GetBlogPostRule();
        ruleResult = rule.execute(jsonMap);
        assertTrue(ruleResult);
        return (String)jsonMap.get("result");
    }

}