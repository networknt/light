/**
 * Created by steve on 12/08/15.
 */
'use strict';

var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';
var _ = require('lodash');
var _products = {};

// Helper object to track state of current selected product variant
var _variant = {
    variantIndex: 0
};

function _productsMixin(product) {
    _.extend(product, _variant);
}

function _setInventory(id, initialInventory, qty=0) {
    var i = _products[id].variantIndex;
    _products[id].variants[i].inventory = initialInventory - qty;
}

function _removeOneFromInventory(product) {
    var i = product.variantIndex;
    var id = product.id;
    --_products[id].variants[i].inventory;
}

class ProductStore extends EventEmitter {

    constructor() {
        super();
    }

    emitChange() {
        this.emit(CHANGE_EVENT);
    }

    addChangeListener(callback) {
        this.on(CHANGE_EVENT, callback);
    }

    removeChangeListener(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    }

    getAll() {
        return _products;
    }

    getVariantIndex(variants, sku) {
        return _.findIndex(variants, variant => variant.sku === sku);
    }
}

ProductStore.dispatchToken = AppDispatcher.register(function(action) {

    //console.info(`Action type: ${action.type}`);
    switch(action.type) {
        case ActionTypes.RECEIVE_ALL_PRODUCTS:
            console.log('received products');
            _products = action.products;
            _products.forEach(_productsMixin);
            productStore.emitChange();
            break;

        case ActionTypes.SET_PRODUCT_VARIANT:
            var id = action.productId;
            _products[id].variantIndex = action.variantIndex;
            productStore.emitChange();
            break;

        case ActionTypes.SET_PRODUCT_INVENTORY:
            _setInventory(action.id, action.initialInventory, action.qty);
            productStore.emitChange();
            break;

        case ActionTypes.REMOVE_ONE_FROM_INVENTORY:
            _removeOneFromInventory(action.product);
            productStore.emitChange();
            break;

        default:
            return true;
    }

    return true;

});

var productStore = new ProductStore();

module.exports = productStore;
                