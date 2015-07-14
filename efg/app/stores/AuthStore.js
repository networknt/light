/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

// Load an access token from the auth storage, you might want to implement
// a 'remember me' using localStorage
var _accessToken = sessionStorage.getItem('accessToken');
var _email = sessionStorage.getItem('email');
var _errors = [];

var AuthStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    isLoggedIn: function() {
        return _accessToken ? true : false;
    },

    getAccessToken: function() {
        return _accessToken;
    },

    getEmail: function() {
        return _email;
    },

    getErrors: function() {
        return _errors;
    }

});

AuthStore.dispatchToken = AppDispatcher.register(function(payload) {
    console.log('payload', payload);
    var type = payload.type;
    console.log('type', type);
    switch(type) {
        case ActionTypes.LOGIN_RESPONSE:
            if (action.json && action.json.access_token) {
                _accessToken = action.json.access_token;
                _email = action.json.email;
                // Token will always live in the session, so that the API can grab it with no hassle
                sessionStorage.setItem('accessToken', _accessToken);
                sessionStorage.setItem('email', _email);
            }
            if (action.errors) {
                _errors = action.errors;
            }
            AuthStore.emitChange();
            break;

        case ActionTypes.LOGOUT:
            _accessToken = null;
            _email = null;
            sessionStorage.removeItem('accessToken');
            sessionStorage.removeItem('email');
            AuthStore.emitChange();
            break;

        default:
    }

    return true;
});

module.exports = AuthStore;

