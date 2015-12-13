var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _forms = {};
var _errors = [];

var FormStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getForm: function(id) {
        return _forms[id];
    },

    getErrors: function() {
        return _errors;
    }

});

FormStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.RECEIVE_FORM:
            console.log('FormStore RECEIVE_FORM', payload.json);
            console.log('FormStore RECEIVE_FORM', payload.json.formId);
            console.log('FormStore RECEIVE_FORM', _forms);
            _forms[payload.json.formId] = payload.json;
            FormStore.emitChange();
            break;
    }

    return true;
});

module.exports = FormStore;
