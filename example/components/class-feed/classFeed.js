'use strict';

angular.module('lightApp').controller('ClassFeedCtrl', ['$scope', '$http', function($scope, $http) {
    var getFormPost = {
        category : 'form',
        name : 'getForm',
        readOnly: true,
        data : {
            id : 'com.cibc.rop.class.feed'
        }
    };

    var submitPost = {
        host: 'injector',
        app: 'main',
        category : 'feed',
        name : 'injClassFeed',
        readOnly: false,
        createUser: "NA"
    };

    $http.post('api/rs', getFormPost)
        .success(function(result, status, headers, config) {
            var data = result.data;
            $scope.schema = data.schema;
            $scope.form = data.form;
            $scope.schemaJson = JSON.stringify($scope.schema, undefined, 2);
            $scope.formJson = JSON.stringify($scope.form, undefined, 2);
            $scope.modelData = data.model || {};
            $scope.decorator = 'bootstrap-decorator';
            console.log($scope.schema);
        }).error(function(data, status, headers, config) {
            alert("failure");
        }
    );

    $scope.submitForm = function(form, model) {
        // First we broadcast an event so all fields validate themselves
        $scope.$broadcast('schemaFormValidate');
        // Then we check if the form is valid
        if (form.$valid) {
            submitPost.data = $scope.modelData;
            $http.post('/api/rs', submitPost)
                .success(function (data, status, headers, config) {
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
                });
        }
    };

}]);
