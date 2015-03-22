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

import com.networknt.light.model.CacheObject;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.util.Map;

/**
 * Created by steve on 10/24/2014.
 *
 * AccessLevel A
 *
 */
public class GetPageRule extends AbstractPageRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        HttpServerExchange exchange = (HttpServerExchange)inputMap.get("exchange");
        String pageId = (String)data.get("pageId");
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        CacheObject co = null;
        try {
            co = getPageById(graph, pageId);
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        if(co != null) {
            if(!matchEtag(inputMap, co)) {
                inputMap.put("result", co.getData());
            }
            return true;
        } else {
            inputMap.put("responseCode", 404);
            inputMap.put("result", "Page with id " + pageId + " cannot be found.");
            return false;
        }
    }

}
