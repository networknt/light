/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    updateShippingAddress: function(data) {
        AppDispatcher.dispatch({
            type: ActionTypes.UPDATE_SHIPPING_ADDRESS,
            data: data
        });
        WebAPIUtils.updShippingAddress(data);
    },

    confirmShippingAddress: function(data) {
        AppDispatcher.dispatch({
            type: ActionTypes.CONFIRM_SHIPPING_ADDRESS_REQUEST,
            data: data
        });
        WebAPIUtils.cnfShippingAddress(data);
    }

};

