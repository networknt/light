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

package com.networknt.light.rule.catalog;

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
 * Created by steve on 25/04/15.
 */
public class CatalogRuleTest extends TestCase {
    ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
    String signInOwner = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"host\":\"example\",\"userIdEmail\":\"stevehu\",\"password\":\"123456\",\"rememberMe\":true, \"clientId\":\"example@Browser\"}}";

    // for admin
    String getExamCatalog = "{\"readOnly\":true,\"category\":\"catalog\",\"name\":\"getCatalog\",\"data\":{\"host\":\"www.example.com\"}}";

    // for catalog home
    String getExamTree = "{\"readOnly\":true,\"category\":\"catalog\",\"name\":\"getCatalogTree\",\"data\":{\"host\":\"www.example.com\"}}";

    String delExamCatalog = "{\"readOnly\":false,\"category\":\"catalog\",\"name\":\"delCatalog\",\"data\":{\"host\":\"www.example.com\"}}";


    String addExamCatalog1 = "{\"readOnly\":false,\"category\":\"catalog\",\"name\":\"addCatalog\",\"data\":{\"host\":\"www.example.com\",\"catalogId\":\"catalog1\",\"description\":\"catalog1\"}}";
    String addExamCatalog11 = "{\"readOnly\":false,\"category\":\"catalog\",\"name\":\"addCatalog\",\"data\":{\"host\":\"www.example.com\",\"catalogId\":\"catalog11\",\"description\":\"catalog11\"}}";
    String addExamCatalog12 = "{\"readOnly\":false,\"category\":\"catalog\",\"name\":\"addCatalog\",\"data\":{\"host\":\"www.example.com\",\"catalogId\":\"catalog12\",\"description\":\"catalog12\"}}";
    String addExamCatalog111 = "{\"readOnly\":false,\"category\":\"catalog\",\"name\":\"addCatalog\",\"data\":{\"host\":\"www.example.com\",\"catalogId\":\"catalog111\",\"description\":\"catalog111\"}}";
    String addExamCatalog121 = "{\"readOnly\":false,\"category\":\"catalog\",\"name\":\"addCatalog\",\"data\":{\"host\":\"www.example.com\",\"catalogId\":\"catalog121\",\"description\":\"catalog121\"}}";
    String addExamCatalog112 = "{\"readOnly\":false,\"category\":\"catalog\",\"name\":\"addCatalog\",\"data\":{\"host\":\"www.example.com\",\"catalogId\":\"catalog112\",\"description\":\"catalog112\"}}";
    String addExamCatalog2 = "{\"readOnly\":false,\"category\":\"catalog\",\"name\":\"addCatalog\",\"data\":{\"host\":\"www.example.com\",\"catalogId\":\"catalog2\",\"description\":\"catalog2\"}}";

    String updExamCatalog = "{\"readOnly\":false,\"category\":\"catalog\",\"name\":\"updCatalog\",\"data\":{\"host\":\"www.example.com\"}}";

    String addExamProduct1 = "{\"readOnly\":false,\"category\":\"catalog\",\"name\":\"addProduct\",\"data\":{\"host\":\"www.example.com\",\"name\":\"product1\",\"description\":\"product 1\",\"variants\":[{\"sku\":\"sku1_1\",\"price\":19.99,\"type\":\"sku_1_type\",\"image\":\"/image/1.jpg\"},{\"sku\":\"sku1_2\",\"price\":19.99,\"type\":\"sku_2_type\",\"image\":\"/image/1.jpg\"}],\"parentId\":\"catalog1\"}}";
    String addExamProduct11 = "{\"readOnly\":false,\"category\":\"catalog\",\"name\":\"addProduct\",\"data\":{\"host\":\"www.example.com\",\"name\":\"product11\",\"description\":\"product 11\",\"variants\":[{\"sku\":\"sku11_1\",\"price\":29.99,\"type\":\"sku_11_1_type\",\"image\":\"/image/11.jpg\"},{\"sku\":\"sku11_2\",\"price\":29.99,\"type\":\"sku_11_2_type\",\"image\":\"/image/11.jpg\"}],\"parentId\":\"catalog11\"}}";
    String addExamProduct12 = "{\"readOnly\":false,\"category\":\"catalog\",\"name\":\"addProduct\",\"data\":{\"host\":\"www.example.com\",\"name\":\"product12\",\"description\":\"product 12\",\"variants\":[{\"sku\":\"sku12_1\",\"price\":39.99,\"type\":\"sku_12_1_type\",\"image\":\"/image/12.jpg\"},{\"sku\":\"sku12_2\",\"price\":39.97,\"type\":\"sku_12_2_type\",\"image\":\"/image/12.jpg\"}],\"parentId\":\"catalog12\"}}";
    String addExamProduct111 = "{\"readOnly\":false,\"category\":\"catalog\",\"name\":\"addProduct\",\"data\":{\"host\":\"www.example.com\",\"name\":\"product111\",\"description\":\"product 111\",\"variants\":[{\"sku\":\"sku111_1\",\"price\":39.99,\"type\":\"sku_111_1_type\",\"image\":\"/image/111.jpg\"},{\"sku\":\"sku111_2\",\"price\":39.97,\"type\":\"sku_111_2_type\",\"image\":\"/image/111.jpg\"}],\"parentId\":\"catalog111\"}}";
    String addExamProduct121 = "{\"readOnly\":false,\"category\":\"catalog\",\"name\":\"addProduct\",\"data\":{\"host\":\"www.example.com\",\"name\":\"product121\",\"description\":\"product 121\",\"variants\":[{\"sku\":\"sku121_1\",\"price\":39.99,\"type\":\"sku_121_1_type\",\"image\":\"/image/121.jpg\"},{\"sku\":\"sku121_2\",\"price\":39.97,\"type\":\"sku_121_2_type\",\"image\":\"/image/121.jpg\"}],\"parentId\":\"catalog121\"}}";
    String addExamProduct112 = "{\"readOnly\":false,\"category\":\"catalog\",\"name\":\"addProduct\",\"data\":{\"host\":\"www.example.com\",\"name\":\"product112\",\"description\":\"product 112\",\"variants\":[{\"sku\":\"sku112_1\",\"price\":39.99,\"type\":\"sku_112_1_type\",\"image\":\"/image/112.jpg\"},{\"sku\":\"sku112_2\",\"price\":39.97,\"type\":\"sku_112_2_type\",\"image\":\"/image/112.jpg\"}],\"parentId\":\"catalog112\"}}";
    String addExamProduct2 = "{\"readOnly\":false,\"category\":\"catalog\",\"name\":\"addProduct\",\"data\":{\"host\":\"www.example.com\",\"name\":\"product2\",\"description\":\"product 2\",\"variants\":[{\"sku\":\"sku2_1\",\"price\":39.99,\"type\":\"sku_2_1_type\",\"image\":\"/image/2.jpg\"},{\"sku\":\"sku2_2\",\"price\":39.97,\"type\":\"sku_2_2_type\",\"image\":\"/image/2.jpg\"}],\"parentId\":\"catalog2\"}}";

    String getExamCatalogProduct1 = "{\"readOnly\":true,\"category\":\"catalog\",\"name\":\"getCatalogProduct\",\"data\":{\"host\":\"www.example.com\",\"pageSize\":3,\"pageNo\":1}}";
    String getExamCatalogProduct2 = "{\"readOnly\":true,\"category\":\"catalog\",\"name\":\"getCatalogProduct\",\"data\":{\"host\":\"www.example.com\",\"pageSize\":3,\"pageNo\":2}}";
    String getExamCatalogProduct3 = "{\"readOnly\":true,\"category\":\"catalog\",\"name\":\"getCatalogProduct\",\"data\":{\"host\":\"www.example.com\",\"pageSize\":3,\"pageNo\":3}}";

    public CatalogRuleTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(CatalogRuleTest.class);
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

        Map<String, Object> examCatalog1 = null;
        Map<String, Object> examCatalog11 = null;
        Map<String, Object> examCatalog12 = null;
        Map<String, Object> examCatalog111 = null;
        Map<String, Object> examCatalog121 = null;
        Map<String, Object> examCatalog112 = null;
        Map<String, Object> examCatalog2 = null;

        JsonToken ownerToken = null;
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

        // get all catalogs from example and delete them all for cleaning up
        {
            jsonMap = mapper.readValue(getExamCatalog,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());

            GetCatalogRule rule = new GetCatalogRule();
            ruleResult = rule.execute(jsonMap);
            if(ruleResult) {
                String result = (String)jsonMap.get("result");
                List<Map<String, Object>> catalogs = mapper.readValue(result,
                        new TypeReference<ArrayList<HashMap<String, Object>>>() {
                        });
                for(Map<String, Object> catalog: catalogs) {
                    String rid = (String)catalog.get("@rid");
                    jsonMap = mapper.readValue(delExamCatalog,
                            new TypeReference<HashMap<String, Object>>() {
                            });
                    jsonMap.put("payload", ownerToken.getPayload());
                    Map<String, Object> data = (Map<String,Object>)jsonMap.get("data");
                    data.put("@rid", rid);
                    DelCatalogRule delRule = new DelCatalogRule();
                    ruleResult = delRule.execute(jsonMap);
                    assertTrue(ruleResult);
                    Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
                    DelCatalogEvRule delEvRule = new DelCatalogEvRule();
                    ruleResult = delEvRule.execute(eventMap);
                    assertTrue(ruleResult);
                }
            }
        }

        // add example catalogs and demo catalogs
        {
            addCatalog(addExamCatalog1, ownerToken);
            addCatalog(addExamCatalog11, ownerToken);
            addCatalog(addExamCatalog12, ownerToken);
            addCatalog(addExamCatalog111, ownerToken);
            addCatalog(addExamCatalog121, ownerToken);
            addCatalog(addExamCatalog112, ownerToken);
            addCatalog(addExamCatalog2, ownerToken);

        }

        // get all catalogs from example to make sure 7 in total. save rids
        {
            jsonMap = mapper.readValue(getExamCatalog,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());

            GetCatalogRule rule = new GetCatalogRule();
            ruleResult = rule.execute(jsonMap);
            if(ruleResult) {
                String result = (String) jsonMap.get("result");
                List<Map<String, Object>> catalogs = mapper.readValue(result,
                        new TypeReference<ArrayList<HashMap<String, Object>>>() {
                        });
                assertEquals(7, catalogs.size());

                examCatalog1 = catalogs.get(0);
                examCatalog11 = catalogs.get(1);
                examCatalog12 = catalogs.get(2);
                examCatalog111 = catalogs.get(3);
                examCatalog121 = catalogs.get(4);
                examCatalog112 = catalogs.get(5);
                examCatalog2 = catalogs.get(6);
            }
        }

        // update examCatalog1 to add two children
        {
            jsonMap = mapper.readValue(updExamCatalog,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());
            Map<String, Object> data = (Map<String,Object>)jsonMap.get("data");
            data.putAll(examCatalog1);
            List children = new ArrayList<String>();
            children.add(examCatalog11.get("@rid"));
            children.add(examCatalog12.get("@rid"));
            data.put("out_Own", children);

            UpdCatalogRule rule = new UpdCatalogRule();
            ruleResult = rule.execute(jsonMap);
            assertTrue(ruleResult);
            Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
            UpdCatalogEvRule evRule = new UpdCatalogEvRule();
            ruleResult = evRule.execute(eventMap);
            assertTrue(ruleResult);

        }

        // get all catalogs from example to make sure 7 in total. save rids again.
        {
            jsonMap = mapper.readValue(getExamCatalog,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());

            GetCatalogRule rule = new GetCatalogRule();
            ruleResult = rule.execute(jsonMap);
            if(ruleResult) {
                String result = (String) jsonMap.get("result");
                List<Map<String, Object>> catalogs = mapper.readValue(result,
                        new TypeReference<ArrayList<HashMap<String, Object>>>() {
                        });
                assertEquals(7, catalogs.size());

                examCatalog1 = catalogs.get(0);
                examCatalog11 = catalogs.get(1);
                examCatalog12 = catalogs.get(2);
                examCatalog111 = catalogs.get(3);
                examCatalog121 = catalogs.get(4);
                examCatalog112 = catalogs.get(5);
                examCatalog2 = catalogs.get(6);
            }
        }

        // update examCatalog11 to add two children
        {
            jsonMap = mapper.readValue(updExamCatalog,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());
            Map<String, Object> data = (Map<String,Object>)jsonMap.get("data");
            data.putAll(examCatalog11);
            List children = new ArrayList<String>();
            children.add(examCatalog111.get("@rid"));
            children.add(examCatalog112.get("@rid"));
            data.put("out_Own", children);

            UpdCatalogRule rule = new UpdCatalogRule();
            ruleResult = rule.execute(jsonMap);
            assertTrue(ruleResult);
            Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
            UpdCatalogEvRule evRule = new UpdCatalogEvRule();
            ruleResult = evRule.execute(eventMap);
            assertTrue(ruleResult);

        }

        // update examCatalog12 to add two children
        {
            jsonMap = mapper.readValue(updExamCatalog,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jsonMap.put("payload", ownerToken.getPayload());
            Map<String, Object> data = (Map<String,Object>)jsonMap.get("data");
            data.putAll(examCatalog12);
            List children = new ArrayList<String>();
            children.add(examCatalog121.get("@rid"));
            data.put("out_Own", children);

            UpdCatalogRule rule = new UpdCatalogRule();
            ruleResult = rule.execute(jsonMap);
            assertTrue(ruleResult);
            Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
            UpdCatalogEvRule evRule = new UpdCatalogEvRule();
            ruleResult = evRule.execute(eventMap);
            assertTrue(ruleResult);

        }

        // get all exam catalogs as a tree structure.
        {
            jsonMap = mapper.readValue(getExamTree,
                    new TypeReference<HashMap<String, Object>>() {
                    });

            GetCatalogTreeRule rule = new GetCatalogTreeRule();
            ruleResult = rule.execute(jsonMap);
            assertTrue(ruleResult);
            String result = (String)jsonMap.get("result");
            System.out.println("example catalog tree = " + result);
        }

        // add example products
        {
            addProduct(addExamProduct1, ownerToken);
            addProduct(addExamProduct11, ownerToken);
            addProduct(addExamProduct12, ownerToken);
            addProduct(addExamProduct111, ownerToken);
            addProduct(addExamProduct121, ownerToken);
            addProduct(addExamProduct112, ownerToken);
            addProduct(addExamProduct2, ownerToken);

        }

        // get catalog product for ExamCatalog1
        {
            String json = getCatalogProduct(getExamCatalogProduct1, (String)examCatalog1.get("@rid"), ownerToken);
            System.out.println("catalogProduct for ExamCatalog1" + json);

            json = getCatalogProduct(getExamCatalogProduct2, (String)examCatalog1.get("@rid"), ownerToken);
            System.out.println("catalogProduct for ExamCatalog2" + json);

            json = getCatalogProduct(getExamCatalogProduct3, (String)examCatalog1.get("@rid"), ownerToken);
            System.out.println("catalogProduct for ExamCatalog3" + json);

            json = getCatalogProduct(getExamCatalogProduct1, (String)examCatalog111.get("@rid"), ownerToken);
            System.out.println("catalogProduct for ExamCatalog111" + json);

        }

    }

    private void addCatalog(String json, JsonToken token) throws Exception {
        Map<String, Object> jsonMap = null;
        boolean ruleResult = false;
        jsonMap = mapper.readValue(json,
                new TypeReference<HashMap<String, Object>>() {
                });
        jsonMap.put("payload", token.getPayload());

        AddCatalogRule rule = new AddCatalogRule();
        ruleResult = rule.execute(jsonMap);
        assertTrue(ruleResult);
        Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
        AddCatalogEvRule evRule = new AddCatalogEvRule();
        ruleResult = evRule.execute(eventMap);
        assertTrue(ruleResult);
    }

    private void addProduct(String json, JsonToken token) throws Exception {
        Map<String, Object> jsonMap = null;
        boolean ruleResult = false;
        jsonMap = mapper.readValue(json,
                new TypeReference<HashMap<String, Object>>() {
                });
        jsonMap.put("payload", token.getPayload());

        AddProductRule rule = new AddProductRule();
        ruleResult = rule.execute(jsonMap);
        assertTrue(ruleResult);
        Map<String, Object> eventMap = (Map<String, Object>)jsonMap.get("eventMap");
        AddProductEvRule evRule = new AddProductEvRule();
        ruleResult = evRule.execute(eventMap);
        assertTrue(ruleResult);
    }

    private String getCatalogProduct(String json, String rid, JsonToken token) throws Exception {
        Map<String, Object> jsonMap = null;
        boolean ruleResult = false;
        jsonMap = mapper.readValue(json,
                new TypeReference<HashMap<String, Object>>() {
                });
        jsonMap.put("payload", token.getPayload());
        Map<String, Object> data = (Map<String, Object>)jsonMap.get("data");
        data.put("@rid", rid);
        GetCatalogProductRule rule = new GetCatalogProductRule();
        ruleResult = rule.execute(jsonMap);
        assertTrue(ruleResult);
        return (String)jsonMap.get("result");
    }
}
