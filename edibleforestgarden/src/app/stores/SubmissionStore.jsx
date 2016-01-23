/**
 * This is generic stoere for form submissions. For the API repsonse, it there is no specific
 * store listening to the response, it goes here.
 *
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _result;
var _errors;

var SubmissionStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getResult: function() {
        return _result;
    },

    getErrors: function() {
        return _errors;
    }

});

SubmissionStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.SUBMIT_FORM_RESPONSE:
            _result = payload.json;
            _errors = payload.error;
            SubmissionStore.emitChange();
            break;
    }

    return true;
});

module.exports = SubmissionStore;
