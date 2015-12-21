var AppDispatcher = require('../dispatcher/AppDispatcher');
var EventEmitter = require('events').EventEmitter;
var AppConstants = require('../constants/AppConstants');
var BlogConstants = require('../constants/BlogConstants');
var _ = require('underscore');

var _blogs = [];
var _blogPosts = [];
var _post = {};

var BlogStore = _.extend({}, EventEmitter.prototype, {
    getBlogs: function() {
        return _blogs;
    },

    getBlogPosts: function() {
        return _blogPosts;
    },

    getPost: function() {
        return _post;
    },

    emitChange: function() {
        this.emit(BlogConstants.ChangeEvents.BLOG_CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(BlogConstants.ChangeEvents.BLOG_CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(BlogConstants.ChangeEvents.BLOG_CHANGE_EVENT, callback);
    }

});

AppDispatcher.register(function(payload) {
    //console.log("BlogStore payload:", payload);
    if (payload == null) return;
    if (payload.type === BlogConstants.ActionTypes.BLOGS_RESPONSE) {
        //console.log("BlogStore received BLOGS:", payload.json);
        _blogs = payload.json;
        BlogStore.emitChange();
    } else if (payload.type === BlogConstants.ActionTypes.BLOG_POSTS_RESPONSE) {
        //console.log("BlogStore received BLOG_POSTS:", payload.json);
        _blogPosts = payload.json;
        BlogStore.emitChange();
    } else if (payload.type === BlogConstants.ActionTypes.BLOG_POST_RESPONSE) {
        //console.log("BlogStore received BLOG_POST:", payload.json);
        _post = payload.json;
        BlogStore.emitChange();
    }
    return true;
});

module.exports = BlogStore;