/**
 * Created by steve on 7/31/2015.
 */
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

module.exports = {
    getRole: function() {
        WebAPIUtils.getRole();
    },

    delRole: function(rid) {
        WebAPIUtils.delRole(rid);
    }

};
