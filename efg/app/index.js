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
var axios = require('axios');
var buildUrl = require('./utils/buildUrl.js');


// Add a request interceptor
axios.interceptors.request.use(function (config) {
    config.headers = config.headers || {};
    var accessToken = AuthStore.getAccessToken();
    // TODO Do not put access token into header of refresh token post. In this case,
    // we don't need to remove the authorizationData before sending refresh token post.
    // chances are some other requests might be sent during the time slot and got login
    // is required error and forced to login page.
    // TODO I really don't like this checking. need to find another way? backend?
    if (accessToken) {
        if(config.data && config.data.name && config.data.name === 'refreshToken') {
            return config;
        }
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
    console.log('error response interceptor', error);
    if(error.status == 401) {
        console.log('401 error response')
        if(error.data.error == 'token_expired') {
            console.log('token expired');
            var savedConfig = error.config;
            // get accessToken from refreshToken
            var refreshToken = {
                category:'user',
                name:'refreshToken',
                readOnly:true,
                data : {
                    refreshToken: AuthStore.getRefreshToken(),
                    userId: AuthStore.getUserId(),
                    clientId: AppConstants.ClientId
                }
            };
            axios.post('http://example:8080/api/rs', refreshToken)
                .then(function (refreshResponse) {
                    console.log('refresh token result', refreshResponse.data);
                    var accessToken = refreshResponse.data.accessToken;
                    AuthActionCreators.refresh(accessToken);
                    // now need to resend the savedConfig here.
                    savedConfig.headers = savedConfig.headers || {};
                    var accessToken = AuthStore.getAccessToken();
                    if(accessToken) {
                        savedConfig.headers.Authorization = 'Bearer ' + accessToken;
                    }
                    console.log('saveConfig before post', savedConfig);
                    if(savedConfig.method == 'get') {
                        axios.get(buildUrl(savedConfig.url, savedConfig.params))
                            .then(function (savedResponse) {
                                console.log('retry is OK', savedResponse.data);
                                return savedResponse;
                            })
                            .catch(function(savedError) {
                                console.log('retry get error', savedError.data);
                                Promise.reject(savedError);
                            });
                    } else if (savedConfig.method == 'post') {
                        axios.post(savedConfig.url, savedConfig.data)
                            .then(function (response) {
                                console.log('retry is OK', response.data);
                                return Promise.resolve(response);
                            })
                            .catch(function(response) {
                                console.log('retry get error', response.data);
                                Promise.reject(response);
                            });

                    }
                })
                .catch(function(refreshError) {
                    console.log('error in refresh token', refreshError);
                });
        }

    } else if(error.status == 403) {
        console.log('403 error response');
        // 403 forbidden. The user is logged in but doesn't have permission for the request.
        // logout and redirect to login page.

        return Promise.reject(error);
    }
});


//var httpBuffer = [];

/**
 * init http hooks for oauth
 *
 */
/*
AjaxInterceptor.addRequestCallback(function(xhr) {
    console.debug("request callback",xhr);
    // get the access token from store and put it into the header.
    var accessToken = AuthStore.getAccessToken();
    if(accessToken) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + accessToken);
    }
});

AjaxInterceptor.addResponseCallback(function(xhr) {
    console.debug("response",xhr);
    // intercept token expire error and refresh token.
    //console.log('xhr.status', xhr.status);
    //console.log('xhr.requesturl', xhr.responseURL);
    //console.log('xhr.responseText', xhr.responseText);
    if(xhr.status === 401 && xhr.responseText === '{"error":"token_expired"}') {
        console.log('token expired, renewing...');
        httpBuffer.push(xhr);
        var refreshToken = {
            category:'user',
            name:'refreshToken',
            readOnly:true,
            data : {
                refreshToken: AuthStore.getRefreshToken(),
                userId: AuthStore.getUserId(),
                clientId: AppConstants.ClientId
            }
        };

        var refreshReq = new XMLHttpRequest();
        refreshReq.onreadystatechange = function () {
            if(refreshReq.readyState == 4 && refreshReq.status == 200) {
                console.log('refreshToken', refreshReq.responseText);
                var jsonPayload = JSON.parse(refreshReq.responseText);
                AuthActionCreators.refresh(jsonPayload.accessToken);
            }
            
        }
        refreshReq.open('POST', 'http://example:8080/api/rs', true);
        refreshReq.setRequestHeader('Authorization', '');
        refreshReq.send(JSON.stringify(refreshToken));

    } else {
        console.log('other error or success that needs to be displayed.', xhr.status);
        console.log('response', xhr.responseText);

    }
});

// Will proxify XHR to fire the above callbacks
AjaxInterceptor.wire();
*/


function buildUrl(url, serializedParams) {
    if (serializedParams.length > 0) {
        url += ((url.indexOf('?') == -1) ? '?' : '&') + serializedParams;
    }
    return url;
};

var router = require('./stores/RouteStore.js').getRouter();

router.run(function (Handler) {
    React.render(<Handler/>, document.getElementById('content'));
});
