var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

module.exports = {
    getAllHost: function() {
        WebAPIUtils.getAllHost();
    },

    delHost: function(hostId) {
        WebAPIUtils.delHost(hostId);
    }

};
