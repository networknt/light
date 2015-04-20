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

package com.networknt.light.rule.post;

import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by steve on 27/11/14.
 */
public abstract class AbstractPostRule extends AbstractRule implements Rule {
    static final Logger logger = LoggerFactory.getLogger(AbstractPostRule.class);

    public abstract boolean execute (Object ...objects) throws Exception;

    protected void upVotePost(Map<String, Object> data) {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            OrientVertex updateUser = (OrientVertex) graph.getVertexByKey("User.userId", data.remove("updateUserId"));
            OrientVertex post = (OrientVertex) graph.getVertexByKey("Post.postId", data.get("postId"));
            if (post != null && updateUser != null) {
                // remove DownVote edge if there is.
                for (Edge edge : updateUser.getEdges(post, Direction.OUT, "DownVote")) {
                    if (edge.getVertex(Direction.IN).equals(post)) graph.removeEdge(edge);
                }
                updateUser.addEdge("UpVote", post);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }

    protected void downVotePost(Map<String, Object> data) {
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            OrientVertex updateUser = (OrientVertex) graph.getVertexByKey("User.userId", data.remove("updateUserId"));
            OrientVertex post = (OrientVertex) graph.getVertexByKey("Post.postId", data.get("postId"));
            if (post != null && updateUser != null) {
                // remove UpVote edge if there is.
                for (Edge edge : updateUser.getEdges(post, Direction.OUT, "UpVote")) {
                    if (edge.getVertex(Direction.IN).equals(post)) graph.removeEdge(edge);
                }
                updateUser.addEdge("DownVote", post);
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
        } finally {
            graph.shutdown();
        }
    }
}
