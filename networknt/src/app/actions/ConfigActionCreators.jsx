var WebAPIUtils = require('../utils/WebAPIUtils.js');

module.exports = {

    getConfig: function(configId) {
        WebAPIUtils.getConfig(configId);
    },

    getAllConfig: function() {
        WebAPIUtils.getAllConfig();
    },

    getAllHostConfig: function() {
        WebAPIUtils.getAllHostConfig();
    },

    delConfig: function(rid) {
        WebAPIUtils.delConfig(rid);
    },

    delHostConfig: function(rid) {
        WebAPIUtils.delHostConfig(rid);
    }
};

