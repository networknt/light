package com.networknt.light.rule.post;

import com.fasterxml.jackson.core.type.TypeReference;
import com.networknt.light.rule.Rule;
import com.networknt.light.rule.blog.AbstractBlogRule;
import com.networknt.light.rule.injector.main.feed.FeedRule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.ServiceLocator;

import java.util.*;

/**
 * Created by steve on 27/11/14.
 */
public class GetPostRule extends AbstractPostRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        long total = DbService.getCount("Post", data);
        if(total > 0) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", total);
            String posts = DbService.getData("Post", data);
            List<Map<String, Object>> jsonList = mapper.readValue(posts,
                    new TypeReference<List<HashMap<String, Object>>>() {
                    });
            result.put("posts", jsonList);
            inputMap.put("result", mapper.writeValueAsString(result));
            return true;
        } else {
            inputMap.put("error", "No post can be found.");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
