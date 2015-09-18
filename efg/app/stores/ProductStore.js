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
var _products = [];
var _ancestors = [];
var _allowUpdate = false;
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

function _setInventory(productIndex, initialInventory, qty=0) {
    var i = _products[productIndex].variantIndex;
    _products[productIndex].variants[i].inventory = initialInventory - qty;
}

function _removeOneFromInventory(product) {
    console.log('_removeOneFromInventory product = ', product);
    var i = product.variantIndex;
    --_products[product.index].variants[i].inventory;
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

    getVariantIndex: function(variants, sku) {
        return _.findIndex(variants, variant => variant.sku === sku);
    },

    getCatalog: function() {
        return _catalog;
    },

    getSelectedCatalog: function() {
        return _selectedCatalog;
    },

    getAncestors: function() {
        return _ancestors;
    },

    getAllowUpdate: function() {
        return _allowUpdate;
    },

    getOffset: function() {
        return _offset;
    }
});

ProductStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {

        case ActionTypes.SET_PRODUCT_VARIANT:
            var index = payload.index;
            console.log('ProductStore SET_PRODUCT_VARIANT payload =', payload);
            _products[index].variantIndex = payload.variantIndex;
            ProductStore.emitChange();
            break;

        case ActionTypes.SET_PRODUCT_INVENTORY:
            console.log('ProductStore SET_PRODUCT_INVENTORY payload = ', payload);
            _setInventory(payload.productIndex, payload.initialInventory, payload.qty);
            ProductStore.emitChange();
            break;

        case ActionTypes.REMOVE_ONE_FROM_INVENTORY:
            _removeOneFromInventory(payload.product);
            ProductStore.emitChange();
            break;

        case ActionTypes.RECEIVE_CATALOG:
            _catalog = payload.json;
            //console.log('ProductStore _catalog = ', _catalog);
            _selectedCatalog = _catalog[0]['@rid'];
            //console.log('ProductStore _selectedCatalog', _selectedCatalog);
            WebAPIUtils.loadProducts(_selectedCatalog);
            ProductStore.emitChange();
            break;

        case ActionTypes.RECEIVE_PRODUCTS:
            _products = payload.json.products? payload.json.products :[];
            _products.forEach(_productsMixin);
            _ancestors = payload.json.ancestors;
            _allowUpdate = payload.json.allowAdd;
            console.log('ProductStore _products = ', _products);
            console.log('ProductStore _ancestors = ', _ancestors);
            console.log('ProductStore _allowUpdate = ', _allowUpdate);
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
                