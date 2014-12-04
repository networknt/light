package com.networknt.light.server.handler.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.util.ServiceLocator;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by steve on 23/08/14.
 */
public class Loader {
    public static final String encoding = "UTF-8";
    static CloseableHttpClient httpclient = null;
    static String jwt = null;

    protected static File getFileFromResourceFolder(String folder) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(folder);
        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        } finally {
            return file;
        }
    }
    protected static void login() throws Exception {
        Map<String, Object> inputMap = new HashMap<String, Object>();
        inputMap.put("category", "user");
        inputMap.put("name", "signInUser");
        inputMap.put("readOnly", false);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("userIdEmail", "stevehu");
        data.put("password", "123456");
        data.put("rememberMe", true);
        inputMap.put("data", data);

        HttpPost httpPost = new HttpPost("http://injector:8080/api/rs");
        StringEntity input = new StringEntity(ServiceLocator.getInstance().getMapper().writeValueAsString(inputMap));
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
            Map<String, Object> jsonMap = ServiceLocator.getInstance().getMapper().readValue(json,
                    new TypeReference<HashMap<String, Object>>() {
                    });
            jwt = (String)jsonMap.get("accessToken");
            EntityUtils.consume(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.close();
        }

    }

}
