/**
 * Created by steve on 19/12/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _clientToken;
var _errors = [];

var PaymentStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getClientToken: function() {
        //console.log('clientToken = ', _clientToken);
        return _clientToken;
    },

    getErrors: function() {
        return _errors;
    }

});

PaymentStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.RECEIVE_CLIENT_TOKEN:
            //console.log('PaymentStore RECEIVE_CLIENT_TOKEN', payload.json);
            _clientToken = payload.json.clientToken;
            //console.log('_clientToken = ', _clientToken);
            PaymentStore.emitChange();
            break;
    }
    return true;
});

module.exports = PaymentStore;
