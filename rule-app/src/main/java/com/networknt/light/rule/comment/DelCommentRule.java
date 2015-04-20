package com.networknt.light.rule.comment;

import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;
import com.networknt.light.util.HashUtil;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by steve on 21/03/15.
 *
 * AccessLevel R [owner, admin, forumAdmin, newsAdmin, blogAdmin, user]
 *
 * user can only delete his/her own comment if there is no other comment link to it.
 *
 * forumAdmin can only delete comments from forum
 * newsAdmin can only delete comments from news
 * blogAdmin can only delete comments from blog
 *
 * for now, only owner and admin will be able to delete comment and if there are
 * comments related, it cannot be deleted.
 *
 * now AccessLevel R [owner, admin]
 *
 *
 */
public class DelCommentRule extends AbstractCommentRule implements Rule {
    static final Logger logger = LoggerFactory.getLogger(AddCommentRule.class);

    public boolean execute (Object ...objects) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>)objects[0];
        Map<String, Object> data = (Map<String, Object>)inputMap.get("data");
        Map<String, Object> payload = (Map<String, Object>) inputMap.get("payload");
        Map<String, Object> user = (Map<String, Object>)payload.get("user");
        String host = (String)data.get("host");
        String rid = (String)data.get("@rid");
        String error = null;
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            // check if the comment exists
            OrientVertex comment = (OrientVertex) DbService.getVertexByRid(graph, rid);
            if(comment == null ) {
                error = "Comment @rid " + rid + " cannot be found";
                inputMap.put("responseCode", 404);
            } else {
                // check if there are edges HasComment
                if(comment.countEdges(Direction.OUT, "HasComment") > 0) {
                    error = "Comment has comment(s), cannot be deleted";
                    inputMap.put("responseCode", 400);
                } else {
                    Map eventMap = getEventMap(inputMap);
                    Map<String, Object> eventData = (Map<String, Object>)eventMap.get("data");
                    inputMap.put("eventMap", eventMap);
                    eventData.put("commentId", comment.getProperty("commentId"));
                }
            }
        } catch (Exception e) {
            logger.error("Exception:", e);
            throw e;
        } finally {
            graph.shutdown();
        }
        if(error != null) {
            inputMap.put("result", error);
            return false;
        } else {
            return true;
        }
    }
}
