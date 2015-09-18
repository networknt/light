/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _blogs = [];
var _errors = [];
var _blog = { title: "", body: "", user: { username: "" } };

var BlogStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getAllBlogs: function() {
        return _blogs;
    },

    getBlog: function() {
        return _blog;
    },

    getErrors: function() {
        return _errors;
    }

});

BlogStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {

        case ActionTypes.RECEIVE_BLOGS:
            _blogs = payload.json;
            console.log('blogStore _blogs = ', _blogs);
            BlogStore.emitChange();
            break;

        case ActionTypes.RECEIVE_CREATED_STORY:
            if (action.json) {
                _blogs.unshift(action.json.story);
                _errors = [];
            }
            if (action.errors) {
                _errors = action.errors;
            }
            BlogStore.emitChange();
            break;

        case ActionTypes.RECEIVE_STORY:
            if (action.json) {
                _blog = action.json.blog;
                _errors = [];
            }
            if (action.errors) {
                _errors = action.errors;
            }
            BlogStore.emitChange();
            break;
    }

    return true;
});

module.exports = BlogStore;
