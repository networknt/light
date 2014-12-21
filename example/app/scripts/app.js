'use strict';

/**
 * @ngdoc overview
 * @name lightApp
 * @description
 * # lightApp
 *
 * Main module of the application.
 */
var lightApp = angular.module('lightApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch',
    'ui.bootstrap',
    'schemaForm',
    'ui.ace',
    'ui.tree',
    'ui.tree-filter',
    'ui.highlight',
    'hc.marked',
    'toaster',
    'schemaForm-marked',
    'mgcrea.ngStrap.helpers.dimensions',
    'mgcrea.ngStrap.helpers.parseOptions',
    'mgcrea.ngStrap.tooltip',
    'mgcrea.ngStrap.select',
    'angular-loading-bar',
    'LocalStorageModule',
    'schemaForm-datepicker',
    'schemaForm-datetimepicker',
    'schemaForm-timepicker'
])
.config(['$httpProvider',
    function ($httpProvider) {
        $httpProvider.interceptors.push('authInterceptorService');
    }
])
.config(['$routeProvider', '$locationProvider', '$controllerProvider', '$compileProvider', '$filterProvider', '$provide', function ($routeProvider, $locationProvider, $controllerProvider, $compileProvider, $filterProvider, $provide) {

    // Notice that the registration methods on the
    // module are now being overridden by their provider equivalents
    lightApp.controller = $controllerProvider.register;
    lightApp.directive  = $compileProvider.directive;
    lightApp.filter     = $filterProvider.register;
    lightApp.factory    = $provide.factory;
    lightApp.service    = $provide.service;

    $routeProvider
      .when('/', {
        templateUrl: 'views/main.html',
        controller: 'mainCtrl'
      })
      .when('/signin', {
        templateUrl: 'views/form.html',
        controller: 'signinCtrl'
      })
      .when('/form/:id/:parentId?', {
        templateUrl: 'views/form.html',
        controller: 'formCtrl'
      })
      .when('/page/:id', {
        templateUrl: 'views/page.html',
        controller: 'pageCtrl'
      })
      .when('/formAdmin', {
        templateUrl: 'views/formAdmin.html',
        controller: 'FormAdminCtrl'
      })
      .when('/menuAdmin', {
        templateUrl: 'views/menuAdmin.html',
        controller: 'MenuAdminCtrl'
      })
      .when('/forum', {
        templateUrl: 'views/forum.html',
        controller: 'forumCtrl'
      })
      .when('/blog', {
        templateUrl: 'views/blog.html',
        controller: 'BlogCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });
    $locationProvider.html5Mode(true);
}])
/*
.config(function (uiTreeFilterSettingsProvider) {
    uiTreeFilterSettingsProvider.addresses = ['id', 'desc'];
    uiTreeFilterSettingsProvider.descendantCollection = "children";
})
*/
.run(['authService', function (authService) {
        authService.fillAuthData();
        //authService.logOut();
    }
]);

lightApp.service('signinService', function() {
    //this is used to parse the profile
    this.base64Decode = function(str) {
        var output = str.replace('-', '+').replace('_', '/');
        switch (output.length % 4) {
            case 0:
                break;
            case 2:
                output += '==';
                break;
            case 3:
                output += '=';
                break;
            default:
                throw 'Illegal base64url string!';
        }
        return window.atob(output); //polifyll https://github.com/davidchambers/Base64.js
    };
});
