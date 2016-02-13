var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

module.exports = {
    getAllHost: function() {
        WebAPIUtils.getAllHost();
    },

    delHost: function(host) {
        WebAPIUtils.delHost(host);
    }

};
