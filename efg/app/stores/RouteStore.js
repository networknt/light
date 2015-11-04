/**
 * Created by steve on 08/07/15.
 */
var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var AuthStore = require('../stores/AuthStore.js');
//var BlogStore = require('../stores/BlogStore.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');
import {Router, Route} from 'react-router';
var routes = require('../routes.js');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var RouteStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function() {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getRoutes: function() {
        return routes;
    },

    redirectHome: function() {
        router.transitionTo('app');
    }
});

RouteStore.dispatchToken = AppDispatcher.register(function(payload) {
    AppDispatcher.waitFor([
        AuthStore.dispatchToken
    ]);
    //console.log('payload', payload);
    var type = payload.type;

    switch(type) {

        case ActionTypes.REDIRECT:
            router.transitionTo(action.route);
            break;

        case ActionTypes.LOGIN_RESPONSE:
            if (AuthStore.isLoggedIn()) {
                router.transitionTo('app');
            }
            break;

        case ActionTypes.RECEIVE_CREATED_STORY:
            router.transitionTo('app');
            break;

        default:
    }

    return true;
});

module.exports = RouteStore;

