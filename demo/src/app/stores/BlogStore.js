var AppDispatcher = require('../dispatcher/AppDispatcher');
var EventEmitter = require('events').EventEmitter;
var AppConstants = require('../constants/AppConstants');
var ActionTypes = AppConstants.ActionTypes;
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var CHANGE_EVENT = 'change';

var _posts = [];
var _recentPosts = [];
var _ancestors = [];
var _total = 0;
var _recentPostTotal = 0;
var _allowPost = false;
var _post = {};

var BlogStore = _.extend({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },


    getPosts: function() {
        return _posts;
    },

    getRecentPosts: function() {
        return _recentPosts;
    },

    getAncestors: function() {
        return _ancestors;
    },

    getAllowPost: function() {
        return _allowPost;
    },

    getTotal: function() {
        return _total;
    },

    getRecentPostTotal: function() {
        return _recentPostTotal;
    },

    getPost: function() {
        return _post;
    }

});

AppDispatcher.register(function(payload) {
    console.log("BlogStore payload:", payload);

    var type = payload.type;
    switch(type) {
        /*
        case ActionTypes.GET_BLOG_TREE_RESPONSE:
            WebAPIUtils.getBlogPost(payload.json[0]['@rid']);
            //ProductStore.emitChange();
            break;
        */
        case ActionTypes.GET_BLOG_POST_RESPONSE:
            _total = payload.json.total;
            _allowPost = payload.json.allowPost
            if(_total == 0) {
                _posts = [];
            } else {
                _posts = payload.json.posts;
            }
            BlogStore.emitChange();
            break;
        case ActionTypes.GET_RECENT_BLOG_POST_RESPONSE:
            _recentPostTotal = payload.json.total;
            if(_recentPostTotal == 0) {
                _recentPosts = [];
            } else {
                _recentPosts = payload.json.posts;
            }
            BlogStore.emitChange();
            break;

    }

    return true;
});

module.exports = BlogStore;