var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _comments = [];
var _total = 0;
var _allowUpdate = false;

var CommentStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getComments: function() {
        return _comments;
    },

    getTotal: function() {
        return _total;
    },

    getAllowUpdate: function () {
        return _allowUpdate;
    }
});

CommentStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.GET_COMMENT_TREE_RESPONSE:
            _total = payload.json.total;
            _allowUpdate = payload.json.allowUpdate;
            if(_total == 0) {
                _comments = [];
            } else {
                _comments = payload.json.entities;
            }
            CommentStore.emitChange();
            break;
    }
    return true;
});

module.exports = CommentStore;
