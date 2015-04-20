package com.networknt.light.rule.blog;

import com.networknt.light.rule.AbstractBfnRule;
import com.networknt.light.rule.Rule;

/**
 * Created by steve on 3/6/2015.
 *
 * Blog user can only delete his or her posts before anyone makes comment. However,
 * owner, admin and blogAdmin can delete post and all the comments relate to the
 * post all together. This should not be done often only at extreme situation.
 *
 * Due to the implementation of orientdb delete graph scheduled for 2.2
 * https://github.com/orientechnologies/orientdb/issues/1108
 * We will be asking all the comments be deleted before deleting the post for now.
 *
 * TODO fix it after orientdb 2.2
 *
 * AccessLevel R [owner, admin, blogAdmin, blogUser]
 */
public class DelPostRule extends AbstractBfnRule implements Rule {
    public boolean execute (Object ...objects) throws Exception {
        return delPost("blog", objects);
    }

}
