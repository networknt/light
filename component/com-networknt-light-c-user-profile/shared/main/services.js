'use strict';

angular.module('lightApp')
.constant('CLIENT', {
    'clientId': 'example@Browser'
})
.factory('base64', function() {
    return {
        // This is used to parse the profile.
        base64Decode: function(str) {
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
        }
    };
})
.service('modelDataService', function() {
    var modelData = null;
    return {
        setModelData : function (data) {
            modelData = data;
        },
        getModelData : function () {
            return modelData;
        }
    };
})
.factory('httpBuffer', ['$injector', function($injector) {
    /** Holds all the requests, so they can be re-requested in future. */
    var buffer = [];
    var attemptUrl = '';

    /** Service initialized later because of circular dependency problem. */
    var $http;
    var $location;

    function retryHttpRequest(config, deferred) {
        console.log("httpBuffer:retryHttpRequest config", config);
        function successCallback(response) {
            deferred.resolve(response);
        }
        function errorCallback(response) {
            deferred.reject(response);
        }
        $http = $http || $injector.get('$http');
        $http(config).then(successCallback, errorCallback);
    }

    return {
        /**
         * Appends HTTP request configuration object with deferred response attached to buffer.
         */
        append: function(config, deferred) {
            console.log("httpBuffer.append is called", config, deferred);
            buffer.push({
                config: config,
                deferred: deferred
            });
        },

        /**
         * Abandon or reject (if reason provided) all the buffered requests.
         */
        rejectAll: function(reason) {
            console.log("httpBuffer.rejectAll is called", reason);
            if (reason) {
                for (var i = 0; i < buffer.length; ++i) {
                    buffer[i].deferred.reject(reason);
                }
            }
            buffer = [];
        },

        /**
         * Retries all the buffered requests clears the buffer.
         */
        retryAll: function(updater) {
            for (var i = 0; i < buffer.length; ++i) {
                console.log("httpBuffer.retryAll is called");
                retryHttpRequest(updater(buffer[i].config), buffer[i].deferred);
            }
            buffer = [];
        },

        saveAttemptUrl: function() {
            $location = $location || $injector.get('$location');
            if($location.path().toLowerCase() != '/signin') {
                attemptUrl = $location.path();
                console.log("attemptUrl = {}", attemptUrl);
            } else {
                attemptUrl = '/page/com-networknt-light-v-user-home';
                console.log("attemptUrl = {}", attemptUrl);
            }
        },

        redirectToAttemptedUrl: function() {
            $location = $location || $injector.get('$location');
            $location.path(attemptUrl);
        }
    };
}])

.factory('applyFn', ['$rootScope',
    function ($rootScope) {
        return function (fn, scope) {
            fn = angular.isFunction(fn) ? fn : angular.noop;
            scope = scope && scope.$apply ? scope : $rootScope;
            fn();
            if (!scope.$$phase) {
                scope.$apply();
            }
        };
    }
])
.factory('timing', ['$rootScope', '$q', '$exceptionHandler',
    function ($rootScope, $q, $exceptionHandler) {
        function timing(fn, delay, times) {
            var timingId, count = 0,
                defer = $q.defer(),
                promise = defer.promise;

            fn = angular.isFunction(fn) ? fn : angular.noop;
            delay = parseInt(delay, 10);
            times = parseInt(times, 10);
            times = times >= 0 ? times : 0;
            timingId = window.setInterval(function () {
                count += 1;
                if (times && count >= times) {
                    window.clearInterval(timingId);
                    defer.resolve(fn(count, times, delay));
                } else {
                    try {
                        fn(count, times, delay);
                    } catch (e) {
                        defer.reject(e);
                        $exceptionHandler(e);
                    }
                }
                if (!$rootScope.$$phase) {
                    $rootScope.$apply();
                }
            }, delay);

            promise.$timingId = timingId;
            return promise;
        }
        timing.cancel = function (promise) {
            if (promise && promise.$timingId) {
                clearInterval(promise.$timingId);
                return true;
            } else {
                return false;
            }
        };
        return timing;
    }
]);