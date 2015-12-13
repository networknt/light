/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    getForm: function(formId) {
        AppDispatcher.dispatch({
            type: ActionTypes.GET_FORM,
            formId: formId
        });
        WebAPIUtils.getForm(formId);
    },

    receiveForm: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_FORM,
            json: json
        });
    }

};

