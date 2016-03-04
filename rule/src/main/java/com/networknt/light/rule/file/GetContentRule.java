package com.networknt.light.rule.file;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.HashUtil;
import com.networknt.light.util.ServiceLocator;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 27/02/16.
 *
 * AccessLevel R [fileAdmin, admin, owner]
 *
 */
public class GetContentRule extends AbstractFileRule implements Rule {
    public boolean execute(Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        // here we get user's host to decide which domain he/she can access
        Map<String, Object> user = (Map<String, Object>) inputMap.get("user");
        String host = (String) user.get("host");
        if(host == null) {
            host = (String)data.get("host");
        }
        String path = (String) data.get("path");
        String root = getRootPath(host);
        String absPath = getAbsPath(root, path);
        // now save this absPath into the in memory cache and return a uuid key to the client in order to
        // download the file.
        Map<String, Object> fileMap = ServiceLocator.getInstance().getMemoryImage("fileMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)fileMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(100)
                    .build();
            fileMap.put("cache", cache);
        }
        String token = HashUtil.generateUUID();
        cache.put(token, absPath);
        Map<String, Object> result = new HashMap<String,Object>();
        result.put("token", token);
        inputMap.put("result", mapper.writeValueAsString(result));
        return true;
    }
}
