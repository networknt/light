var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _category = [];
var _errors = [];


var ForumCategoryStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getCategory: function() {
        return _category;
    },

    getErrors: function() {
        return _errors;
    }

});

ForumCategoryStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.GET_FORUM_TREE_RESPONSE:
            _category = payload.json;
            ForumCategoryStore.emitChange();
            break;
    }

    return true;
});

module.exports = ForumCategoryStore;
