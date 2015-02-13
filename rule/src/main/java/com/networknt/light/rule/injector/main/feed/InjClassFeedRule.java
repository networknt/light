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

package com.networknt.light.rule.injector.main.feed;

import com.networknt.light.rule.Rule;

import java.util.Map;

/**
 * Created by husteve on 9/5/2014.
 */
public class InjClassFeedRule extends ClassFeedRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        // make sure payload is not null. If you have payload that means the token is valid and not expired.
        String error = null;
        if(payload == null) {
            error ="Login is required";
            inputMap.put("responseCode",401);
        } else {
            // now we need to construct the bean and send to the queue.
            /*
            IDataFeed dataFeed = new ClassDataFeed();
            send(dataFeed, data);
            */
            // data has been enriched with requestId, dataFeedType and LoanNumber etc.
            // now we need to remove all the orientdb attributes in order to save again.
            data.remove("@type");
            data.remove("@rid");
            data.remove("@version");
            data.remove("@class");
            data.remove("@fieldTypes");
            inputMap.put("result", data.get("requestId"));
        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }
}
