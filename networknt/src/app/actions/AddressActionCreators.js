/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    updateShippingAddress: function(data) {
        WebAPIUtils.updateShippingAddress(data);
    },

    confirmShippingAddress: function(data) {
        WebAPIUtils.confirmShippingAddress(data);
    },

    updateBillingAddress: function(data) {
        WebAPIUtils.updateBillingAddress(data);
    },

    confirmBillingAddress: function(data) {
        WebAPIUtils.confirmBillingAddress(data);
    }

};

