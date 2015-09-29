/*
 * Copyright 2015 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

require('bootstrap/less/bootstrap.less');

var React = require('react');

var Router = require('react-router')
    , RouteHandler = Router.RouteHandler
    , Route = Router.Route;

var AuthActionCreators = require('./actions/AuthActionCreators.js');
var AuthStore = require('./stores/AuthStore.js');
var AppConstants = require('./constants/AppConstants.js');
var $ = require('jquery');
var buffer = [];  // save all the requests that gets token expired
var refreshing = false;

require('./assets/stylesheets/main.scss');

window.React = React;

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
        console.log('beforeSend', xhr);
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
        console.log('jqxhr = ', jqxhr);
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
                refreshReq.open('POST', 'http://example:8080/api/rs', true);
                refreshReq.send(JSON.stringify(refreshToken));
            }
        } else {
            console.log('other error than 401', jqxhr.responseText);
            deferred.rejectWith(jqxhr, args);
        }
    });

    return deferred.promise(jqxhr);
});

var router = require('./stores/RouteStore.js').getRouter();

// TODO remove it
var warn = console.warn;
console.warn = function(warning) {
    if (/(setState)/.test(warning)) {
        throw new Error(warning);
    }
    warn.apply(console, arguments);
};


router.run(function (Handler) {
    React.render(<Handler/>, document.getElementById('content'));
});
