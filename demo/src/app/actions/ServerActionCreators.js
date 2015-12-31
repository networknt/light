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

    receiveMenu: function(json, error) {
        //console.log('ServerActionCreators receiveMenu', json);
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_MENU,
            json: json,
            error: error
        });
    },

    getBlogTreeResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_BLOG_TREE_RESPONSE,
            json: json,
            error: error
        });
    },

    getBlogPostResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_BLOG_POST_RESPONSE,
            json: json,
            error: error
        });
    },

    getBlogResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_BLOG_RESPONSE,
            json: json,
            error: error
        });
    },

    // TODO create a toaster component to display the result.
    delBlogResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.DEL_BLOG_RESPONSE,
            json: json,
            error: error
        });
    },

    receiveCreatedStory: function(json, errors) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_CREATED_STORY,
            json: json,
            errors: errors
        });
    },

    getCatalogTreeResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_CATALOG_TREE_RESPONSE,
            json: json,
            error: error
        });
    },

    getCatalogProductResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_CATALOG_PRODUCT_RESPONSE,
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

    updateShippingAddressResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.UPDATE_SHIPPING_ADDRESS_RESPONSE,
            json: json,
            error: error
        });
    },

    confirmShippingAddressResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.CONFIRM_SHIPPING_ADDRESS_RESPONSE,
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
        //console.log('ServerActionCreator receiveAddOrder is callled', json);
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_ADD_ORDER,
            json: json,
            error: error
        });
    },

    receiveAddTransaction: function(json, error) {
        //console.log('ServerActionCreator receiveAddTransaction is callled', json);
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_ADD_TRANSACTION,
            json: json,
            error: error
        });
    },

    submitFormRes: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.SUBMIT_FORM_RESPONSE,
            json: json,
            error: error
        });
    }
};

