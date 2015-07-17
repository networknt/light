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


/**
 * init http hooks for oauth
 *
 */
AjaxInterceptor.addRequestCallback(function(xhr) {
    console.debug("request",xhr);

});
AjaxInterceptor.addResponseCallback(function(xhr) {
    console.debug("response",xhr);
});

// Will proxify XHR to fire the above callbacks
AjaxInterceptor.wire();

var router = require('./stores/RouteStore.js').getRouter();

router.run(function (Handler) {
    React.render(<Handler/>, document.getElementById('content'));
});
