/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    getForm: function(formId) {
        WebAPIUtils.getForm(formId);
    },

    getAllForm: function() {
        WebAPIUtils.getAllForm();
    },

    receiveForm: function(json) {
        AppDispatcher.dispatch({
            type: ActionTypes.RECEIVE_FORM,
            json: json
        });
    },

    submitForm: function(action) {
        WebAPIUtils.submitForm(action);
    },

    setFormModel: function(formId, json) {
        console.log('setFormModel', formId, json);
        AppDispatcher.dispatch({
            type: ActionTypes.SET_FORM_MODEL,
            formId: formId,
            json: json
        });
    }

};

