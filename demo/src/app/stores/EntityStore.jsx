var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _entity;

var EntityStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getEntity: function() {
        return _entity;
    }

});

EntityStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.GET_POST_RESPONSE:
            _entity = payload.json;
            EntityStore.emitChange();
            break;

        case ActionTypes.GET_PRODUCT_RESPONSE:
            _entity = payload.json;
            EntityStore.emitChange();
            break;
    }
    return true;
});

module.exports = EntityStore;
