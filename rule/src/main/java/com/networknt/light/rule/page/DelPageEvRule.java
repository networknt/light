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

package com.networknt.light.rule.page;

import com.hazelcast.core.ITopic;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;

import java.util.Map;

/**
 * Created by husteve on 10/24/2014.
 */
public class DelPageEvRule extends AbstractPageRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> eventMap = (Map<String, Object>) objects[0];
        //ITopic topic = ServiceLocator.getInstance().getHzInstance().getTopic("page");
        //topic.publish(eventMap);
        Map<String, Object> data = (Map<String, Object>) eventMap.get("data");
        String pageId = (String)data.get("pageId");
        delPage(pageId);
        return true;
    }
}
