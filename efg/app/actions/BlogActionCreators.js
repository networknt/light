/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');

var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    loadStories: function() {
        AppDispatcher.dispatch({
            type: ActionTypes.LOAD_STORIES
        });
        WebAPIUtils.loadStories();
    },

    loadStory: function(storyId) {
        AppDispatcher.dispatch({
            type: ActionTypes.LOAD_STORY,
            storyId: storyId
        });
        WebAPIUtils.loadStory(storyId);
    },

    createStory: function(title, body) {
        AppDispatcher.dispatch({
            type: ActionTypes.CREATE_STORY,
            title: title,
            body: body
        });
        WebAPIUtils.createStory(title, body);
    }

};

