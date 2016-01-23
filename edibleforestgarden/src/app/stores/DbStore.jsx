var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _queryResult;
var _errors = [];

var DbStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getQueryResult: function() {
        return _queryResult;
    },

    getErrors: function() {
        return _errors;
    }

});

DbStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.EXEC_QUERY_CMD_RESPONSE:
        case ActionTypes.DOWNLOAD_EVENT_RESPONSE:
            _queryResult = payload.json;
            DbStore.emitChange();
            break;
    }

    return true;
});

module.exports = DbStore;
