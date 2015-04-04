'use strict';

/*
This is a generic form controller. It handles forms without special handling.
 */
angular.module('lightApp').controller('formCtrl', ['$scope', '$routeParams', '$http', '$location', 'toaster', 'modelDataService', function($scope, $routeParams, $http, $location, toaster, modelDataService) {

    var getFormPost = {
        category : 'form',
        name : 'getForm',
        readOnly: true,
        data : {
            formId : $routeParams.formId,
            parentId: $routeParams.parentId
        }
    };

    console.log("getFormPost.data", getFormPost.data);

    $http.post('api/rs', getFormPost)
        .success(function(result, status, headers, config) {
            $scope.schema = result.schema;
            console.log('schema = ', $scope.schema);
            $scope.form = result.form;
            console.log('form = ', $scope.form);
            $scope.action = result.action;
            console.log('action = ', $scope.action);
            $scope.schemaJson = JSON.stringify($scope.schema, undefined, 2);
            $scope.formJson = JSON.stringify($scope.form, undefined, 2);
            $scope.modelData = result.model || modelDataService.getModelData() || {};
            modelDataService.setModelData(null); // reset the modelDataService variable.
            $scope.decorator = 'bootstrap-decorator';
        }).error(function(data, status, headers, config) {
            toaster.pop('error', status, data, 5000);
        }
    );

    $scope.setButtonIndex = function(index) {
        $scope.buttonIndex = index;
    }

    $scope.submitForm = function(form, model) {
        // First we broadcast an event so all fields validate themselves
        $scope.$broadcast('schemaFormValidate');
        // Then we check if the form is valid
        if (form.$valid) {
            $scope.action[$scope.buttonIndex].data = $scope.modelData;
            $scope.action[$scope.buttonIndex].data.parentId = $routeParams.parentId;
            $http.post('/api/rs', $scope.action[$scope.buttonIndex])
                .success(function (data, status, headers, config) {
                    if(angular.isDefined($scope.action[$scope.buttonIndex].success)) {
                        if(angular.isDefined($routeParams.parentId)) {
                            modelDataService.setModelData($routeParams.parentId);
                        }
                        $location.path($scope.action[$scope.buttonIndex].success);
                    }
                    toaster.pop('success', status, data);
                    //console.log(headers);
                    //console.log(config);
                })
                .error(function (data, status, headers, config) {
                    if(angular.isDefined($scope.action[$scope.buttonIndex].error)) {
                        $location.path($scope.action[$scope.buttonIndex].error);
                    }
                    toaster.pop('error', status, data, 5000);
                    //console.log(headers);
                    //console.log(config);
                });
        }
    };

}]);
