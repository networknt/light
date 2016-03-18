/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    signInUserResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.SIGNIN_USER_RESPONSE,
            json: json
        });
    },

    googleLoginResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GOOGLE_LOGIN_RESPONSE,
            json: json
        });
    },

    facebookLoginResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.FACEBOOK_LOGIN_RESPONSE,
            json: json
        });
    },

    signUpUserResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.SIGNUP_USER_RESPONSE,
            json: json
        });
    },

    getMenuResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_MENU_RESPONSE,
            json: json
        });
    },

    getAllMenuResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_ALL_MENU_RESPONSE,
            json: json
        });
    },

    getRoleResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_ROLE_RESPONSE,
            json: json
        });
    },

    delRoleResponse: function(rid) {
        AppDispatcher.dispatch({
            type: ActionTypes.DEL_ROLE_RESPONSE,
            rid: rid
        });
    },

    getPostResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_POST_RESPONSE,
            json: json
        });
    },

    getProductResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_PRODUCT_RESPONSE,
            json: json
        });
    },

    getAllAccessResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_ALL_ACCESS_RESPONSE,
            json: json
        });
    },

    getAllUserResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_ALL_USER_RESPONSE,
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

    getForumTreeResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_FORUM_TREE_RESPONSE,
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

    getNewsPostResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_NEWS_POST_RESPONSE,
            json: json,
            error: error
        });
    },

    getForumPostResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_FORUM_POST_RESPONSE,
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

    getRecentForumPostResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_RECENT_FORUM_POST_RESPONSE,
            json: json,
            error: error
        });
    },

    getTagEntityResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_TAG_ENTITY_RESPONSE,
            json: json
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

    getForumResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_FORUM_RESPONSE,
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

    getUserResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_USER_RESPONSE,
            json: json
        });
    },

    updateShippingAddressResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.UPDATE_SHIPPING_ADDRESS_RESPONSE,
            json: json
        });
    },

    confirmShippingAddressResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.CONFIRM_SHIPPING_ADDRESS_RESPONSE,
            json: json
        });
    },

    updateBillingAddressResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.UPDATE_BILLING_ADDRESS_RESPONSE,
            json: json
        });
    },

    confirmBillingAddressResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.CONFIRM_BILLING_ADDRESS_RESPONSE,
            json: json
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

    addSubscriptionResponse: function(json) {
        console.log('ServerActionCreator addSubscriptionResponse is callled', json);
        AppDispatcher.dispatch({
            type: ActionTypes.ADD_SUBSCRIPTION_RESPONSE,
            json: json
        });
    },

    submitFormResponse: function(json, error) {
        AppDispatcher.dispatch({
            type: ActionTypes.SUBMIT_FORM_RESPONSE,
            json: json,
            error: error
        });
    },

    execRuleCmdResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.EXEC_RULE_CMD_RESPONSE,
            json: json
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
    },

    getAllFormResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_ALL_FORM_RESPONSE,
            json: json
        });
    },

    getAllPageResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_ALL_PAGE_RESPONSE,
            json: json
        });
    },

    getRuleResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_RULE_RESPONSE,
            json: json
        });
    },

    getAllHostResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_ALL_HOST_RESPONSE,
            json: json
        });
    },

    delHostResponse: function(hostId) {
        console.log('ServerActionCreators.delHostResponse', hostId);
        AppDispatcher.dispatch({
            type: ActionTypes.DEL_HOST_RESPONSE,
            hostId: hostId
        });
    },

    getAllConfigResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_ALL_CONFIG_RESPONSE,
            json: json
        });
    },

    getAllHostConfigResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_ALL_HOST_CONFIG_RESPONSE,
            json: json
        });
    },

    getConfigResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_CONFIG_RESPONSE,
            json: json
        });
    },

    getFileResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_FILE_RESPONSE,
            json: json
        });
    },

    updPathResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.UPD_PATH_RESPONSE,
            json: json
        });
    },

    uplFileResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.UPL_FILE_RESPONSE,
            json: json
        });
    },

    getCommentResponse: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_COMMENT_RESPONSE,
            json: json
        });
    }


};

