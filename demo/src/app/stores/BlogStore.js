var AppDispatcher = require('../dispatcher/AppDispatcher');
var EventEmitter = require('events').EventEmitter;
var AppConstants = require('../constants/AppConstants');
var ActionTypes = AppConstants.ActionTypes;
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var CHANGE_EVENT = 'change';

var _blogPosts = [];
var _ancestors = [];
var _total = 0;
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


    getBlogPosts: function() {
        return _blogPosts;
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
            _blogPosts = payload.json.posts;
            _total = payload.json.total;
            _allowPost = payload.json.allowPost
            BlogStore.emitChange();
            break;

    }

    return true;
});

module.exports = BlogStore;