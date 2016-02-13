/**
 * Created by steve on 12/08/15.
 */
'use strict';

var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');
var ActionTypes = AppConstants.ActionTypes;

module.exports = {
    getPost: function(entityId) {
        WebAPIUtils.getPost(entityId);
    }
};
