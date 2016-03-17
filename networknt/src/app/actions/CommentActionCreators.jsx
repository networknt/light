/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    getComment: function(parentRid) {
        WebAPIUtils.getComment(parentRid);
    },

    addComment: function(data) {
        WebAPIUtils.addComment(data);
    },

    delComment: function(rid) {
        WebAPIUtils.delComment(rid);
    },

    updComment: function(comment) {
        WebAPIUtils.updComment(comment);
    }
};

