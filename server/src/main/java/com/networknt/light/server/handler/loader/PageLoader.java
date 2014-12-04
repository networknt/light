package com.networknt.light.server.handler.loader;

import com.networknt.light.server.LightServer;
import com.networknt.light.util.ServiceLocator;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
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
    static public String pageFolder = "page";

    public static void loadPage() throws Exception {
        File folder = getFileFromResourceFolder(pageFolder);
        if(folder != null) {
            LightServer.start();
            httpclient = HttpClients.createDefault();
            // login as owner here
            login();
            File[] listOfFiles = folder.listFiles();
            for (int i = 0; i < listOfFiles.length; i++) {
                loadPageFile(listOfFiles[i]);
            }
            httpclient.close();

            LightServer.stop();
        }
    }
    private static void loadPageFile(File file) {
        Scanner scan = null;
        try {
            scan = new Scanner(file, Loader.encoding);
            // the content is only the data portion. convert to map
            String content = scan.useDelimiter("\\Z").next();
            Map<String, Object> inputMap = new HashMap<String, Object>();
            inputMap.put("category", "page");
            inputMap.put("name", "impPage");
            inputMap.put("readOnly", false);
            Map<String, Object> data = new HashMap<String, Object>();
            String id = file.getName();
            id = id.substring(0, id.lastIndexOf('.'));
            data.put("id", id);
            data.put("content", content);
            inputMap.put("data", data);
            HttpPost httpPost = new HttpPost("http://injector:8080/api/rs");
            httpPost.addHeader("Authorization", "Bearer " + jwt);
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
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (scan != null) scan.close();
        }
    }
}
