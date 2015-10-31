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
        console.log('ProductActionCreator setProductVariant', variant);
        AppDispatcher.dispatch({
            type: ActionTypes.SET_PRODUCT_VARIANT,
            index: variant.index,
            variantIndex: variant.variantIndex
        });
    },
    /*
    removeOneFromInventory: function(product) {
        AppDispatcher.dispatch({
            type: ActionTypes.REMOVE_ONE_FROM_INVENTORY,
            product: product
        });
    },

    setInventory: function (productIndex, initialInventory, qty) {
        AppDispatcher.dispatch({
            type: ActionTypes.SET_PRODUCT_INVENTORY,
            productIndex: productIndex,
            initialInventory: initialInventory,
            qty: qty
        });
    },
    */

    loadCatalog: function() {
        AppDispatcher.dispatch({
            type: ActionTypes.LOAD_CATALOG
        });
        WebAPIUtils.loadCatalog();
    },

    selectCatalog: function(node, selected, onCategorySelect) {
        console.log('ProductActionCreators is called' || node || selected || onCategorySelect);
        AppDispatcher.dispatch({
            type: ActionTypes.SELECT_CATALOG,
            rid: node.props.catalog['@rid'],
            node: node,
            selected: selected,
            onCategorySelect: onCategorySelect
        });

        WebAPIUtils.loadProducts(node.props.catalog['@rid']);
    }

};
