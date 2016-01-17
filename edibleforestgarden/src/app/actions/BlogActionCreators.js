/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    getBlogTree: function() {
        WebAPIUtils.getBlogTree();
    },

    getBlogPost: function(rid, pageNo, pageSize) {
        WebAPIUtils.getBlogPost(rid, pageNo, pageSize);
    },

    addPost: function(action) {
        WebAPIUtils.addPost(action);
    },

    updPost: function(action) {
        WebAPIUtils.updPost(action);
    },

    delPost: function(rid) {
        WebAPIUtils.delPost(rid);
    },

    receiveBlog: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_BLOG,
            json: json
        });
    },


    receiveCreatedBlog: function(json, errors) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_CREATED_STORY,
            json: json,
            errors: errors
        });
    }

};

