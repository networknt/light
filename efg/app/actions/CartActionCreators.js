/**
 * Created by steve on 12/08/15.
 */
'use strict';

var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    addToCart: function(product) {
        AppDispatcher.dispatch({
            type: ActionTypes.ADD_PRODUCT_TO_CART,
            product: product
        });
    },

    setQty: function(qty, sku) {
        AppDispatcher.dispatch({
            type: ActionTypes.SET_QTY,
            qty: qty,
            sku: sku
        });
    },

    remove: function(sku) {
        AppDispatcher.dispatch({
            type: ActionTypes.REMOVE_CART_ITEM,
            sku: sku
        });
    },

    toggleCart: function(isOpen) {
        AppDispatcher.dispatch({
            type: ActionTypes.TOGGLE_CART
        });
    }

};
