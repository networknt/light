/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    serverErrorResponse: function(error) {
        AppDispatcher.dispatch({
            type: ActionTypes.SERVER_ERROR_RESPONSE,
            error: error
        });
    }
};
