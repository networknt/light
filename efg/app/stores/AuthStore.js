/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');
var jwtDecode = require('jwt-decode');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

// Load an access token from the auth storage, you might want to implement
// a 'remember me' using localStorage
var _isLoggedIn = false;
var _currentUser = { userId: '', roles: ['anonymous']};
var _rememberMe = false;
var _accessToken = '';
var _refreshToken = '';
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
        return _isLoggedIn;
    },

    getAccessToken: function() {
        return _accessToken;
    },

    getRefreshToken: function() {
        return _refreshToken;
    },

    getRoles: function () {
        return _currentUser.roles;
    },

    getUserId: function() {
        return _currentUser.userId;
    },

    getRememberMe: function() {
        return _rememberMe
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
        case ActionTypes.LOGIN_REQUEST:
            // This is an indicator if refresh token will be used to get another access token after access token is expired.
            _rememberMe = payload.rememberMe;
            break;

        case ActionTypes.LOGIN_RESPONSE:
            if (payload.json) {
                // Successfully logged in and get access token back. If remember me is checked, then a refresh token is returned as well.
                _isLoggedIn = true;
                // Parse the Json Token and get uesr object which contains userId and roles.
                //_currentUser = JSON.parse(base64.base64Decode(payload.json.accessToken.split('.')[1])).user;
                // Save authorizationData object into local storage so it can last longer than the browser session. local storage will
                // fall back to Cookie if HTML5 is not supported by the browser.
                _accessToken = payload.json.accessToken;
                localStorage.setItem('accessToken', _accessToken);
                console.log('_accessToken', _accessToken);
                var jwt = jwtDecode(_accessToken);
                console.log('jwt', jwt);
                _currentUser = jwt.user;

                if(_rememberMe) {
                    _refreshToken = payload.json.refreshToken;
                    localStorage.setItem('refreshToken', _refreshToken);
                }
                // Redirect to the attempted url if the login page was redirected upon 401 and 403 error.
                // httpBuffer.redirectToAttemptedUrl();
            }
            if (payload.error) {
                _errors = payload.error;
            }
            AuthStore.emitChange();
            break;
        case ActionTypes.REFRESH:
            console.log('refreshed access token is saved');
            _accessToken = payload.accessToken;
            localStorage.setItem('accessToken', _accessToken);
            break;

        case ActionTypes.LOGOUT:
            console.log('logout  action type in AuthStore');
            _isLoggedIn = false;
            _accessToken = null;
            localStorage.removeItem('accessToken');
            if(_rememberMe) {
                _rememberMe = false;
                _refreshToken = null;
                localStorage.removeItem('refreshToken');
            }
            AuthStore.emitChange();
            break;
        case ActionTypes.INIT:
            console.log('init is called');
            var accessToken = localStorage.getItem('accessToken');
            if(accessToken) {
                _isLoggedIn = true;
                _accessToken = accessToken;
                var jwt = jwtDecode(_accessToken);
                console.log('jwt', jwt);
                _currentUser = jwt.user;
            }
            var refreshToken = localStorage.getItem('refreshToken');
            if(refreshToken) {
                _rememberMe = true;
                _refreshToken = refreshToken;
            }
            AuthStore.emitChange();
            break;

        default:
    }

    return true;
});

module.exports = AuthStore;

