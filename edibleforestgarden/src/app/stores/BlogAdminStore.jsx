var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _blogs = [];
var error = null;


var BlogAdminStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getBlogs: function() {
        return _blogs;
    },

    getErrors: function() {
        return _errors;
    }

});

BlogAdminStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.GET_BLOG_RESPONSE:
            error = payload.error;
            //console.log('error', error);
            if(!error) {
                _blogs = payload.json;
            }
            BlogAdminStore.emitChange();
            break;
    }

    return true;
});

module.exports = BlogAdminStore;
