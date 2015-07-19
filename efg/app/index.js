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

var AjaxInterceptor = require('ajax-interceptor');
var AuthStore = require('./stores/AuthStore.js');

/**
 * init http hooks for oauth
 *
 */
AjaxInterceptor.addRequestCallback(function(xhr) {
    console.debug("request",xhr);
    // get the access token from store and put it into the header.
    var accessToken = AuthStore.getAccessToken();
    if(accessToken) {
        xhr.setRequestHeader('Authorization', 'Bearer ' + accessToken);
    }
});

AjaxInterceptor.addResponseCallback(function(xhr) {
    console.debug("response",xhr);
    // intercept token expire error and refresh token.
    console.log('xhr.status', xhr.status);
    console.log('xhr.requesturl', xhr.responseURL);
    console.log('xhr.responseText', xhr.responseText);
    //var error = JSON.parse(xhr.responseText);
    //console.log('error', error);
    //console.log('xhr.error', JSON.parse(xhr.responseText).error);

    if(xhr.status === 401 && xhr.responseText === '{"error":"token_expired"}') {
        console.log('token expired, renewing...');
        

    } else {
        console.log('other error that needs to be displayed.');

    }
});

// Will proxify XHR to fire the above callbacks
AjaxInterceptor.wire();

var router = require('./stores/RouteStore.js').getRouter();

router.run(function (Handler) {
    React.render(<Handler/>, document.getElementById('content'));
});
