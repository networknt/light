/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    signup: function(email, userId, password, passwordConfirm, firstName, lastName) {
        WebAPIUtils.signup(email, userId, password, passwordConfirm, firstName, lastName);
    },

    login: function(userIdEmail, password, rememberMe) {
        AppDispatcher.dispatch({
            type: ActionTypes.SIGNIN_USER_REQUEST,
            userIdEmail: userIdEmail,
            password: password,
            rememberMe: rememberMe
        });
        WebAPIUtils.login(userIdEmail, password, rememberMe);
    },

    init: function() {
        //console.log('the inti action is called', AppDispatcher);
        AppDispatcher.dispatch({
            type: ActionTypes.INIT
        });
    },

    refresh: function(accessToken) {
        //console.log('refresh in AuthActionCreators is called.');
        AppDispatcher.dispatch({
            type: ActionTypes.REFRESH,
            accessToken: accessToken
        });
    },

    logout: function() {
        AppDispatcher.dispatch({
            type: ActionTypes.LOGOUT
        });
    }

};

