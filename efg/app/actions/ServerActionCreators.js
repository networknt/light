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
        //console.log('ServerActionCreators receiveMenu', json);
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
    },

    receiveCatalog: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_CATALOG,
            json: json,
            error: error
        });
    },

    receiveProducts: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_PRODUCTS,
            json: json,
            error: error
        });
    },

    receiveUser: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_USER,
            json: json,
            error: error
        });
    },

    receiveUpdShippingAddress: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.UPD_SHIPPING_ADDRESS_RESPONSE,
            json: json,
            error: error
        });
    },

    receiveForm: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_FORM,
            json: json,
            error: error
        });
    },

    receiveClientToken: function(json, error) {
        //console.log('ServerActionCreator receiveClientToken is callled');
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_CLIENT_TOKEN,
            json: json,
            error: error
        });
    },

    receiveAddOrder: function(json, error) {
        //console.log('ServerActionCreator receiveAddOrder is callled');
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_ADD_ORDER,
            json: json,
            error: error
        });
    }

};

