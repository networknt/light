var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    getClientToken: function() {
        WebAPIUtils.getClientToken();
    },

    addTransaction: function(payment, orderId) {
        WebAPIUtils.addTransaction(payment, orderId);
    },

    addSubscription: function(payment, orderId) {
        WebAPIUtils.addSubscription(payment, orderId);
    }

};

