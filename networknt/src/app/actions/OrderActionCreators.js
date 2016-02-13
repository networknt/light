var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    addOrder: function(order) {
        AppDispatcher.dispatch({
            type: ActionTypes.ADD_ORDER,
            order: order
        });
        WebAPIUtils.addOrder(order);
    }

};
