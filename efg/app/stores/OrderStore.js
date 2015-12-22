var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _total;
var _orderId;
var _order;
var _errors = [];

var OrderStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getTotal: function() {
        return _total;
    },

    getOrderId: function() {
        return _orderId;
    },

    getOrder: function() {
        return _order;
    },

    getErrors: function() {
        return _errors;
    }

});

OrderStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.RECEIVE_ADD_ORDER:
            console.log('OrderStore RECEIVE_ADD_ORDER total = ', payload.json.total);
            _total = payload.json.total;
            _orderId = payload.json.orderId;
            OrderStore.emitChange();
            break;
        case ActionTypes.RECEIVE_ADD_TRANSACTION:
            console.log('OrderStore RECEIVE_ADD_TRANSACTION order = ', payload.json)
            _order = payload.json;
            OrderStore.emitChange();
            break;
    }
    return true;
});

module.exports = OrderStore;
