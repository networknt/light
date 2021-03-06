/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    getCommentTree: function(parentRid, pageNo, pageSize, sortedBy, sortDir) {
        WebAPIUtils.getCommentTree(parentRid, pageNo, pageSize, sortedBy, sortDir);
    },

    addComment: function(data) {
        WebAPIUtils.addComment(data);
    },

    delComment: function(data) {
        WebAPIUtils.delComment(data);
    },

    updComment: function(data) {
        WebAPIUtils.updComment(data);
    },

    spmComment: function(data) {
        WebAPIUtils.spmComment(data);
    },

    upComment: function(data) {
        WebAPIUtils.upComment(data);
    },

    downComment: function(data) {
        WebAPIUtils.downComment(data);
    }

};

