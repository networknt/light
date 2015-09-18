/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    redirect: function(route) {
        AppDispatcher.dispatch({
            type: ActionTypes.REDIRECT,
            route: route
        });
    }

};


