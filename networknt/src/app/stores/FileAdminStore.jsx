/**
 * Created by steve on 7/31/2015.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _fileMap = {};
var _path = '.';

var FileAdminStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getFiles: function(path) {
        return _fileMap[path];
    },

    getPath: function() {
        return _path;
    }

});

FileAdminStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.GET_FILE_RESPONSE:
        case ActionTypes.UPD_PATH_RESPONSE:
            _path = payload.json.path;
            _fileMap[_path] = payload.json.children;
            FileAdminStore.emitChange();
            break;
    }
    return true;
});

module.exports = FileAdminStore;
