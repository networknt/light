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
 * Created by husteve on 10/24/2014.
 */
public class PageLoader extends Loader {
    static String pageFolder = "page";
    static Map<String, String> pageMap = new HashMap<String, String>();

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
                System.out.println("Usage: PageLoader host userId password");
                System.exit(1);
            }

            File folder = getFileFromResourceFolder(pageFolder);
            if(folder != null) {
                httpclient = HttpClients.createDefault();
                // login as owner here
                login(host, userId, password);
                getPageMap(host);

                File[] listOfFiles = folder.listFiles();
                for (int i = 0; i < listOfFiles.length; i++) {
                    loadPageFile(host, listOfFiles[i]);
                }
                httpclient.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * Get all pages from the server and construct a map in order to compare content
     * to detect changes or not.
     *
     */
    private static void getPageMap(String host) {

        Map<String, Object> inputMap = new HashMap<String, Object>();
        inputMap.put("category", "page");
        inputMap.put("name", "getPageMap");
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
            System.out.println("Got page map from server");
            if(json != null && json.trim().length() > 0) {
                pageMap = ServiceLocator.getInstance().getMapper().readValue(json,
                        new TypeReference<HashMap<String, String>>() {
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

    private static void loadPageFile(String host, File file) {
        Scanner scan = null;
        try {
            scan = new Scanner(file, Loader.encoding);
            // the content is only the data portion. convert to map
            String content = scan.useDelimiter("\\Z").next();
            String pageId = file.getName();
            pageId = pageId.substring(0, pageId.lastIndexOf('.'));
            if(content != null && !content.equals(pageMap.get(pageId))) {
                //System.out.println(content);
                //System.out.println(pageMap.get(id));
                Map<String, Object> inputMap = new HashMap<String, Object>();
                inputMap.put("category", "page");
                inputMap.put("name", "impPage");
                inputMap.put("readOnly", false);
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("pageId", pageId);
                data.put("content", content);
                inputMap.put("data", data);
                HttpPost httpPost = new HttpPost(host + "/api/rs");
                httpPost.addHeader("Authorization", "Bearer " + jwt);
                StringEntity input = new StringEntity(ServiceLocator.getInstance().getMapper().writeValueAsString(inputMap));
                input.setContentType("application/json");
                httpPost.setEntity(input);
                CloseableHttpResponse response = httpclient.execute(httpPost);

                try {
                    System.out.println("Page: " + file.getAbsolutePath() + " is loaded with status " + response.getStatusLine());
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
            } else {
                System.out.println("Skip file " + pageId);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (scan != null) scan.close();
        }
    }
}
