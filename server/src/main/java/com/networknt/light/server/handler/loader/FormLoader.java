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

package com.networknt.light.server.handler.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.metadata.schema.OType;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by steve on 23/08/14.
 *
 * This has to be called
 */
public class FormLoader extends Loader {
    static String formFolder = "form";
    static Map<String, Object> formMap = new HashMap<String, Object>();

    public static void main(String[] args) {
        try {
            String host = null;
            String userId = null;
            String password = null;
            if(args != null && args.length == 3) {
                host = args[0];
                userId = args[1];
                password = args[2];
                if(host.length() == 0 || userId.length() == 0 || password.length() == 0) {
                    System.out.println("host, userId and password are required");
                    System.exit(1);
                }
            } else {
                System.out.println("Usage: FormLoader host userId password");
                System.exit(1);
            }
            File folder = getFileFromResourceFolder(formFolder);
            if(folder != null) {
                httpclient = HttpClients.createDefault();
                // login as owner here
                login(host, userId, password);
                // get formMap for comparison
                getFormMap(host);

                File[] listOfFiles = folder.listFiles();
                for (int i = 0; i < listOfFiles.length; i++) {
                    loadFormFile(host, listOfFiles[i]);
                }
                httpclient.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * Get all forms from the server and construct a map in order to compare content
     * to detect changes or not.
     *
     */
    private static void getFormMap(String host) {

        Map<String, Object> inputMap = new HashMap<String, Object>();
        inputMap.put("category", "form");
        inputMap.put("name", "getFormMap");
        inputMap.put("readOnly", true);

        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(host + "/api/rs");
            httpPost.addHeader("Authorization", "Bearer " + jwt);
            StringEntity input = new StringEntity(ServiceLocator.getInstance().getMapper().writeValueAsString(inputMap));
            input.setContentType("application/json");
            httpPost.setEntity(input);
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
            String json = "";
            String line = "";
            while ((line = rd.readLine()) != null) {
                json = json + line;
            }
            EntityUtils.consume(entity);
            System.out.println("Got form map from server");
            if(json != null && json.trim().length() > 0) {
                formMap = ServiceLocator.getInstance().getMapper().readValue(json,
                        new TypeReference<HashMap<String, Map<String, Object>>>() {
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void loadFormFile(String host, File file) {
        Scanner scan = null;
        try {
            scan = new Scanner(file, Loader.encoding);
            // the content is only the data portion.
            String content = scan.useDelimiter("\\Z").next();
            // convert to map
            Map<String, Object> newMap = ServiceLocator.getInstance().getMapper().readValue(content,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            String id = (String)newMap.get("id");
            Map<String, Object> oldMap = (Map<String, Object>)formMap.get(id);
            boolean changed = false;
            if(oldMap == null) {
                // never loaded before.
                changed = true;
            } else {
                if(newMap.get("action") != null && !newMap.get("action").equals(oldMap.get("action"))) {
                    changed = true;
                }
                if(!changed && newMap.get("schema") != null && !newMap.get("schema").equals(oldMap.get("schema"))) {
                    changed = true;
                }
                if(!changed && newMap.get("form") != null && !newMap.get("form").equals(oldMap.get("form"))) {
                    changed = true;
                }
                if(!changed && newMap.get("modelData") != null && !newMap.get("modelData").equals(oldMap.get("modelData"))) {
                    changed = true;
                }
            }
            if(changed) {
                Map<String, Object> inputMap = new HashMap<String, Object>();
                inputMap.put("category", "form");
                inputMap.put("name", "impForm");
                inputMap.put("readOnly", false);

                Map<String, Object> data = new HashMap<String, Object>();
                data.put("content", content);
                inputMap.put("data", data);
                HttpPost httpPost = new HttpPost(host + "/api/rs");
                httpPost.addHeader("Authorization", "Bearer " + jwt);
                StringEntity input = new StringEntity(ServiceLocator.getInstance().getMapper().writeValueAsString(inputMap));
                input.setContentType("application/json");
                httpPost.setEntity(input);
                CloseableHttpResponse response = httpclient.execute(httpPost);

                try {
                    System.out.println("Form: " + file.getAbsolutePath() + " is loaded with status " + response.getStatusLine());
                    HttpEntity entity = response.getEntity();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
                    String json = "";
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        json = json + line;
                    }
                    //System.out.println("json = " + json);
                    EntityUtils.consume(entity);
                } finally {
                    response.close();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (scan != null) scan.close();
        }
    }
}
