/**
 * Created by steve on 7/31/2015.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    loadMenu: function() {
        AppDispatcher.dispatch({
            type: ActionTypes.LOAD_MENU
        });
        WebAPIUtils.loadMenu();
    }
};

