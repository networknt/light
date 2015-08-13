/**
 * Created by steve on 12/08/15.
 */
'use strict';

var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');
var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    setProductVariant: function(variant) {
        AppDispatcher.dispatch({
            type: ActionTypes.SET_PRODUCT_VARIANT,
            productId: variant.productId,
            variantIndex: variant.variantIndex
        });
    },

    removeOneFromInventory: function(product) {
        AppDispatcher.dispatch({
            type: ActionTypes.REMOVE_ONE_FROM_INVENTORY,
            product: product
        });
    },

    setInventory: function (id, initialInventory, qty) {
        AppDispatcher.dispatch({
            type: ActionTypes.SET_PRODUCT_INVENTORY,
            id: id,
            initialInventory: initialInventory,
            qty: qty
        });
    }

};
