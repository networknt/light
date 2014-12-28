package com.networknt.light.rule.blog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.injector.main.feed.FeedRule;
import com.networknt.light.util.ServiceLocator;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by husteve on 10/8/2014.
 */
public class GetBlogRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return getBfn("blog", objects);
    }

    /*
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> blogMap = ServiceLocator.getInstance().getMemoryImage("blogMap");
        // determine if the current user can post.
        boolean allowPost = false;
        String host = (String)data.get("host");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        if(payload != null) {
            Map<String,Object> user = (Map<String, Object>)payload.get("user");
            List roles = (List)user.get("roles");
            if(roles.contains("owner")) {
                allowPost = true;
            } else if(roles.contains("admin") || roles.contains("blowAdmin") || roles.contains("blogUser")){
                if(host.equals(user.get("host"))) {
                    allowPost = true;
                }
            }
        }

        List<String> newList = (List<String>)blogMap.get(host + "newList");
        if(newList == null) {
            refreshCache(host);
            newList = (List)blogMap.get(host + "newList");
        }
        // get the page from cache.
        long total = newList.size();
        Integer pageSize = (Integer)data.get("pageSize");
        Integer pageNo = (Integer)data.get("pageNo");
        if(pageSize == null) {
            inputMap.put("result", "pageSize is required");
            inputMap.put("responseCode", 400);
        }
        if(pageNo == null) {
            inputMap.put("result", "pageNo is required");
            inputMap.put("responseCode", 400);
        }
        if(total > 0) {
            List<Map<String, Object>> blogs = new ArrayList<Map<String, Object>>();

            for(int i = pageSize*(pageNo - 1); i < Math.min(pageSize*pageNo, newList.size()); i++) {
                String rid = newList.get(i);
                String json = getJsonByRid(rid);
                Map<String, Object> blog = mapper.readValue(json,
                        new TypeReference<HashMap<String, Object>>() {
                        });
                // convert tags value from map to string.
                Map tags = (Map)blog.get("tags");
                if(tags != null && tags.size() > 0) {
                    String s = String.join(",", tags.keySet());
                    blog.put("tags", s);
                }

                blogs.add(blog);
            }
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", total);
            result.put("blogs", blogs);
            result.put("allowPost", allowPost);
            Set hosts = ServiceLocator.getInstance().getHostMap().keySet();
            result.put("hosts", hosts);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        } else {
            // there is no blog available. but still need to return allowPost and hosts
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", 0);
            result.put("allowPost", allowPost);
            Set hosts = ServiceLocator.getInstance().getHostMap().keySet();
            result.put("hosts", hosts);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        }
    }
    */

}
