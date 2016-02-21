/**
 * Created by steve on 7/31/2015.
 */
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

module.exports = {
    getAllUser: function(pageNo, pageSize) {
        WebAPIUtils.getAllUser(pageNo, pageSize);
    },

    delUser: function(rid) {
        WebAPIUtils.delUser(rid);
    },

    getUser: function(userId) {
        WebAPIUtils.getUser(userId);
    }
};
