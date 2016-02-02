/**
 * Created by steve on 12/08/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';
var _ = require('lodash');

var _shipping;
var _taxes;
var _clientToken;

var _cartItems = [];
var _inventory = {};  //entityId is key of map(sku, inventory) as inventory for variants. used to update product summary and detail.
var _isOpen = false;

function _add(product) {
    _cartItems.push(product);
}

function _getItemBySku(sku) {
    return _.find(_cartItems, item =>
        item.sku === sku
    );
}

function _setQty(qty, sku) {
    _getItemBySku(sku).qty = qty;
}

function _remove(sku) {
    _cartItems =  _.reject(_cartItems, item =>
        item.sku === sku
    );
}

function _create(product) {
    var i = product.variantIndex;
    var newItem = _.assign({}, product, product.variants[i]);
    newItem.qty = 1;
    newItem.initialInventory = product.variants[i].inventory;
    return newItem;
}

function _toggleCart() {
    //console.log('CartStore toggleCart is called.');
    _isOpen = !_isOpen;
}

var CartStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getAll: function() {
        //console.log('CartStore getAll is called', _cartItems);
        return _cartItems;
    },

    getCartItemsCount: function() {
        //console.log('CartStore getCartItemsCound is called');
        return _.reduce(_cartItems, function(count, item) {
            return (count = count + Number(item.qty));
        }, 0);
    },

    getCartTotal: function() {
        //console.log('CartStore getCartTotal is called');
        return _.reduce(_cartItems, function(total, item) {
            total = total + Number(item.price * item.qty);
            total.toFixed(2);
            return total;
        }, 0);
    },

    getShipping: function() {
        return _shipping;
    },

    getTaxes: function() {

        return _taxes;
    },

    getClientToken: function() {
        return _clientToken;
    },

    getCartStatus: function() {
        //console.log('CartStore getCartStatus is called', _isOpen)
        return _isOpen;
    },

    getInventory: function(entityId) {
        return _inventory[entityId];  // each product summary will use entityId to query inventory
    }
});


CartStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch (type) {

        case ActionTypes.ADD_PRODUCT_TO_CART:
            var product = payload.product;
            var i = product.variantIndex;
            var sku = product.variants[i].sku;
            // check if this cartItem has been added before.
            var cartItem = _getItemBySku(sku);
            if(!cartItem) {
                cartItem = _create(product);
                _add(cartItem);
            } else {
                cartItem.qty += 1;
            }
            // upd inventory
            let inventory = _inventory[product.entityId];
            if(!inventory) {
                inventory = {};
                for(var j = 0; j < product.variants.length; j++) {
                    inventory[product.variants[j].sku] = product.variants[j].inventory
                }
                _inventory[product.entityId] = inventory;
            }
            inventory[sku] = inventory[sku] - 1;
            console.log('CartStore.ADD_PRODUCT_TO_CART _inventory', _inventory);
            CartStore.emitChange();
            break;

        case ActionTypes.SET_QTY:
            _setQty(payload.qty, payload.cartItem.sku);
            // upd inventory
            _inventory[payload.cartItem.entityId][payload.cartItem.sku] = payload.cartItem.initialInventory - payload.qty;
            CartStore.emitChange();
            break;

        case ActionTypes.REMOVE_CART_ITEM:
            _remove(payload.cartItem.sku);
            // upd inventory
            _inventory[payload.cartItem.entityId][payload.cartItem.sku] = payload.cartItem.initialInventory;
            CartStore.emitChange();
            break;

        case ActionTypes.TOGGLE_CART:
            _toggleCart();
            CartStore.emitChange();
            break;
        case ActionTypes.UPDATE_SHIPPING_ADDRESS_RESPONSE:
            //console.log('shippingAddress update res ', payload.json);
            _shipping = payload.json.shipping;
            _taxes = payload.json.taxes;

            CartStore.emitChange();
            break;

        case ActionTypes.CONFIRM_SHIPPING_ADDRESS_RESPONSE:
            //console.log('shippingAddress confirm res ', payload.json);
            _shipping = payload.json.shipping;
            _taxes = payload.json.taxes;
            CartStore.emitChange();
            break;

        case ActionTypes.RECEIVE_CLIENT_TOKEN:
            _clientToken = payload.json.clientToken;
            CartStore.emitChange();
            break;

        case ActionTypes.RESET_CART_ITEM:
            _cartItems.length = 0;
            CartStore.emitChange();
            break;

        default:
            return true;

    }
    return true;
});

module.exports = CartStore;
