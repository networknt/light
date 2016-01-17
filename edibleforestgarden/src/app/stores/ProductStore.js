var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _result;
var _errors;

var ProductStore = assign({}, EventEmitter.prototype, {

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

ProductStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.ADD_PRODUCT_RESPONSE:
        case ActionTypes.UPD_PRODUCT_RESPONSE:
        case ActionTypes.DEL_PRODUCT_RESPONSE:
            _result = payload.json;
            _errors = payload.error;
            ProductStore.emitChange();
            break;
    }

    return true;
});

module.exports = ProductStore;
