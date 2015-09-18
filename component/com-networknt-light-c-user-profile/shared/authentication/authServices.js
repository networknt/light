'use strict';
angular.module('lightApp')
.factory('authInterceptorService', ['$q', '$injector','$location', 'httpBuffer', 'localStorageService','toaster', function ($q, $injector, $location, httpBuffer, localStorageService, toaster) {
    var authInterceptorServiceFactory = {};
    var $http;

    var _request = function (config) {
        config.headers = config.headers || {};
        var authorizationData = localStorageService.get('authorizationData');
        //console.log('config', config);
        // TODO Do not put access token into header of refresh token post. In this case,
        // we don't need to remove the authorizationData before sending refresh token post.
        // chances are some other requests might be sent during the time slot and got login
        // is required error and forced to login page.
        // TODO I really don't like this checking. need to find another way? backend?
        if (authorizationData) {
            if(angular.isDefined(config.data) && angular.isDefined(config.data.name) && config.data.name === 'refreshToken') {
                return config;
            }
            config.headers.Authorization = 'Bearer ' + authorizationData.token;
        }
        return config;
    }

    var _responseError = function (rejection) {
        console.log("responseError: rejection", rejection);
        var deferred = $q.defer();
        if (rejection.status === 401) {
            var authService = $injector.get('authService');
            if(rejection.data.error === 'token_expired') {
                console.log("token expired, renewing...")
                httpBuffer.append(rejection.config, deferred);
                console.log("rejection and deferred are added to httpBuffer", rejection, deferred);
                authService.refreshToken().then(function (response) {
                    console.log("_responseError: successfully called refreshToken");
                    // the updater function will put the renewed token into header of saved requests.
                    httpBuffer.retryAll(function(config) {
                        config.headers = config.headers || {};
                        var authorizationData = localStorageService.get('authorizationData');
                        if (authorizationData) {
                            config.headers.Authorization = 'Bearer ' + authorizationData.token;
                        }
                        return config;
                    });
                }, function () {
                    // failed to get refresh token somehow. Maybe didn't check remember me. go to login page.
                    toaster.pop('error', rejection.status, rejection.data, 5000);
                    console.log("_responseError failed to get refresh token. Maybe didn't check remember me");
                    // abandon all the saved requests
                    httpBuffer.rejectAll();
                    httpBuffer.saveAttemptUrl();
                    authService.logOut();
                    $location.path('/signin');
                    deferred.reject(rejection);
                });
            } else {
                // 401 but not token expired the user is not logged in yet.
                toaster.pop('error', rejection.status, rejection.data, 5000);
                httpBuffer.rejectAll();
                httpBuffer.saveAttemptUrl();
                $location.path('/signin');
                deferred.reject(rejection);
            }
        } else if (rejection.status === 403) {
            // 403 forbidden. The user is logged in but doesn't have permission for the request.
            // logout and redirect to login page.
            toaster.pop('error', rejection.status, rejection.data, 5000);
            httpBuffer.rejectAll();
            httpBuffer.saveAttemptUrl();
            authService.logOut();
            $location.path('/signin');
            deferred.reject(rejection);
        } else if (rejection.status === 404) {
            // 404 not found. don't do anything here just let it go. as the controller might try
            // some other ways to get the resource. eg. PageCtrl load from file system first and
            // then try REST API second for development.
            deferred.reject(rejection);
        } else {
            // some other errors, reject immediately.
            toaster.pop('error', rejection.status, rejection.data, 5000);
            deferred.reject(rejection);
        }
        return deferred.promise;
    }

    authInterceptorServiceFactory.request = _request;
    authInterceptorServiceFactory.responseError = _responseError;

    return authInterceptorServiceFactory;
}])
.factory('authService', ['$q', '$injector', 'localStorageService', 'base64', 'CLIENT', function ($q, $injector, localStorageService, base64, CLIENT) {

    var $http;
    var authServiceFactory = {};

    var _authentication = {
        isAuth: false,
        useRefreshTokens: false,
        currentUser: { userId: '', roles: ['anonymous']}
    };

    var _logOut = function () {
        localStorageService.remove('authorizationData');
        _authentication.isAuth = false;
        _authentication.currentUser = { userId: '', roles: ['anonymous']};
        _authentication.useRefreshTokens = false;
    };

    var _fillAuthData = function () {
        var authorizationData = localStorageService.get('authorizationData');
        console.log('_fillAuthData:authorizationData', authorizationData);
        if (authorizationData) {
            _authentication.isAuth = true;
            _authentication.useRefreshTokens = authorizationData.useRefreshTokens;
            _authentication.currentUser = authorizationData.currentUser;
        }
    };

    var _getRefreshToken = function () {
        var authorizationData = localStorageService.get('authorizationData');
        return authorizationData.refreshToken;
    }

    var _refreshToken = function ()
    {
        var deferred = $q.defer();

        var refreshTokenPost = {
            category : 'user',
            name : 'refreshToken',
            readOnly: true
        };

        var authorizationData = localStorageService.get('authorizationData');
        console.log("authService:_refreshToken:authorizationData before refresh", authorizationData);
        if (authorizationData && authorizationData.useRefreshTokens) {
            refreshTokenPost.data = {refreshToken : authorizationData.refreshToken, userId: authorizationData.currentUser.userId, clientId: CLIENT.clientId};
            // The authorizationData must be removed before calling refreshToken api as the old expired token will be sent again
            // and cause infinite loop. Once it is removed, not access token will be sent to the server along with the request.
            // TODO but we have another issue that some other requests might be sent during this time slot. It is better to check
            // refresh token command in the request intercpetor.
            //localStorageService.remove('authorizationData');
            $http = $http || $injector.get('$http');
            $http.post('/api/rs', refreshTokenPost).success(function (response) {
                _authentication.isAuth = true;
                _authentication.currentUser = JSON.parse(base64.base64Decode(response.accessToken.split('.')[1])).user;
                _authentication.useRefreuseshTokens = true;
                authorizationData.token = response.accessToken; // only access token is replaced.
                localStorageService.set('authorizationData', authorizationData);
                console.log("authService:_refreshToken:authrizationData after refresh", authorizationData);
                deferred.resolve(response);
            }).error(function (err, status) {
                _logOut();
                deferred.reject(err);
            });
        } else {
            console.log("not use refresh token.");
            deferred.reject();
        }
        return deferred.promise;
    };

    authServiceFactory.logOut = _logOut;
    authServiceFactory.fillAuthData = _fillAuthData;
    authServiceFactory.authentication = _authentication;
    authServiceFactory.refreshToken = _refreshToken;
    authServiceFactory.getRefreshToken = _getRefreshToken;

    return authServiceFactory;
}]);
