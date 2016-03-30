var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _forums = [];
var error = null;


var ForumAdminStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getForums: function() {
        return _forums;
    },

    getErrors: function() {
        return _errors;
    }

});

ForumAdminStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.GET_FORUM_RESPONSE:
            error = payload.error;
            //console.log('error', error);
            if(!error) {
                _forums = payload.json;
            }
            ForumAdminStore.emitChange();
            break;
    }

    return true;
});

module.exports = ForumAdminStore;
