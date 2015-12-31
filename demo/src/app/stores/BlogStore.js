var AppDispatcher = require('../dispatcher/AppDispatcher');
var EventEmitter = require('events').EventEmitter;
var AppConstants = require('../constants/AppConstants');
var ActionTypes = AppConstants.ActionTypes;
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var CHANGE_EVENT = 'change';

var _blogPosts = [];
var _blogs = [];
var _ancestors = [];
var _allowUpdate = false;

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

    getBlogs: function () {
        return _blogs;
    },

    getAncestors: function() {
        return _ancestors;
    },

    getAllowUpdate: function() {
        return _allowUpdate;
    }

});

AppDispatcher.register(function(payload) {
    console.log("BlogStore payload:", payload);

    var type = payload.type;
    switch(type) {
        case ActionTypes.GET_BLOG_TREE_RESPONSE:
            WebAPIUtils.getBlogPost(payload.json[0]['@rid']);
            //ProductStore.emitChange();
            break;

        case ActionTypes.GET_BLOG_RESPONSE:
            _blogs = payload.json;
            BlogStore.emitChange();
            break;

    }

    return true;
});

module.exports = BlogStore;