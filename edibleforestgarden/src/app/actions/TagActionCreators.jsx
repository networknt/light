/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {
    getTagEntity: function(tagId, pageNo, pageSize) {
        WebAPIUtils.getTagEntity(tagId, pageNo, pageSize);
    }
};

