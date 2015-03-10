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
package com.networknt.light.rule.dependency;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by steve on 3/9/2015.
 */
public abstract class AbstractDependencyRule extends AbstractRule implements Rule {
    static final Logger logger = LoggerFactory.getLogger(AbstractDependencyRule.class);

    public abstract boolean execute (Object ...objects) throws Exception;

    protected void addDependency(Map<String, Object> data) throws Exception {
        String json = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex sourceRule = graph.getVertexByKey("Rule.ruleClass", data.get("sourceRuleClass"));
            Vertex destRule = graph.getVertexByKey("Rule.ruleClass", data.get("destRuleClass"));
            Edge edge = sourceRule.addEdge("Depend", destRule);
            edge.setProperty("content", data.get("content"));
            graph.commit();
            //json = edge.getRecord().toJSON();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
        Map<String, Object> pageMap = ServiceLocator.getInstance().getMemoryImage("pageMap");
        ConcurrentMap<Object, Object> cache = (ConcurrentMap<Object, Object>)pageMap.get("cache");
        if(cache == null) {
            cache = new ConcurrentLinkedHashMap.Builder<Object, Object>()
                    .maximumWeightedCapacity(1000)
                    .build();
            pageMap.put("cache", cache);
        }
        cache.put(data.get("pageId"), json);
    }

}
