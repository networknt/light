/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    signup: function(email, username, password, passwordConfirmation) {
        AppDispatcher.dispatch({
            type: ActionTypes.SIGNUP_REQUEST,
            email: email,
            username: username,
            password: password,
            passwordConfirmation: passwordConfirmation
        });
        WebAPIUtils.signup(email, username, password, passwordConfirmation);
    },

    login: function(userIdEmail, password, rememberMe) {
        AppDispatcher.dispatch({
            type: ActionTypes.LOGIN_REQUEST,
            userIdEmail: userIdEmail,
            password: password,
            rememberMe: rememberMe
        });
        WebAPIUtils.login(userIdEmail, password, rememberMe);
    },

    logout: function() {
        AppDispatcher.dispatch({
            type: ActionTypes.LOGOUT
        });
    }

};

