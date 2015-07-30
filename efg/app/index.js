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

//var AjaxInterceptor = require('ajax-interceptor');
var AuthActionCreators = require('./actions/AuthActionCreators.js');
var AuthStore = require('./stores/AuthStore.js');
var AppConstants = require('./constants/AppConstants.js');
var $ = require('jquery');
var buffer = [];  // save all the requests that gets token expired
var refreshing = false;

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

/*
var axios = require('axios');
var buildUrl = require('./utils/buildUrl.js');


// Add a request interceptor
axios.interceptors.request.use(function (config) {
    config.headers = config.headers || {};
    var accessToken = AuthStore.getAccessToken();
    if (accessToken) {
        config.headers.Authorization = 'Bearer ' + accessToken;
    }
    return config;
}, function (error) {
    // Do something with request error
    return Promise.reject(error);
});

// Add a response interceptor
axios.interceptors.response.use(function (response) {
    // Do something with response data
    console.log('response interceptor', response);
    return response;
}, function (error) {
    // Do something with response error
    //console.log('error response interceptor', error);
    if (error.status == 401) {
        //console.log('401 error response')
        if (error.data.error == 'token_expired') {
            console.log('token expired');
            var originalConfig = error.config;
            // get accessToken from refreshToken
            refreshToken(originalConfig).then(axios(originalConfig), null);
        };
    };
});


function refreshToken(originalConfig) {
    var refreshTokenPost = {
        category: 'user',
        name: 'refreshToken',
        readOnly: true,
        data: {
            refreshToken: AuthStore.getRefreshToken(),
            userId: AuthStore.getUserId(),
            clientId: AppConstants.ClientId
        }
    };

    // Return a new promise.
    return new Promise(function (resolve, reject) {
        // Do the usual XHR stuff
        console.log('refresh is called.');
        var req = new XMLHttpRequest();
        req.open('POST', 'http://example:8080/api/rs');

        req.onload = function () {
            // This is called even on 404 etc
            // so check the status
            if (req.status == 200) {
                console.log('refresh token success', req.response);
                var accessToken = JSON.parse(req.response).accessToken;
                console.log('accessToken in refreshToken', accessToken);
                AuthActionCreators.refresh(accessToken);
                originalConfig.headers = originalConfig.headers || {};
                var accessToken = AuthStore.getAccessToken();
                if (accessToken) {
                    originalConfig.headers.Authorization = 'Bearer ' + accessToken;
                }
                // Resolve the promise with the response text
                resolve(req.response);
            }
            else {
                // Otherwise reject with the status text
                // which will hopefully be a meaningful error
                reject(Error(req.statusText));
            }
        };

        // Handle network errors
        req.onerror = function () {
            reject(Error("Network Error"));
        };

        // Make the request
        req.send(JSON.stringify(refreshTokenPost));
    });
};


function retryRequest(config, promise) {
    console.log("retryRequest config", config);
    function successCallback(response) {
        console.log('internal retryRequest response', response);
        promise.resolve(response);
    }
    function errorCallback(response) {
        promise.reject(response);
    }
    axios(originalConfig).then(successCallback, errorCallback);
}

function buildUrl(url, serializedParams) {
    if (serializedParams.length > 0) {
        url += ((url.indexOf('?') == -1) ? '?' : '&') + serializedParams;
    }
    return url;
};

function type(obj) {
    return Object.prototype.toString.call(obj).slice(8, -1);
}
*/

var router = require('./stores/RouteStore.js').getRouter();

router.run(function (Handler) {
    React.render(<Handler/>, document.getElementById('content'));
});
