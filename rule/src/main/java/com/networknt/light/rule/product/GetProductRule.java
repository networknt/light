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

package com.networknt.light.rule.product;

import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;

import java.util.Map;

/**
 * Created by husteve on 10/14/2014.
 * if there is category passed in get the list of products in that category and subcategories.
 * sort by order by
 *
 */
public class GetProductRule extends AbstractProductRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) objects[0];
        Map<String, Object> data = (Map<String, Object>) inputMap.get("data");
        Map<String, Object> productMap = ServiceLocator.getInstance().getMemoryImage("productMap");
        String host = (String)data.get("host");
        if(host == null) {
            inputMap.put("result", "host is required");
            inputMap.put("responseCode", 400);
            return false;
        }
        Integer pageSize = (Integer)data.get("pageSize");
        if(pageSize == null) {
            inputMap.put("result", "pageSize is required");
            inputMap.put("responseCode", 400);
            return false;
        }
        Integer pageNo = (Integer)data.get("pageNo");
        if(pageNo == null) {
            inputMap.put("result", "pageNo is required");
            inputMap.put("responseCode", 400);
            return false;
        }
        String json = searchProduct(data);
        if(json != null) {
            inputMap.put("result", json);
            return true;
        } else {
            inputMap.put("result", "No product can be found.");
            inputMap.put("responseCode", 404);
            return false;
        }
    }
}
