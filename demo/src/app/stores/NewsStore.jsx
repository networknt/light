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

var NewsStore = _.extend({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },


    getNewsPosts: function() {
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
    console.log("NewsStore payload:", payload);

    var type = payload.type;
    switch(type) {
        /*
         case ActionTypes.GET_NEWS_TREE_RESPONSE:
         WebAPIUtils.getNewsPost(payload.json[0]['@rid']);
         //ProductStore.emitChange();
         break;
         */
        case ActionTypes.GET_NEWS_POST_RESPONSE:
            _blogPosts = payload.json.posts;
            _total = payload.json.total;
            _allowPost = payload.json.allowPost
            NewsStore.emitChange();
            break;

    }

    return true;
});

module.exports = NewsStore;
