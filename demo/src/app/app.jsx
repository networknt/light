import React from 'react';
import ReactDOM from 'react-dom';
import {Router} from 'react-router';
import AppRoutes from './app-routes.jsx';
import injectTapEventPlugin from 'react-tap-event-plugin';
import createHistory from 'history/lib/createBrowserHistory';

import AuthActionCreators from './actions/AuthActionCreators';
import AuthStore from './stores/AuthStore';
import AppConstants from './constants/AppConstants';
import $ from 'jquery';

require('../www/assets/stylesheets/main.scss');

let buffer = [];  // save all the requests that gets token expired
let refreshing = false;

//Helpers for debugging
window.React = React;
window.Perf = require('react-addons-perf');

function replayBuffer() {
  var deferred;
  var promises = [];

  for(var i in buffer) {
    deferred = buffer[i].deferred;
    buffer[i].options.refreshRetry = true;
    promises.push($.ajax(buffer[i].options).then(deferred.resolve, deferred.reject));
  }

  buffer = [];

  $.when.apply($, promises)
      .done(function() {
        console.log('retry requests are all done');
        refreshing = false;
      })
      .fail(function(){
        refreshing = false;
        console.log('at least one retry failed');
        AuthActionCreators.logout();
      });
};

$.ajaxSetup({
  beforeSend: function (xhr) {
    //console.log('beforeSend', xhr);
    var accessToken = AuthStore.getAccessToken();
    if (accessToken) {
      xhr.setRequestHeader('Authorization', 'Bearer ' + accessToken);
    }
  }
});

$.ajaxPrefilter(function(options, originalOptions, jqxhr) {
  // you could pass this option in on a retry so that it doesn't
  // get all recursive on you.
  if (options.refreshRetry === true) {
    return;
  }
  // our own deferred object to handle done/fail callbacks
  var deferred = $.Deferred();

  //self.currentRequests.push(options.url);
  //jqxhr.always(function(){
  //    self.currentRequests.splice($.inArray(options.url, self.currentRequests), 1);
  //});
  jqxhr.done(deferred.resolve);
  jqxhr.fail(function() {
    var args = Array.prototype.slice.call(arguments);
    var refreshToken = {
      category: 'user',
      name: 'refreshToken',
      readOnly: true,
      data: {
        refreshToken: AuthStore.getRefreshToken(),
        userId: AuthStore.getUserId(),
        clientId: AppConstants.ClientId
      }
    };
    //console.log('jqxhr = ', jqxhr);
    if (jqxhr.status === 401 && jqxhr.responseText === '{"error":"token_expired"}') {
      console.log('token expired, renew...');
      buffer.push({options: options, deferred: deferred});
      if(!refreshing) {
        var refreshReq = new XMLHttpRequest();
        refreshReq.onreadystatechange = function () {
          if(refreshReq.readyState == 4 && refreshReq.status == 200) {
            console.log('refreshToken', refreshReq.responseText);
            var jsonPayload = JSON.parse(refreshReq.responseText);
            AuthActionCreators.refresh(jsonPayload.accessToken);
            replayBuffer();
          } else {
            console.log('failed to get access token from refresh token');
            AuthActionCreators.logout();
          }
        }
        refreshReq.open('POST', '/api/rs', true);
        refreshReq.send(JSON.stringify(refreshToken));
      }
    } else {
      console.log('other error than 401', jqxhr.responseText);
      deferred.rejectWith(jqxhr, args);
    }
  });

  return deferred.promise(jqxhr);
});


//Needed for onTouchTap
//Can go away when react 1.0 release
//Check this repo:
//https://github.com/zilverline/react-tap-event-plugin
injectTapEventPlugin();

/**
 * Render the main app component. You can read more about the react-router here:
 * https://github.com/rackt/react-router/blob/master/docs/guides/overview.md
 */
ReactDOM.render(
  <Router
    history={createHistory()}
    onUpdate={() => window.scrollTo(0, 0)}
  >
    {AppRoutes}
  </Router>
, document.getElementById('app'));
