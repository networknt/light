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

    updatePaymentAddress: function(user) {
        AppDispatcher.dispatch({
            type: ActionTypes.UPDATE_PAYMENT_ADDRESS,
            user: user
        });
        WebAPIUtils.updUserProfile(user);
    }

};

