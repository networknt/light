/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    loadShippingAddress: function(userId) {
        AppDispatcher.dispatch({
            type: ActionTypes.LOAD_SHIPPING_ADDRESS,
            userId: userId
        });
        WebAPIUtils.loadShippingAddress(userId);
    },

    loadPaymentAddress: function(userId) {
        AppDispatcher.dispatch({
            type: ActionTypes.LOAD_PAYMENT_ADDRESS,
            userId: userId
        });
        WebAPIUtils.loadPaymentAddress(userId);
    },

    receiveShippingAddress: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_SHIPPING_ADDRESS,
            json: json
        });
    },

    receivePaymentAddress: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_PAYMENT_ADDRESS,
            json: json
        });
    },

    createShippingAddress: function(userId, model) {
        AppDispatcher.dispatch({
            type: ActionTypes.CREATE_SHIPPING_ADDRESS,
            userId: userId,
            model: model
        });
        WebAPIUtils.createShippingAddress(userId, model);
    },

    createPaymentAddress: function(userId, model) {
        AppDispatcher.dispatch({
            type: ActionTypes.CREATE_PAYMENT_ADDRESS,
            userId: userId,
            model: model
        });
        WebAPIUtils.createPaymentAddress(userId, model);
    },

    receiveCreatedShippingAddress: function(json, errors) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_CREATED_SHIPPING_ADDRESS,
            json: json,
            errors: errors
        });
    },

    receiveCreatedPaymentAddress: function(json, errors) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_CREATED_PAYMENT_ADDRESS,
            json: json,
            errors: errors
        });
    }

};

