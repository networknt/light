var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _hosts = [];

var HostAdminStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getHosts: function() {
        return _hosts;
    }

});

HostAdminStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.GET_ALL_HOST_RESPONSE:
            _hosts = payload.json;
            HostAdminStore.emitChange();
            break;

        case ActionTypes.DEL_HOST_RESPONSE:
            // remove the deleted one from the list.
            let hostId = payload.hostId;
            console.log('HostAdminStore.DEL_HOST_RESPONSE', payload);
            _hosts.splice(_.findIndex(_hosts, function(host) {
                return host.hostId === hostId;
            }), 1);
            HostAdminStore.emitChange();
            break;

    }

    return true;
});

module.exports = HostAdminStore;
