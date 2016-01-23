var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _pages = {};
var _errors = [];

var PageStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getPage: function(pageId) {
        return _pages[pageId];
    },

    getErrors: function() {
        return _errors;
    }

});

PageStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.GET_PAGE_RESPONSE:
            console.log('PageStore', payload.json);
            _pages[payload.json.pageId] = payload.json;
            _errors = payload.error;
            PageStore.emitChange();
            break;
    }
    return true;
});

module.exports = PageStore;
