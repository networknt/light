/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    getCatalogTree: function() {
        WebAPIUtils.getCatalogTree();
    },

    getCatalog: function(host) {
        WebAPIUtils.getCatalog(host);
    },

    delCatalog: function(rid) {
        WebAPIUtils.delCatalog(rid);
    },

    getCatalogProduct: function(rid, pageNo, pageSize) {
        WebAPIUtils.getCatalogProduct(rid, pageNo, pageSize);
    },

    addProduct: function(action) {
        WebAPIUtils.addProduct(action);
    },

    updProduct: function(action) {
        WebAPIUtils.updProduct(action);
    },

    delProduct: function(rid) {
        WebAPIUtils.delProduct(rid);
    }

};
