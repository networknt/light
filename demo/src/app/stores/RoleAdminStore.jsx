/**
 * Created by steve on 7/31/2015.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _roles = [];
var _hosts = [];

var RoleAdminStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getRoles: function() {
        return _roles;
    },

    getHosts: function() {
        return _hosts;
    }

});

RoleAdminStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.GET_ROLE_RESPONSE:
            _roles = payload.json.roles;
            _hosts = payload.json.hosts;
            RoleAdminStore.emitChange();
            break;
        case ActionTypes.DEL_ROLE_RESPONSE:
            // remove the deleted one from the list.
            let rid = payload.rid;
            console.log('RoleAdminStore.DEL_ROLE_RESPONSE', rid, _roles);
            _roles.splice(_.findIndex(_roles, function(role) {
                return role['@rid'] === rid;
            }), 1);
            RoleAdminStore.emitChange();
            break;
    }

    return true;
});

module.exports = RoleAdminStore;
