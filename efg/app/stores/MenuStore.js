/**
 * Created by steve on 7/31/2015.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _menu = [];
var _errors = [];

var MenuStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getMenu: function() {
        return _menu;
    },

    getErrors: function() {
        return _errors;
    }

});

MenuStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.RECEIVE_MENU:
            console.log('MenuStore RECEIVE_MENU', payload.json);
            _menu = payload.json;
            console.log('menuStore _menu = ', _menu);
            MenuStore.emitChange();
            break;
    }

    return true;
});

module.exports = MenuStore;
