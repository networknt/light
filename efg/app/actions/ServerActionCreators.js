/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    receiveLogin: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.LOGIN_RESPONSE,
            json: json,
            error: error
        });
    },

    receiveBlogs: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_BLOGS,
            json: json,
            error: error
        });
    },

    receiveMenu: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_MENU,
            json: json,
            error: error
        });
    },

    receiveStory: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_STORY,
            json: json
        });
    },

    receiveCreatedStory: function(json, errors) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_CREATED_STORY,
            json: json,
            errors: errors
        });
    }

};

