package com.networknt.light.rule.page;

import com.networknt.light.rule.Rule;
import com.networknt.light.rule.form.AbstractFormRule;

import java.util.Map;

/**
 * Created by husteve on 10/24/2014.
 */
public class ImpPageEvRule extends AbstractPageRule implements Rule {
    public boolean execute(Object... objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        String id = (String) data.get("id");
        String json = getPageById(id);
        if(json != null) {
            updPage(data);
        } else {
            addPage(data);
        }
        return true;
    }
}
