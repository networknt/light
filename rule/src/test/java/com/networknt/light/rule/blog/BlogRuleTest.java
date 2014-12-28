package com.networknt.light.rule.blog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.rule.user.*;
import com.networknt.light.util.JwtUtil;
import com.networknt.light.util.ServiceLocator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.oauth.jsontoken.JsonToken;

import java.util.*;

/**
 * Created by husteve on 10/8/2014.
 */
public class BlogRuleTest extends TestCase {
    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
    String signInAdmin = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"userIdEmail\":\"stevehu\",\"password\":\"123456\",\"rememberMe\":true}}";
    String signInTest = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"userIdEmail\":\"test\",\"password\":\"123456\",\"rememberMe\":true}}";

    String addBlog1 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addBlog\",\"data\":{\"host\":\"www.example.com\",\"title\":\"Test blog1\",\"source\":\"http://www.networknt.com/blog/1\",\"content\":\"This is just a test1\"}}";
    String addBlog2 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addBlog\",\"data\":{\"host\":\"www.example.com\",\"title\":\"Test blog2\",\"source\":\"http://www.networknt.com/blog/1\",\"content\":\"This is just a test2\",\"tags\":\"test, blog, java\"}}";
    String addBlog3 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addBlog\",\"data\":{\"host\":\"www.example.com\",\"title\":\"Test blog3\",\"source\":\"http://www.networknt.com/blog/1\",\"content\":\"This is just a test3\",\"tags\":\"test, blog, java\"}}";
    String addBlog4 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addBlog\",\"data\":{\"host\":\"www.example.com\",\"title\":\"Test blog4\",\"source\":\"http://www.networknt.com/blog/1\",\"content\":\"This is just a test4\",\"tags\":\"test, blog, java\"}}";
    String addBlog5 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addBlog\",\"data\":{\"host\":\"www.example.com\",\"title\":\"Test blog5\",\"source\":\"http://www.networknt.com/blog/1\",\"content\":\"This is just a test5\",\"tags\":\"test, blog, java\"}}";
    String addBlog6 = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"addBlog\",\"data\":{\"host\":\"www.example.com\",\"title\":\"Test blog6\",\"source\":\"http://www.networknt.com/blog/1\",\"content\":\"This is just a test6\",\"tags\":\"test, blog, java\"}}";


    String getBlog1 = "{\"readOnly\":true,\"category\":\"blog\",\"name\":\"getBlog\",\"data\":{\"host\":\"www.example.com\",\"pageSize\":2,\"pageNo\":1}}";
    String getBlog2 = "{\"readOnly\":true,\"category\":\"blog\",\"name\":\"getBlog\",\"data\":{\"host\":\"www.example.com\",\"pageSize\":2,\"pageNo\":2}}";
    String getBlog3 = "{\"readOnly\":true,\"category\":\"blog\",\"name\":\"getBlog\",\"data\":{\"host\":\"www.example.com\",\"pageSize\":2,\"pageNo\":3}}";

    String delBlog = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"delBlog\",\"data\":{\"host\":\"www.example.com\"}}";

    String updBlog = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"updBlog\",\"data\":{\"host\":\"www.example.com\"}}";

    String upBlog = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"upBlog\",\"data\":{\"host\":\"www.example.com\"}}";
    String downBlog = "{\"readOnly\":false,\"category\":\"blog\",\"name\":\"downBlog\",\"data\":{\"host\":\"www.example.com\"}}";

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
        try {

            JsonToken adminToken = null;
            JsonToken userToken = null;
            // signIn admin by userId
            {
                jsonMap = mapper.readValue(signInAdmin,
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
                adminToken = JwtUtil.Deserialize(jwt);
                SignInUserEvRule rule = new SignInUserEvRule();
                ruleResult = rule.execute(jsonMap);
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
                Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                String result = (String)jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                String jwt = (String)jsonMap.get("accessToken");
                userToken = JwtUtil.Deserialize(jwt);
                SignInUserEvRule rule = new SignInUserEvRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
            }

            // del post if it exists in case previous test failed and the post is not removed.
            {
                DelBlogEvRule rule = new DelBlogEvRule();
                //rule.delBlogByHost("www.example.com");
            }

            // add post
            {
                addBlog(addBlog1, adminToken);
                addBlog(addBlog2, adminToken);
                addBlog(addBlog3, userToken);
                addBlog(addBlog4, userToken);
                addBlog(addBlog5, userToken);
            }
            // get blogs page 1, blog5 and blog4.
            {
                jsonMap = mapper.readValue(getBlog1,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminToken.getPayload());

                GetBlogRule rule = new GetBlogRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String)jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                assertEquals(5, (int)jsonMap.get("total"));
                List blogs = (List)jsonMap.get("blogs");
                assertEquals(2, blogs.size());
                Map<String, Object> blog = (Map<String, Object>)blogs.get(0);
                assertEquals("Test blog5", blog.get("title"));
            }

            // get blogs page 2, blog3 and blog2
            {
                jsonMap = mapper.readValue(getBlog2,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminToken.getPayload());

                GetBlogRule rule = new GetBlogRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);

                String result = (String)jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                List blogs = (List)jsonMap.get("blogs");
                assertEquals(2, blogs.size());
                Map<String, Object> blog = (Map<String, Object>)blogs.get(0);
                assertEquals("Test blog3", blog.get("title"));

            }

            // get blogs page 3, blog1 only
            {
                jsonMap = mapper.readValue(getBlog3,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminToken.getPayload());

                GetBlogRule rule = new GetBlogRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String)jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                List blogs = (List)jsonMap.get("blogs");
                assertEquals(1, blogs.size());
                Map<String, Object> blog = (Map<String, Object>)blogs.get(0);
                assertEquals("Test blog1", blog.get("title"));

            }

            {
                addBlog(addBlog6, adminToken);
            }

            // get blogs page 2 and delete blog3
            {
                jsonMap = mapper.readValue(getBlog2,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminToken.getPayload());

                GetBlogRule rule = new GetBlogRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String)jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                List blogs = (List)jsonMap.get("blogs");
                assertEquals(2, blogs.size());

                Map<String, Object> blog = (Map<String, Object>)blogs.get(1);
                assertEquals("Test blog3", blog.get("title"));

                // delete blog3 here
                jsonMap = mapper.readValue(delBlog,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminToken.getPayload());
                Map<String, Object> data = (Map<String, Object>) jsonMap.get("data");
                data.put("@rid", blog.get("@rid"));
                DelBlogRule valRule = new DelBlogRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                DelBlogEvRule delRule = new DelBlogEvRule();
                ruleResult = delRule.execute(jsonMap);
                assertTrue(ruleResult);

            }

            // get blogs page 2 and second blog should be blog2
            {
                jsonMap = mapper.readValue(getBlog2,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminToken.getPayload());

                GetBlogRule rule = new GetBlogRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String)jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                List blogs = (List)jsonMap.get("blogs");
                assertEquals(2, blogs.size());
                Map<String, Object> blog = (Map<String, Object>)blogs.get(1);
                assertEquals("Test blog2", blog.get("title"));
            }

            // get blogs page 2 and update blog4
            {
                jsonMap = mapper.readValue(getBlog2,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminToken.getPayload());

                GetBlogRule rule = new GetBlogRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String)jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                List blogs = (List)jsonMap.get("blogs");
                assertEquals(2, blogs.size());
                Map<String, Object> blog = (Map<String, Object>)blogs.get(0);
                assertEquals("Test blog4", blog.get("title"));

                // update blog4 here
                jsonMap = mapper.readValue(updBlog,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminToken.getPayload());
                Map<String, Object> data = (Map<String, Object>) jsonMap.get("data");
                blog.put("content", "new content");
                blog.put("tags", "test, java, php");
                jsonMap.put("data", blog);
                UpdBlogRule valRule = new UpdBlogRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                UpdBlogEvRule updRule = new UpdBlogEvRule();
                ruleResult = updRule.execute(jsonMap);
                assertTrue(ruleResult);

            }

            // get blogs page 1, should be blog4 and blog6
            {
                jsonMap = mapper.readValue(getBlog1,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", userToken.getPayload());

                GetBlogRule rule = new GetBlogRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String)jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                assertEquals(5, (int)jsonMap.get("total"));
                List blogs = (List)jsonMap.get("blogs");
                assertEquals(2, blogs.size());
                Map<String, Object> blog = (Map<String, Object>)blogs.get(0);

                assertEquals("Test blog4", blog.get("title"));
                assertEquals("new content", blog.get("content"));
                String tags = (String)blog.get("tags");
                assertEquals("java,test,php", tags);

                blog = (Map<String, Object>)blogs.get(1);
                assertEquals("Test blog6", blog.get("title"));

            }

            // get blogs page 2  blog5 and blog2
            {
                jsonMap = mapper.readValue(getBlog2,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", userToken.getPayload());

                GetBlogRule rule = new GetBlogRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String)jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                assertEquals(5, jsonMap.get("total"));
                List blogs = (List)jsonMap.get("blogs");
                assertEquals(2, blogs.size());

                Map<String, Object> blog = (Map<String, Object>)blogs.get(0);
                assertEquals("Test blog5", blog.get("title"));
                blog = (Map<String, Object>)blogs.get(1);
                assertEquals("Test blog2", blog.get("title"));

            }

            // get blogs page 3, blog1 and up vote it
            {
                jsonMap = mapper.readValue(getBlog3,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", userToken.getPayload());

                GetBlogRule rule = new GetBlogRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String)jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                assertEquals(5, jsonMap.get("total"));
                List blogs = (List)jsonMap.get("blogs");
                assertEquals(1, blogs.size());
                Map<String, Object> blog = (Map<String, Object>)blogs.get(0);
                assertEquals("Test blog1", blog.get("title"));
                String blogRid = (String)blog.get("@rid");
                // up vote it.
                jsonMap = mapper.readValue(upBlog,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", userToken.getPayload());
                Map<String,Object> data = (Map<String, Object>)jsonMap.get("data");
                data.put("@rid", blogRid);
                UpBlogRule valRule = new UpBlogRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                UpBlogEvRule upRule = new UpBlogEvRule();
                upRule.execute(jsonMap);
                assertTrue(ruleResult);

                String b = upRule.getJsonByRid(blogRid);
                jsonMap = mapper.readValue(b,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                List upList = (List)jsonMap.get("upUsers");
                assertEquals(1, upList.size());
                List downList = (List)jsonMap.get("downUsers");
                assertNull(downList);

                // upvote it again with the same user, nothing should be changed.
                jsonMap = mapper.readValue(upBlog,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", userToken.getPayload());
                data = (Map<String, Object>)jsonMap.get("data");
                data.put("@rid", blogRid);
                valRule = new UpBlogRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                upRule = new UpBlogEvRule();
                upRule.execute(jsonMap);
                assertTrue(ruleResult);

                b = upRule.getJsonByRid(blogRid);
                jsonMap = mapper.readValue(b,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                upList = (List)jsonMap.get("upUsers");
                assertEquals(1, upList.size());
                downList = (List)jsonMap.get("downUsers");
                assertNull(downList);

            }

            // get blogs page 3, blog1 and up vote it by admin
            {
                jsonMap = mapper.readValue(getBlog3,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminToken.getPayload());

                GetBlogRule rule = new GetBlogRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String)jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                assertEquals(5, jsonMap.get("total"));
                List blogs = (List)jsonMap.get("blogs");
                assertEquals(1, blogs.size());
                Map<String, Object> blog = (Map<String, Object>)blogs.get(0);
                assertEquals("Test blog1", blog.get("title"));
                String blogRid = (String)blog.get("@rid");
                // up vote it again
                jsonMap = mapper.readValue(upBlog,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminToken.getPayload());
                Map<String,Object> data = (Map<String, Object>)jsonMap.get("data");
                data.put("@rid", blogRid);
                UpBlogRule valRule = new UpBlogRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                UpBlogEvRule upRule = new UpBlogEvRule();
                upRule.execute(jsonMap);
                assertTrue(ruleResult);

                String b = upRule.getJsonByRid(blogRid);
                jsonMap = mapper.readValue(b,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                List upList = (List)jsonMap.get("upUsers");
                assertEquals(2, upList.size());
                List downList = (List)jsonMap.get("downUsers");
                assertNull(downList);

                // up vote again by admin, nothing should be changed this time.
                jsonMap = mapper.readValue(upBlog,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminToken.getPayload());
                data = (Map<String, Object>)jsonMap.get("data");
                data.put("@rid", blogRid);
                valRule = new UpBlogRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                upRule = new UpBlogEvRule();
                upRule.execute(jsonMap);
                assertTrue(ruleResult);

                b = upRule.getJsonByRid(blogRid);
                jsonMap = mapper.readValue(b,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                upList = (List)jsonMap.get("upUsers");
                assertEquals(2, upList.size());
                downList = (List)jsonMap.get("downUsers");
                assertNull(downList);

            }

            // get blogs page 3 and down vote it by admin, should be upVoted by test and downVoted by admin
            {
                jsonMap = mapper.readValue(getBlog3,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminToken.getPayload());

                GetBlogRule rule = new GetBlogRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String)jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                assertEquals(5, jsonMap.get("total"));
                List blogs = (List)jsonMap.get("blogs");
                assertEquals(1, blogs.size());
                Map<String, Object> blog = (Map<String, Object>)blogs.get(0);
                assertEquals("Test blog1", blog.get("title"));
                String blogRid = (String)blog.get("@rid");
                // down vote it again
                jsonMap = mapper.readValue(downBlog,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminToken.getPayload());
                Map<String,Object> data = (Map<String, Object>)jsonMap.get("data");
                data.put("@rid", blogRid);
                DownBlogRule valRule = new DownBlogRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                DownBlogEvRule downRule = new DownBlogEvRule();
                downRule.execute(jsonMap);
                assertTrue(ruleResult);
                String b = downRule.getJsonByRid(blogRid);
                jsonMap = mapper.readValue(b,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                List upList = (List)jsonMap.get("upUsers");
                assertEquals(1, upList.size());
                List downList = (List)jsonMap.get("downUsers");
                assertEquals(1, downList.size());

                jsonMap = mapper.readValue(downBlog,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminToken.getPayload());
                data = (Map<String, Object>)jsonMap.get("data");
                data.put("@rid", blogRid);
                valRule = new DownBlogRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                downRule = new DownBlogEvRule();
                downRule.execute(jsonMap);
                assertTrue(ruleResult);
                b = downRule.getJsonByRid(blogRid);
                jsonMap = mapper.readValue(b,
                        new TypeReference<HashMap<String, Object>>() {
                        });

                upList = (List)jsonMap.get("upUsers");
                assertEquals(1, upList.size());
                downList = (List)jsonMap.get("downUsers");
                assertEquals(1, downList.size());

            }

            // get blogs page 3 and update blog1 tags
            {
                jsonMap = mapper.readValue(getBlog3,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminToken.getPayload());

                GetBlogRule rule = new GetBlogRule();
                ruleResult = rule.execute(jsonMap);
                assertTrue(ruleResult);
                String result = (String)jsonMap.get("result");
                jsonMap = mapper.readValue(result,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                assertEquals(5, jsonMap.get("total"));
                List blogs = (List)jsonMap.get("blogs");
                assertEquals(1, blogs.size());
                Map<String, Object> blog = (Map<String, Object>)blogs.get(0);
                assertEquals("Test blog1", blog.get("title"));
                String blogRid = (String)blog.get("@rid");

                // update blog1 here
                jsonMap = mapper.readValue(updBlog,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                jsonMap.put("payload", adminToken.getPayload());
                Map<String, Object> data = (Map<String, Object>) jsonMap.get("data");
                blog.put("content", "new content");
                blog.put("tags", "test, java, php");
                jsonMap.put("data", blog);
                UpdBlogRule valRule = new UpdBlogRule();
                ruleResult = valRule.execute(jsonMap);
                assertTrue(ruleResult);
                UpdBlogEvRule updRule = new UpdBlogEvRule();
                ruleResult = updRule.execute(jsonMap);
                assertTrue(ruleResult);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addBlog(String json, JsonToken token) throws Exception {
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        boolean ruleResult = false;

        jsonMap = mapper.readValue(json,
                new TypeReference<HashMap<String, Object>>() {
                });
        jsonMap.put("payload", token.getPayload());

        AddBlogRule valRule = new AddBlogRule();
        ruleResult = valRule.execute(jsonMap);
        assertTrue(ruleResult);

        AddBlogEvRule rule = new AddBlogEvRule();
        ruleResult = rule.execute(jsonMap);
        assertTrue(ruleResult);
    }

}