var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _news = [];
var error = null;


var NewsAdminStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getNews: function() {
        return _news;
    },

    getError: function() {
        return error;
    }

});

NewsAdminStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.GET_NEWS_RESPONSE:
            error = payload.error;
            //console.log('error', error);
            if(!error) {
                _news = payload.json;
            }
            NewsAdminStore.emitChange();
            break;
    }

    return true;
});

module.exports = NewsAdminStore;
