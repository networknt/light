/**
 * Created by steve on 25/06/14.
 */
'use strict';

angular.module('lightApp').controller('ContactCtrl', function($scope, $http) {
    $scope.message = '';

    var formUrl = 'data/contact.json';
    $http.get(formUrl).then(function (res) {
        $scope.schema = res.data.schema;
        $scope.form = res.data.form;
        $scope.schemaJson = JSON.stringify($scope.schema, undefined, 2);
        $scope.formJson = JSON.stringify($scope.form, undefined, 2);

        $scope.modelData = res.data.model || {};
    });
    $scope.decorator = 'bootstrap-decorator';

    $scope.submitForm = function(form, model) {
        console.log(model);
        // First we broadcast an event so all fields validate themselves
        $scope.$broadcast('schemaFormValidate');
        // Then we check if the form is valid
        if (form.$valid) {
            window.alert('You did it!');
            $http
                .post('api/rs/contact/add', model)
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


    $scope.awesomeThings = [1,2, 3];
});
