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
    'hc.marked',
    'toaster',
    'schemaForm-marked'
])
.config(['$httpProvider',
    function ($httpProvider) {
        // global error handling and authentication
        $httpProvider.interceptors.push(function ($rootScope, $q, $window, toaster) {
            return {
                request: function (config) {
                    config.headers = config.headers || {};
                    if ($window.sessionStorage.token) {
                        config.headers.Authorization = 'Bearer ' + $window.sessionStorage.token;
                    }
                    return config;
                },
                /*
                response: function (res) {
                    var error, data = res;
                    if (angular.isObject(data)) {
                        error = !data.ack && data.error;
                    }
                    if (error) {
                        toaster.pop('error', 'data', error.message + " : " + error.name, 5000);
                        console.log(error.message + " : " + error.name);
                        return $q.reject(data);
                    } else {
                        return res;
                    }
                },
                */
                responseError: function (res) {
                    console.log(res);
                    console.log(res.data);
                    console.log(res.status);
                    if (res.status === 401) {
                        if(res.data && res.data === 'token_expired') {
                            // token expired
                            console.log('logout() is called');
                            $rootScope.logout();
                            // refresh the token
                            var deferred = $q.defer(); // defer until we can re-request a new token
                            // Get a new token... (cannot inject $http directly as will cause a circular ref)
                            $injector.get("$http").jsonp('/some/endpoint/that/reissues/tokens?cb=JSON_CALLBACK').then(function(loginResponse) {
                                if (loginResponse.data) {
                                    $rootScope.oauth = loginResponse.data.oauth; // we have a new oauth token - set at $rootScope
                                    // now let's retry the original request - transformRequest in .run() below will add the new OAuth token
                                    $injector.get("$http")(response.config).then(function(response) {
                                        // we have a successful response - resolve it using deferred
                                        deferred.resolve(response);
                                    },function(response) {
                                        deferred.reject(); // something went wrong
                                    });
                                } else {
                                    deferred.reject(); // login.json didn't give us data
                                }
                            }, function(response) {
                                deferred.reject(); // token retry failed, redirect so user can login again
                                $location.path('/user/sign/in');
                                return;
                            });
                            return deferred.promise; // return the deferred promise

                        } else {
                            // TODO access some protected pages, redirect to login page.
                            $location.path('/signin');
                        }
                        // handle the case where the user is not authenticated
                    }
                    toaster.pop('error', res.status, res.data, 5000);
                    return $q.reject(res);
                }
            };
        });
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
        controller: 'MainCtrl'
      })
      .when('/signin', {
        templateUrl: 'templates/form.html',
        controller: 'signinCtrl'
      })
      .when('/form/:id', {
        templateUrl: 'templates/form.html',
        controller: 'FormCtrl'
      })
      .when('/page/:id', {
        templateUrl: 'templates/page.html',
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
      .when('/contact', {
        templateUrl: 'views/contact.html',
        controller: 'ContactCtrl'
      })
      .when('/blog', {
        templateUrl: 'templates/blog.html',
        controller: "BlogCtrl"
      })
      .otherwise({
        redirectTo: '/'
      });
    $locationProvider.html5Mode(true);
}])
.run(['$rootScope', '$location', '$window', function ($rootScope, $location, $window) {
        $rootScope.global = {
            isLogin: false,
            profile: {},
            info: {}
        }

        $rootScope.currentUser = { userId: '', roles: ['anonymous']};

        $rootScope.logout = function () {
            // TODO call the server to logout
            $rootScope.global.isLogin = false;
            delete $window.sessionStorage.token;
            $location.path('/');
        };


        function init() {
            // check if you have a token and it is expired. if token is not expired, set timeout to refresh it
            // if already expired, refresh it.
            if ($window.sessionStorage.token) {

            }


                // TODO check if token is expired, if yes, remove it from the local storage.
            // set isLogin to false;
            // if token is still valid, then set timeout to refresh it if possible.
            // for now, just remove the token to force user to login again.
            //$rootScope.logout();

            // TODO get index page

            /*
            restAPI.index.get({}, function (data) {
                app.timeOffset = Date.now() - data.timestamp;
                data = data.data;
                data.title2 = data.description;
                data.info.angularjs = angular.version.full.replace(/\-build.*$/, '');
                app.union(global, data);
                app.version = global.info.version || '';
                app.upyun = global.user && global.user.upyun;
                app.checkUser();
            });
            */
        }

        init();
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
