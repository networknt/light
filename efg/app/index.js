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

var angular = require('angular');

'use strict';
var lightApp = angular.module('lightApp', [
    require('angular-resource'),
    require('angular-route'),
])
    .config(['$routeProvider', '$locationProvider', '$controllerProvider', '$compileProvider', '$filterProvider', '$provide', function ($routeProvider, $locationProvider, $controllerProvider, $compileProvider, $filterProvider, $provide) {

        // Notice that the registration methods on the
        // module are now being overridden by their provider equivalents
        lightApp.controller = $controllerProvider.register;
        lightApp.directive = $compileProvider.directive;
        lightApp.filter = $filterProvider.register;
        lightApp.factory = $provide.factory;
        lightApp.service = $provide.service;

        $routeProvider
            .when('/', {
                templateUrl: 'views/main.html',
                controller: 'mainCtrl'
            })
            .when('/signin', {
                templateUrl: 'views/form.html',
                controller: 'signinCtrl'
            })
            .when('/form/:formId/:parentId?', {
                templateUrl: 'views/form.html',
                controller: 'formCtrl'
            })
            .when('/page/:pageId', {
                templateUrl: 'views/page.html',
                controller: 'pageCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
        $locationProvider.html5Mode(true);
    }])

require('bootstrap/less/bootstrap.less');

var React = require('react');
var Router = require('react-router')
    , RouteHandler = Router.RouteHandler
    , Route = Router.Route;

var ReactBootstrap = require('react-bootstrap')
    , Nav = ReactBootstrap.Nav
    , ListGroup = ReactBootstrap.ListGroup;

var ReactRouterBootstrap = require('react-router-bootstrap')
    , NavItemLink = ReactRouterBootstrap.NavItemLink
    , ButtonLink = ReactRouterBootstrap.ButtonLink
    , ListGroupItemLink = ReactRouterBootstrap.ListGroupItemLink;

var App = React.createClass({
    render: function() {
        return (
            <div>
                NavItemLink
                <br/>
                <Nav>
                    <NavItemLink to="destination" params={{ someparam: 'hello' }}>Linky!</NavItemLink>
                </Nav>
                <br/>
                ButtonLink<br/>
                <ButtonLink to="destination" params={{ someparam: 'hello' }}>Linky!</ButtonLink>
                <br/>
                <ListGroup>
                    <ListGroupItemLink to="destination" params={{ someparam: 'hello' }}>Linky!</ListGroupItemLink>
                </ListGroup>
                <RouteHandler/>
            </div>
        );
    }
});

var Destination = React.createClass({
    render: function() {
        return <div>You made it!</div>;
    }
});

var routes = (
    <Route handler={App} path="/">
    <Route name="destination" path="destination/:someparam" handler={Destination} />
    </Route>
);

Router.run(routes, function (Handler) {
    React.render(<Handler/>, document.body);
});