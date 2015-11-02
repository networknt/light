var keyMirror = require('react/lib/keyMirror');

module.exports = {
    ActionTypes: keyMirror({
        BLOGS_RESPONSE: null,
        BLOG_POSTS_RESPONSE: null,
        BLOG_POST_RESPONSE: null
    }),
    ChangeEvents: {
        BLOG_CHANGE_EVENT: 'blogChange'
    }
};