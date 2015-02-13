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

package com.networknt.light.rule.category;

import com.networknt.light.rule.Rule;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.List;
import java.util.Map;

/**
 * Created by husteve on 10/14/2014.
 */
public class AddChildRule extends AbstractCategoryRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");

        String rid = (String)data.get("@rid");
        String childRid = (String) data.get("childRid");
        String host = (String)data.get("host");
        int inputVersion = (int)data.get("@version");

        String error = null;
        if(payload == null) {
            error ="Login is required";
            inputMap.put("responseCode", 401);
        } else {
            // TODO check if the user has permission to do so.

            ODocument category = getCategoryByRid(rid);
            if(category == null) {
                error = "Category does not exist in host" + host;
                inputMap.put("responseCode", 404);
            } else {
                // check if the version is the same.
                int storedVersion = (int)category.field("@version");
                if(inputVersion != storedVersion) {
                    error = "Updating version " + inputVersion + " doesn't match stored version " + storedVersion;
                    inputMap.put("responseCode", 400);
                } else {
                    // check if child id is already in children
                    List children = category.field("children");
                    if(children.contains(new ORecordId(childRid))) {
                        error ="The child is already in the list";
                        inputMap.put("responseCode", 400);
                    }
                }
            }
        }
        if(error != null) {
            inputMap.put("error", error);
            return false;
        } else {
            return true;
        }
    }
}
