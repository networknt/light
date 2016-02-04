var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';


var _error = null;
var _status = null;

var ErrorStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getError: function() {
        return _error;
    },

    getStatus: function() {
        return _status;
    }
});

ErrorStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.SERVER_ERROR_RESPONSE:
            _error = payload.error;
            _status = payload.status;
            ErrorStore.emitChange();
            break;
    }
    return true;
});

module.exports = ErrorStore;
