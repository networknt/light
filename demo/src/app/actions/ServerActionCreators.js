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

    getMenuResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_MENU_RESPONSE,
            json: json
        });
    },

    getRoleResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_ROLE_RESPONSE,
            json: json
        });
    },

    delRoleResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.DEL_ROLE_RESPONSE,
            json: json
        });
    },

    getAllAccessResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_ALL_ACCESS_RESPONSE,
            json: json
        });
    },

    getBlogTreeResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_BLOG_TREE_RESPONSE,
            json: json,
            error: error
        });
    },

    getNewsTreeResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_NEWS_TREE_RESPONSE,
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

    getRecentBlogPostResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_RECENT_BLOG_POST_RESPONSE,
            json: json,
            error: error
        });
    },

    getRecentNewsPostResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_RECENT_NEWS_POST_RESPONSE,
            json: json,
            error: error
        });
    },

    getNewsPostResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_NEWS_POST_RESPONSE,
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

    getNewsResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_NEWS_RESPONSE,
            json: json,
            error: error
        });
    },

    getCatalogResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_CATALOG_RESPONSE,
            json: json
        });
    },

    // TODO create a toaster component to display the result.
    delPostResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.DEL_POST_RESPONSE,
            json: json,
            error: error
        });
    },

    // TODO create a toaster component to display the result.
    addPostResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.ADD_POST_RESPONSE,
            json: json,
            error: error
        });
    },

    // TODO create a toaster component to display the result.
    updPostResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.UPD_POST_RESPONSE,
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

    delProductResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.DEL_PRODUCT_RESPONSE,
            json: json,
            error: error
        });
    },

    addProductResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.ADD_PRODUCT_RESPONSE,
            json: json,
            error: error
        });
    },

    updProductResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.UPD_PRODUCT_RESPONSE,
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

    getPageResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_PAGE_RESPONSE,
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

    submitFormResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.SUBMIT_FORM_RESPONSE,
            json: json,
            error: error
        });
    },

    execQueryCmdResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.EXEC_QUERY_CMD_RESPONSE,
            json: json,
            error: error
        });
    },

    downloadEventResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.DOWNLOAD_EVENT_RESPONSE,
            json: json,
            error: error
        });
    }

};

