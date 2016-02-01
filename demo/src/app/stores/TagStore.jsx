var AppDispatcher = require('../dispatcher/AppDispatcher');
var EventEmitter = require('events').EventEmitter;
var AppConstants = require('../constants/AppConstants');
var ActionTypes = AppConstants.ActionTypes;
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var CHANGE_EVENT = 'change';

var _entities = [];
var _total = 0;

var TagStore = _.extend({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },


    getEntities: function() {
        return _entities;
    },

    getTotal: function() {
        return _total;
    }
});

AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.GET_TAG_ENTITY_RESPONSE:
            _total = payload.json.total;
            if(_total == 0) {
                _entities = [];
            } else {
                _entities = payload.json.entities;
            }
            TagStore.emitChange();
            break;
    }

    return true;
});

module.exports = TagStore;