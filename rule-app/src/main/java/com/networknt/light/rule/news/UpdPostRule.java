package com.networknt.light.rule.news;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;

/**
 * Created by steve on 3/6/2015.
 * Update post in a forum
 *
 * AccessLevel R [owner, admin, newsAdmin, user]
 *
 * User can only update his or her post and there will be an indicate that
 * the post is updated. Maybe just the update date?
 *
 * for now to make it simple, user cannot update the post.
 *
 */public class UpdPostRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return updPost("news", objects);
    }
}
