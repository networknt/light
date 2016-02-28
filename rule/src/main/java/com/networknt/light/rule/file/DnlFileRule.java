package com.networknt.light.rule.file;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.HashUtil;
import com.networknt.light.util.ServiceLocator;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 2/26/2016.
 *
 * This rule can be access by anyone but an uuid must be passed in which created
 * by the previous command getContent.
 *
 * AccessLevel: A
 *
 */
public class DnlFileRule extends AbstractFileRule implements Rule {
    public boolean execute(Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        String token = (String) data.get("token");
        String path = null;
        Map<String, Object> fileMap = ServiceLocator.getInstance().getMemoryImage("fileMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)fileMap.get("cache");
        if(cache != null) {
            path = (String)cache.get(token);
        }
        if(path == null) {
            inputMap.put("result", "Token is expired.");
            inputMap.put("responseCode", 400);
            return false;
        } else {
            HttpServerExchange exchange = (HttpServerExchange)inputMap.get("exchange");
            File file = new File(path);
            String name = file.getName();
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/x-download");
            exchange.getResponseHeaders().put(new HttpString("Content-disposition"), "attachment; filename=" + name);
            writeToOutputStream(file, exchange.getOutputStream());
            return true;
        }
    }
}
