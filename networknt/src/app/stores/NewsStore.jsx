var AppDispatcher = require('../dispatcher/AppDispatcher');
var EventEmitter = require('events').EventEmitter;
var AppConstants = require('../constants/AppConstants');
var ActionTypes = AppConstants.ActionTypes;
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var CHANGE_EVENT = 'change';

var _posts = [];
var _ancestors = [];
var _total = 0;
var _allowUpdate = false;
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


    getPosts: function() {
        return _posts;
    },

    getAncestors: function() {
        return _ancestors;
    },

    getAllowUpdate: function() {
        return _allowUpdate;
    },

    getTotal: function() {
        return _total;
    },

    getPost: function() {
        return _post;
    }

});

AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.GET_NEWS_POST_RESPONSE:
            _total = payload.json.total;
            _allowUpdate = payload.json.allowUpdate;
            if(_total == 0) {
                _posts = [];
            } else {
                _posts = payload.json.entities;
            }
            NewsStore.emitChange();
            break;
        case ActionTypes.GET_RECENT_NEWS_POST_RESPONSE:
            _total = payload.json.total;
            if(_total == 0) {
                _posts = [];
            } else {
                _posts = payload.json.entities;
            }
            NewsStore.emitChange();
            break;

    }

    return true;
});

module.exports = NewsStore;
