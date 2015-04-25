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

package com.networknt.light.server.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.light.server.LightServer;
import com.networknt.light.util.ServiceLocator;
import io.undertow.util.StatusCodes;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 8/25/2014.
 */
public class RestHandlerTest extends TestCase {

    String getUserByUserId = "{\"readOnly\":true,\"category\":\"user\",\"name\":\"getUser\",\"data\":{\"userId\":\"steve\"}}";
    String logOutUser = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"logOutUser\",\"data\":{\"userId\":\"steve\"}}";
    String delUser = "{\"readOnly\": false, \"category\": \"user\", \"name\": \"delUser\", \"data\": {\"userId\":\"steve\"}}";

    String signUpJson = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signUpUser\",\"data\":{\"userId\":\"steve\",\"email\":\"steve@gmail.com\",\"password\":\"abcdefg\",\"passwordConfirm\":\"abcdefg\",\"firstName\":\"steve\",\"lastName\":\"hu\"}}";
    String getUserByEmail = "{\"readOnly\":true,\"category\":\"user\",\"name\":\"getUser\",\"data\":{\"email\":\"steve@gmail.com\"}}";

    String signInJsonEmail = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"userIdEmail\":\"steve@gmail.com\",\"password\":\"abcdefg\",\"rememberMe\":false}}";
    String signInByUserId = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"signInUser\",\"data\":{\"userIdEmail\":\"steve\",\"password\":\"abcdefg\",\"rememberMe\":true}}";

    String updPasswordJson = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"updPassword\",\"data\":{\"userId\":\"steve\",\"password\":\"abcdefg\",\"newPassword\":\"123456\",\"passwordConfirm\":\"123456\"}}";
    String signInJsonNewPass = "{\"readOnly\": true, \"category\": \"user\", \"name\": \"signInUser\", \"data\": {\"userIdEmail\":\"steveu@gmail.com\", \"password\": \"123456\"}}";

    String updProfileJson = "{\"readOnly\":false,\"category\":\"user\",\"name\":\"updProfile\",\"data\":{\"userId\":\"steve\",\"firstName\":\"Steve\",\"lastName\":\"Hu\"}}";


    String addJson = "{\"readOnly\": false, \"category\": \"form\", \"name\": \"addForm\", \"data\": {\"id\": \"com.networknt.light.common.test.json\", \"schema\": {\"type\": \"object\", \"title\": \"Comment\",\"properties\": { \"name\":  { \"title\": \"Name\",\"type\": \"string\"}, \"email\":  {\n        \"title\": \"Email\",\n        \"type\": \"string\",\n        \"pattern\": \"^\\\\S+@\\\\S+$\",\n        \"description\": \"Email will be used for evil.\"\n      },\n      \"comment\": {\n        \"title\": \"Comment\",\n        \"type\": \"string\",\n        \"maxLength\": 20,\n        \"validationMessage\": \"Don't be greedy!\"\n      }\n    },\n    \"required\": [\"name\",\"email\",\"comment\"]\n  },\n  \"form\": [\n    \"name\",\n    \"email\",\n    {\n      \"key\": \"comment\",\n      \"type\": \"textarea\"\n    },\n    {\n      \"type\": \"submit\",\n\t  \"style\": \"btn-info\",\n      \"title\": \"OK\"} ]}}";
    String getJson = "{\"readOnly\": true, \"category\": \"form\", \"name\": \"getForm\", \"data\": {\"id\":\"com.networknt.light.common.test.json\"}}";
    String updJson = "{\"readOnly\": false, \"category\": \"form\", \"name\": \"updForm\", \"data\": {\"id\": \"com.networknt.light.common.test.json\", \"version\": 0, \"schema\": {\"type\": \"object\", \"title\": \"Updated Comment\",\"properties\": { \"name\":  { \"title\": \"Name\",\"type\": \"string\"}, \"email\":  {\n        \"title\": \"Email\",\n        \"type\": \"string\",\n        \"pattern\": \"^\\\\S+@\\\\S+$\",\n        \"description\": \"Email will be used for evil.\"\n      },\n      \"comment\": {\n        \"title\": \"Comment\",\n        \"type\": \"string\",\n        \"maxLength\": 20,\n        \"validationMessage\": \"Don't be greedy!\"\n      }\n    },\n    \"required\": [\"name\",\"email\",\"comment\"]\n  },\n  \"form\": [\n    \"name\",\n    \"email\",\n    {\n      \"key\": \"comment\",\n      \"type\": \"textarea\"\n    },\n    {\n      \"type\": \"submit\",\n\t  \"style\": \"btn-info\",\n      \"title\": \"OK\"} ]}}";
    String getAllJson = "{\"readOnly\": true, \"category\": \"form\", \"name\": \"getAllForm\"}";
    String delJson = "{\"readOnly\": false, \"category\": \"form\", \"name\": \"delForm\", \"data\": {\"id\":\"com.networknt.light.common.test.json\", \"version\": 1}}";

    String getMenuJson = "{\"readOnly\": true, \"category\": \"menu\", \"name\": \"getMenu\", \"data\": {\"host\":\"injector\"}}";

    CloseableHttpClient httpclient = null;
    public RestHandlerTest(String name) {
        super(name);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(RestHandlerTest.class);
        return suite;
    }

    public void setUp() throws Exception {
        LightServer.start();
        httpclient = HttpClients.createDefault();
        super.setUp();
    }

    public void tearDown() throws Exception {   
        LightServer.stop();
        httpclient.close();
        super.tearDown();
    }

    /*
    public void testRewrite() throws Exception {
        HttpGet get = new HttpGet("http://example:8080/page/a");
        HttpResponse response = httpclient.execute(get);
        Assert.assertEquals(StatusCodes.OK, response.getStatusLine().getStatusCode());
    }
    */

    public void testUser() throws Exception {

        /*
        postCleanUpUser();
        postSignUpUser();
        postSignInUser();
        postGetUserByUserId();
        postGetUserByEmail();
        postAddForm();
        postGetForm();
        getForm();
        postUpdForm();
        postGetFormVerifyUpdate();
        postGetAllForm();
        postDelForm();
        postGetFormVerifyDelete();
        */
    }

    /*
    public void testForm() throws Exception {
        postAddForm();
        postGetForm();
        getForm();
        postUpdForm();
        postGetFormVerifyUpdate();
        postGetAllForm();
        postDelForm();
        postGetFormVerifyDelete();
    }
    */
    /*
    public void testMenu() throws Exception {
        postGetMenu();

    }
    */

    private void postCleanUpUser() throws Exception {
        System.out.println("postCleanUpUser starts");
        StatusLine statusLine = null;
        // getUser and check status
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(getUserByUserId);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

        // delete user if it exists.
        if(statusLine.getStatusCode() == 200) {
            postDelUser();
        }

        System.out.println("postCleanUpUser ends");
    }

    private void postDelUser() throws Exception {
        System.out.println("postDelUser starts");
        // getUser and check status
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(delUser);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            assertEquals(200, response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        System.out.println("postDelUser ends");
    }

    private void postSignUpUser() throws Exception {
        System.out.println("postSignUpUser starts");
        // getUser and check status
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(signUpJson);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            assertEquals(200, response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        System.out.println("postSignUpUser ends");
    }

    private void postSignInUser() throws Exception {
        System.out.println("postSignInUser starts");
        // getUser and check status
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(signInByUserId);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            assertEquals(200, response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        System.out.println("postSignInUser ends");
    }

    private void postGetUserByUserId() throws Exception {
        System.out.println("postGetUserByUserId starts");
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(getUserByUserId);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            assertEquals(200, response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        System.out.println("postGetUserByUserId ends");
    }

    private void postGetUserByEmail() throws Exception {
        System.out.println("postGetUserByEmail starts");
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(getUserByEmail);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            assertEquals(200, response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        System.out.println("postGetUserByEmail ends");
    }

    private void postGetMenu() throws Exception {
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(getMenuJson);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
            Map<String, Object> jsonMap = mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});
            List<Map<String, Object>> result = (List<Map<String, Object>>) jsonMap.get("data");
            assertTrue(result.size() == 5);
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

    }


    private void getForm() {

        /*
        HttpGet httpGet = new HttpGet("http://targethost/homepage");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        // The underlying HTTP connection is still held by the response object
        // to allow the response content to be streamed directly from the network socket.
        // In order to ensure correct deallocation of system resources
        // the user MUST call CloseableHttpResponse#close() from a finally clause.
        // Please note that if response content is not fully consumed the underlying
        // connection cannot be safely re-used and will be shut down and discarded
        // by the connection manager.
        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }
        */
        boolean result = true;
        assertTrue(result);
    }

    private void postAddForm() throws Exception {

        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(addJson);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            assertEquals("{\"data\":\"success\"}", json);
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

    }

    private void postGetForm() throws Exception {
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(getJson);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
            Map<String, Object> jsonMap = mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});
            Map<String, Object> result = (Map<String, Object>) jsonMap.get("data");
            assertTrue(result.size() == 3);
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

    }

    private void postUpdForm() throws Exception {
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(updJson);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            assertEquals("{\"data\":\"success\"}", json);
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

    }

    private void postGetFormVerifyUpdate() throws Exception {
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(getJson);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            ObjectMapper mapper = ServiceLocator.getInstance().getMapper();
            Map<String, Object> jsonMap = mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});
            Map<String, Object> result = (Map<String, Object>) jsonMap.get("data");
            assertTrue(result.size() == 3);

            Map<String, Object> schema = (Map<String, Object>)result.get("schema");
            String title = (String)schema.get("title");
            assertEquals(title, "Updated Comment");

            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
    }

    private void postGetAllForm() throws Exception {
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(getAllJson);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

    }

    private void postDelForm() throws Exception {
        System.out.println("postDelForm starts");
        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(delJson);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            assertEquals("{\"data\":\"success\"}", json);
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        System.out.println("postDelForm ends");
    }

    private void postGetFormVerifyDelete() throws Exception {
        System.out.println("postGetFormVerifyDelete starts");

        HttpPost httpPost = new HttpPost("http://example:8080/api/rs");
        StringEntity input = new StringEntity(getJson);
        input.setContentType("application/json");
        httpPost.setEntity(input);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            System.out.println("json = " + json);
            assertTrue(json.contains("cannot be found"));

            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        System.out.println("postGetFormVerifyDelete ends");
    }

}
