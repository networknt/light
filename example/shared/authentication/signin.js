/**
 * Created by steve on 25/06/14.
 */
'use strict';

angular.module('lightApp').controller('signinCtrl', ['$rootScope', '$scope', '$http', '$location', 'localStorageService', 'base64', 'authService', 'httpBuffer', 'toaster', 'CLIENT', function($rootScope, $scope, $http, $location, localStorageService, base64, authService, httpBuffer, toaster, CLIENT) {
    $scope.message = '';
    $scope.error = false;

    var getFormPost = {
        category : 'form',
        name : 'getForm',
        readOnly: true,
        data : {
            formId : 'com.networknt.light.user.signin'
        }
    };

    $http.post('api/rs', getFormPost)
        .success(function(result, status, headers, config) {
            var data = result;
            $scope.schema = data.schema;
            $scope.form = data.form;
            $scope.action = data.action;
            $scope.schemaJson = JSON.stringify($scope.schema, undefined, 2);
            $scope.formJson = JSON.stringify($scope.form, undefined, 2);
            $scope.modelData = data.model || {};
            $scope.decorator = 'bootstrap-decorator';
        }).error(function(data, status, headers, config) {
            $scope.error = true;
            $scope.message = data;
        }
    );

    $scope.submitForm = function(form, model) {
        // First we broadcast an event so all fields validate themselves
        $scope.$broadcast('schemaFormValidate');
        // Then we check if the form is valid
        if (form.$valid) {
            // inject client id into the modelData
            $scope.modelData.clientId = CLIENT.clientId;
            $scope.action[0].data = $scope.modelData;
            console.log('modelData = ', $scope.modelData);
            $http.post('/api/rs', $scope.action[0])
                .success(function (data, status, headers, config) {
                    console.log('data', data);
                    // Successfully logged in and get access token back. If remember me is checked, then a refresh token is returned as well.
                    authService.authentication.isAuth = true;
                    // Parse the Json Token and get uesr object which contains userId and roles.
                    authService.authentication.currentUser = JSON.parse(base64.base64Decode(data.accessToken.split('.')[1])).user;
                    // This is an indicator if refresh token will be used to get another access token after access token is expired.
                    authService.authentication.useRefreshTokens = $scope.modelData.rememberMe;
                    // Save authorizationData object into local storage so it can last longer than the browser session. local storage will
                    // fall back to Cookie if HTML5 is not supported by the browser.
                    localStorageService.set('authorizationData', { token: data.accessToken, currentUser: authService.authentication.currentUser, refreshToken: data.refreshToken || '', useRefreshTokens: authService.authentication.useRefreshTokens  });
                    // Redirect to the attempted url if the login page was redirected upon 401 and 403 error.
                    httpBuffer.redirectToAttemptedUrl();
                })
                .error(function (data, status, headers, config) {
                    // Clean the authorizatonData from local storage.
                    authService.logOut();
                    // Clean all the saved requests in httpBuffer if this is redirected from failure of refreshing token.
                    httpBuffer.rejectAll();
                    if(angular.isDefined($scope.action[0].error)) {
                        $location.path($scope.action[0].error);
                    }
                    toaster.pop('error', status, data, 5000);
                });
        }
    };


    $scope.callRestricted = function () {
        $http({url: '/api/restricted', method: 'GET'})
            .success(function (data, status, headers, config) {
                $scope.message = $scope.message + ' ' + data.name; // Should log 'foo'
                console.log(data);
                console.log(status);
                console.log(headers);
                console.log(config);
            })
            .error(function (data, status, headers, config) {
                console.log(data);
                console.log(status);
                console.log(headers);
                console.log(config);

                window.alert(data);
            });
    };

    $scope.awesomeThings = [1,2, 3];
}]);
