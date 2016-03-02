var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _configs = {};

var ConfigStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getConfig: function(id) {
        return _configs[id];
    }
});

ConfigStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.GET_CONFIG_RESPONSE:
            console.log('ConfigStore payload.json', payload.json);
            _configs[payload.json.configId] = payload.json.properties;
            ConfigStore.emitChange();
            break;
    }
    return true;
});

module.exports = ConfigStore;
