/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    getForumTree: function() {
        WebAPIUtils.getForumTree();
    },

    getForumPost: function(rid, pageNo, pageSize) {
        WebAPIUtils.getForumPost(rid, pageNo, pageSize);
    },

    getRecentForumPost: function(pageNo, pageSize) {
        WebAPIUtils.getRecentForumPost(pageNo, pageSize);
    },

    getForum: function(host) {
        WebAPIUtils.getForum(host);
    },

    delForum: function(rid) {
        WebAPIUtils.delForum(rid);
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

    receiveForum: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_FORUM,
            json: json
        });
    },


    receiveCreatedForum: function(json, errors) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_CREATED_STORY,
            json: json,
            errors: errors
        });
    }

};

