var WebAPIUtils = require('../utils/WebAPIUtils.js');

module.exports = {

    getConfig: function(configId) {
        WebAPIUtils.getConfig(configId);
    },

    getAllConfig: function() {
        WebAPIUtils.getAllConfig();
    }

};

