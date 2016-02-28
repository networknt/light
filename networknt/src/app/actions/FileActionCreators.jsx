/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    getFile: function(path) {
        WebAPIUtils.getFile(path);
    },

    getContent: function(path, name) {
        WebAPIUtils.getContent(path, name);
    },

    uplFile: function(name, path, content) {
        WebAPIUtils.uplFile(name, path, content);
    },

    delFile: function (currentPath, file) {
        WebAPIUtils.delFile(currentPath, file);
    },

    addFolder: function (path, folder) {
        WebAPIUtils.addFolder(path, folder);
    },

    renFile: function (path, oldName, newName) {
        WebAPIUtils.renFile(path, oldName, newName);
    }

};

