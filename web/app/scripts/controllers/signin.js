/**
 * Created by steve on 25/06/14.
 */
'use strict';

angular.module('lightApp').controller('signinCtrl', ['$rootScope', '$scope', '$http', '$window', '$location', 'base64', 'toaster', function($rootScope, $scope, $http, $window, $location, base64, toaster) {
    $scope.message = '';
    $scope.error = false;

    var getFormPost = {
        category : 'form',
        name : 'getForm',
        readOnly: true,
        data : {
            id : 'com.networknt.light.user.signin'
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
            $scope.action[0].data = $scope.modelData;
            $http.post('/api/rs', $scope.action[0])
                .success(function (data, status, headers, config) {
                    $rootScope.global.isLogin = true;
                    $window.sessionStorage.token = data;
                    $rootScope.currentUser = JSON.parse(base64.base64Decode(data.split('.')[1])).user;
                    if(angular.isDefined($scope.action[0].success)) {
                        $location.path($scope.action[0].success);
                    }
                    //console.log($rootScope.currentUser);
                    //console.log(data);
                    //console.log(status);
                    //console.log(headers);
                    //console.log(config);

                })
                .error(function (data, status, headers, config) {
                    delete $window.sessionStorage.token;
                    $rootScope.global.isLogin = false;
                    if(angular.isDefined($scope.action[0].error)) {
                        $location.path($scope.action[0].error);
                    }
                    toaster.pop('error', status, data, 5000);
                    //console.log(data);
                    //console.log(status);
                    //console.log(headers);
                    //console.log(config);
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
