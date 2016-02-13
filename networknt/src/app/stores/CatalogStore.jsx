var AppDispatcher = require('../dispatcher/AppDispatcher');
var EventEmitter = require('events').EventEmitter;
var AppConstants = require('../constants/AppConstants');
var ActionTypes = AppConstants.ActionTypes;
var WebAPIUtils = require('../utils/WebAPIUtils.js');
var _ = require('lodash');

var CHANGE_EVENT = 'change';

var _products = [];
var _ancestors = [];
var _total = 0;
var _allowUpdate = false;
var _product = {};

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
    //console.log('_removeOneFromInventory product = ', product);
    var i = product.variantIndex;
    --_products[product.index].variants[i].inventory;
}



var CatalogStore = _.extend({}, EventEmitter.prototype, {

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

    getAncestors: function() {
        return _ancestors;
    },

    getAllowUpdate: function() {
        return _allowUpdate;
    },

    getTotal: function() {
        return _total;
    },

    getProduct: function() {
        return _product;
    }

});

AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.SET_PRODUCT_VARIANT:
            var index = payload.index;
            _products[index].variantIndex = payload.variantIndex;
            console.log('ProductStore after setVariant', _products);
            CatalogStore.emitChange();
            break;

        case ActionTypes.SET_PRODUCT_INVENTORY:
            //console.log('ProductStore SET_PRODUCT_INVENTORY payload = ', payload);
            _setInventory(payload.productIndex, payload.initialInventory, payload.qty);
            CatalogStore.emitChange();
            break;

        case ActionTypes.REMOVE_ONE_FROM_INVENTORY:
            _removeOneFromInventory(payload.product);
            CatalogStore.emitChange();
            break;

        case ActionTypes.GET_CATALOG_PRODUCT_RESPONSE:
            _total = payload.json.total;
            _allowUpdate = payload.json.allowUpdate;
            if(_total == 0) {
                _products = [];
            } else {
                _products = payload.json.entities;
                _products.forEach(_productsMixin);
            }
            CatalogStore.emitChange();
            break;

    }

    return true;
});

module.exports = CatalogStore;