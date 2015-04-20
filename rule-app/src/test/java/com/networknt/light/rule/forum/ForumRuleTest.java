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

package com.networknt.light.rule.forum;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Created by steve on 26/11/14.
 */
public class ForumRuleTest extends TestCase {
    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();

    String signInOwner = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"stevehu\",\"password\":\"123456\",\"rememberMe\":true, \"clientId\":\"example@Browser\"}}";
    String signInUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"test\",\"password\":\"123456\",\"rememberMe\":true, \"clientId\":\"example@Browser\"}}";

    String getForum = "{\"readOnly\":true,\"category\":\"forum\",\"name\":\"getForum\",\"data\":{\"host\":\"www.example.com\"}}";
    String getForumTree = "{\"readOnly\":true,\"category\":\"forum\",\"name\":\"getForum\",\"data\":{\"host\":\"www.example.com\"}}";
    String delForum = "{\"readOnly\":false,\"category\":\"forum\",\"name\":\"delForum\",\"data\":{\"host\":\"www.example.com\"}}";

    String addForum1 = "{\"readOnly\":false,\"category\":\"forum\",\"name\":\"addForum\",\"data\":{\"host\":\"www.example.com\",\"forumId\":\"Living\",\"description\":\"Living\"}}";
    String addForum2 = "{\"readOnly\":false,\"category\":\"forum\",\"name\":\"addForum\",\"data\":{\"host\":\"www.example.com\",\"forumId\":\"Plant\",\"description\":\"Plant\"}}";
    String addForum3 = "{\"readOnly\":false,\"category\":\"forum\",\"name\":\"addForum\",\"data\":{\"host\":\"www.example.com\",\"forumId\":\"Animal\",\"description\":\"Animal\"}}";
    String addForum4 = "{\"readOnly\":false,\"category\":\"forum\",\"name\":\"addForum\",\"data\":{\"host\":\"www.example.com\",\"forumId\":\"Tree\",\"description\":\"Tree\"}}";
    String addForum5 = "{\"readOnly\":false,\"category\":\"forum\",\"name\":\"addForum\",\"data\":{\"host\":\"www.example.com\",\"forumId\":\"Herb\",\"description\":\"Herb\"}}";
    String addForum6 = "{\"readOnly\":false,\"category\":\"forum\",\"name\":\"addForum\",\"data\":{\"host\":\"www.example.com\",\"forumId\":\"Pine\",\"description\":\"Pine\"}}";

    String updForum = "{\"readOnly\":false,\"category\":\"forum\",\"name\":\"updForum\",\"data\":{\"host\":\"www.example.com\"}}";


    public ForumRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(ForumRuleTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testExecute() throws Exception {
        Map<String, Object> jsonMap = null;
        boolean ruleResult = false;
        JsonToken ownerToken = null;
        JsonToken userToken = null;
        Map<String, Object> forumLiving = null;
        Map<String, Object> forumPlant = null;
        Map<String, Object> forumAnimal = null;
        Map<String, Object> forumTree = null;
        Map<String, Object> forumHerb = null;
        Map<String, Object> forumPine = null;

        // signIn owner by userId
        {
            jsonMap = mapper.readValue(signInOwner,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            SignInUserRule rule = new SignInUserRule();
            ruleResult = rule.execute(jsonMap);
            assertTrue(ruleResult);
            Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
            String result = (String)jsonMap.get("result");
            jsonMap = mapper.readValue(result,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            String jwt = (String)jsonMap.get("accessToken");
            ownerToken = JwtUtil.Deserialize(jwt);
            SignInUserEvRule evRule = new SignInUserEvRule();
            ruleResult = evRule.execute(eventMap);
            assertTrue(ruleResult);
        }

        // signIn user by userId
        {
            jsonMap = mapper.readValue(signInUser,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            SignInUserRule rule = new SignInUserRule();
            ruleResult = rule.execute(jsonMap);
            assertTrue(ruleResult);
            Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
            String result = (String)jsonMap.get("result");
            jsonMap = mapper.readValue(result,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            String jwt = (String)jsonMap.get("accessToken");
            userToken = JwtUtil.Deserialize(jwt);
            SignInUserEvRule evRule = new SignInUserEvRule();
            ruleResult = evRule.execute(eventMap);
            assertTrue(ruleResult);
        }

        // get all forum and delete them all for cleaning up
        {
            jsonMap = mapper.readValue(getForum,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());

            GetForumRule rule = new GetForumRule();
            ruleResult = rule.execute(jsonMap);
            if(ruleResult) {
                String result = (String)jsonMap.get("result");
                List<Map<String, Object>> forums = mapper.readValue(result,
                        new TypeReference<ArrayList<HashMap<String, Object>>>() {
                        });
                for(Map<String, Object> forum: forums) {
                    String rid = (String)forum.get("@rid");
                    jsonMap = mapper.readValue(delForum,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("payload", ownerToken.getPayload());
                    Map<String, Object> data = (Map<String,Object>)jsonMap.get("data");
                    data.put("@rid", rid);
                    DelForumRule delRule = new DelForumRule();
                    ruleResult = delRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    DelForumEvRule delEvRule = new DelForumEvRule();
                    ruleResult = delEvRule.execute(eventMap);
                    assertTrue(ruleResult);
                }
            }
        }

        // add forums
        {
            addForum(addForum1, ownerToken);
            addForum(addForum2, ownerToken);
            addForum(addForum3, ownerToken);
            addForum(addForum4, ownerToken);
            addForum(addForum5, ownerToken);
            addForum(addForum6, ownerToken);
        }

        // add forum6 again by owner failed as it exists
        {
            jsonMap = mapper.readValue(addForum6,
                    new TypeReference<HashMap<String, Object>>() {
                    });

            jsonMap.put("payload", ownerToken.getPayload());

            AddForumRule rule = new AddForumRule();
            ruleResult = rule.execute(jsonMap);
            assertFalse(ruleResult);
            assertEquals(400, jsonMap.get("responseCode"));
        }

        // get all forums to make sure 6 in total. save rids
        {
            jsonMap = mapper.readValue(getForum,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());

            GetForumRule rule = new GetForumRule();
            ruleResult = rule.execute(jsonMap);
            if(ruleResult) {
                String result = (String) jsonMap.get("result");
                List<Map<String, Object>> forums = mapper.readValue(result,
                        new TypeReference<ArrayList<HashMap<String, Object>>>() {
                        });
                assertEquals(6, forums.size());

                forumLiving = forums.get(0);
                forumPlant = forums.get(1);
                forumAnimal = forums.get(2);
                forumTree = forums.get(3);
                forumHerb = forums.get(4);
                forumPine = forums.get(5);
            }
        }

        // update plant by owner with new desc and two children tree and herb
        {
            jsonMap = mapper.readValue(updForum,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());
            Map<String, Object> data = (Map<String,Object>)jsonMap.get("data");
            data.putAll(forumPlant);
            data.put("description", "Plant1");
            List children = new ArrayList<String>();
            children.add(forumTree.get("@rid"));
            children.add(forumHerb.get("@rid"));
            data.put("out_Own", children);

            UpdForumRule rule = new UpdForumRule();
            ruleResult = rule.execute(jsonMap);
            assertTrue(ruleResult);
            Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
            UpdForumEvRule evRule = new UpdForumEvRule();
            ruleResult = evRule.execute(eventMap);
            assertTrue(ruleResult);
        }

        // get all forum as a tree structure.
        {
            jsonMap = mapper.readValue(getForumTree,
                    new TypeReference<HashMap<String, Object>>() {
                    });

            GetForumTreeRule rule = new GetForumTreeRule();
            ruleResult = rule.execute(jsonMap);
            assertTrue(ruleResult);
            String result = (String)jsonMap.get("result");
            System.out.println("result = " + result);
        }

        // get all forums to make sure 6 in total. save rids
        {
            jsonMap = mapper.readValue(getForum,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());

            GetForumRule rule = new GetForumRule();
            ruleResult = rule.execute(jsonMap);
            if(ruleResult) {
                String result = (String) jsonMap.get("result");
                List<Map<String, Object>> forums = mapper.readValue(result,
                        new TypeReference<ArrayList<HashMap<String, Object>>>() {
                        });
                assertEquals(6, forums.size());

                forumLiving = forums.get(0);
                forumPlant = forums.get(1);
                forumAnimal = forums.get(2);
                forumTree = forums.get(3);
                forumHerb = forums.get(4);
                forumPine = forums.get(5);
            }
        }

        // update tree to add pine as child
        {
            jsonMap = mapper.readValue(updForum,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());
            Map<String, Object> data = (Map<String,Object>)jsonMap.get("data");
            data.putAll(forumTree);
            List children = new ArrayList<String>();
            children.add(forumPine.get("@rid"));
            data.put("out_Own", children);

            UpdForumRule rule = new UpdForumRule();
            ruleResult = rule.execute(jsonMap);
            assertTrue(ruleResult);
            Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
            UpdForumEvRule evRule = new UpdForumEvRule();
            ruleResult = evRule.execute(eventMap);
            assertTrue(ruleResult);

        }

        // get all forums to check Living, Plant, Animal and Tree for children
        {
            jsonMap = mapper.readValue(getForum,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());

            GetForumRule rule = new GetForumRule();
            ruleResult = rule.execute(jsonMap);
            if(ruleResult) {
                String result = (String) jsonMap.get("result");
                List<Map<String, Object>> forums = mapper.readValue(result,
                        new TypeReference<ArrayList<HashMap<String, Object>>>() {
                        });
                assertEquals(6, forums.size());
                Map<String, Object> forum2 = forums.get(1);
                assertEquals("Plant1", forum2.get("description"));
                List<String> children = (List<String>)forum2.get("out_Own");
                assertEquals(2, children.size());


                Map<String, Object> forum4 = forums.get(3);
                children = (List<String>)forum4.get("out_Own");
                assertEquals(1, children.size());

            }
        }

        // get all forum as a tree structure.
        {
            jsonMap = mapper.readValue(getForumTree,
                    new TypeReference<HashMap<String, Object>>() {
                    });

            GetForumTreeRule rule = new GetForumTreeRule();
            ruleResult = rule.execute(jsonMap);
            assertTrue(ruleResult);
            String result = (String)jsonMap.get("result");
            System.out.println("result = " + result);
        }

        // delete forum Pines
        {

        }

        // delete forum Animals
        {

        }

        // get all to make sure
        // Trees children are updated
        // EdibleForestGarden children are updated
        {

        }
    }

    private void addForum(String json, JsonToken token) throws Exception {
        Map<String, Object> jsonMap = null;
        boolean ruleResult = false;
        jsonMap = mapper.readValue(json,
                new TypeReference<HashMap<String, Object>>() {
                });
        jsonMap.put("payload", token.getPayload());

        AddForumRule rule = new AddForumRule();
        ruleResult = rule.execute(jsonMap);
        assertTrue(ruleResult);
        Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
        AddForumEvRule evRule = new AddForumEvRule();
        ruleResult = evRule.execute(eventMap);
        assertTrue(ruleResult);
    }

}
