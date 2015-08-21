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
var _catalog = [];
var _selectedCatalog = null;
var _offset = 0;


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


var ProductStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getProducts: function() {
        return _products;
    },

    getCatalog: function() {
        return _catalog;
    },

    getSelectedCatalog: function() {
        return _selectedCatalog;
    },

    getOffset: function() {
        return _offset;
    },

    getVariantIndex: function(variants, sku) {
        return _.findIndex(variants, variant => variant.sku === sku);
    }
});

ProductStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {

        case ActionTypes.RECEIVE_ALL_PRODUCTS:
            console.log('received products');
            _products = action.products;
            _products.forEach(_productsMixin);
            ProductStore.emitChange();
            break;

        case ActionTypes.SET_PRODUCT_VARIANT:
            var id = action.productId;
            _products[id].variantIndex = action.variantIndex;
            ProductStore.emitChange();
            break;

        case ActionTypes.SET_PRODUCT_INVENTORY:
            _setInventory(action.id, action.initialInventory, action.qty);
            ProductStore.emitChange();
            break;

        case ActionTypes.REMOVE_ONE_FROM_INVENTORY:
            _removeOneFromInventory(action.product);
            ProductStore.emitChange();
            break;

        case ActionTypes.RECEIVE_CATALOG:
            _catalog = payload.json;
            console.log('ProductStore _catalog = ', _catalog);
            _selectedCatalog = _catalog[0]['@rid'];
            console.log('ProductStore _selectedCatalog', _selectedCatalog);
            ProductStore.emitChange();
            break;

        case ActionTypes.SELECT_CATALOG:
            _selectedCatalog = payload.rid;
            console.log('ProductStore _selectedCatalog', _selectedCatalog);
            ProductStore.emitChange();
            break;

        default:
            return true;
    }

    return true;
});

module.exports = ProductStore;
                