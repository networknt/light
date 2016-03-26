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

    delComment: function(rid) {
        WebAPIUtils.delComment(rid);
    },

    updComment: function(comment) {
        WebAPIUtils.updComment(comment);
    },

    spmComment: function(data) {
        WebAPIUtils.spmComment(data);
    }

};

