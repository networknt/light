'use strict';
angular.module('lightApp')
    .controller('FormAdminCtrl', ['$scope', '$http', function ($scope, $http) {
        var getAllFormPost = {
            category : 'form',
            name : 'getAllForm',
            readOnly: true,
            createUser: "NA"
        };

        var getFormPost = {
            category : 'form',
            name : 'getForm',
            readOnly: true,
            createUser: "NA"
        };

        var updFormPost = {
            category : 'form',
            name : 'updForm',
            readOnly: false,
            createUser: "NA"
        };

        $scope.forms = [];
        $http.post('api/rs', getAllFormPost)
            .success(function(result, status, headers, config) {
                $scope.forms = result;
                console.log($scope.forms);
                console.log($scope.forms[0]);
            }).error(function(data, status, headers, config) {
                alert("failure");
            }
        );
        $scope.selectedForm = $scope.forms[0];
        $scope.decorator = 'bootstrap-decorator';

        $scope.$watch('selectedForm',function(val){
            if (val) {
                getFormPost.data = {id : val};
                $http.post('api/rs', getFormPost)
                    .success(function(result, status, headers, config) {
                        $scope.schema = result.schema;
                        $scope.form = result.form;
                        $scope.action = result.action;
                        $scope.schemaJson = JSON.stringify($scope.schema, undefined, 2);
                        $scope.formJson = JSON.stringify($scope.form, undefined, 2);
                        $scope.actionJson = JSON.stringify($scope.action, undefined, 2);
                        $scope.modelData = result.model || {};
                    }).error(function(result, status, headers, config) {
                        alert("failure");
                    }
                );
            }
        });


        $scope.itParsesSchema = true;
        $scope.itParsesForm = true;
        $scope.itParsesAction = true;

        $scope.$watch('schemaJson',function(val,old){
            if (val && val !== old) {
                try {
                    $scope.schema = JSON.parse($scope.schemaJson);
                    $scope.itParsesSchema = true;
                } catch (e){
                    $scope.itParsesSchema = false;
                }
            }
        });

        $scope.$watch('formJson',function(val,old){
            if (val && val !== old) {
                try {
                    $scope.form = JSON.parse($scope.formJson);
                    $scope.itParsesForm = true;
                } catch (e){
                    $scope.itParsesForm = false;
                }
            }
        });

        $scope.$watch('actionJson',function(val,old){
            if (val && val !== old) {
                try {
                    $scope.action = JSON.parse($scope.actionJson);
                    $scope.itParsesAction = true;
                } catch (e){
                    $scope.itParsesAction = false;
                }
            }
        });

        $scope.pretty = function(){
            return JSON.stringify($scope.modelData,undefined,2,2);
        };

        $scope.log = function(msg){
            console.log('Simon says',msg);
        };

        $scope.sayNo = function() {
            window.alert('Noooooooo');
        };

        $scope.say = function(msg) {
            window.alert(msg);
        };

        $scope.submitForm = function(form, model) {
            // First we broadcast an event so all fields validate themselves
            console.log(model);
            $scope.$broadcast('schemaFormValidate');
            // Then we check if the form is valid
            if (form.$valid) {
                // submit the form


            }
        };

        $scope.submitSchema = function() {
            console.log('submitSchema is called');
            console.log($scope.schema);
            console.log($scope.form);

            updFormPost.data = {
                id: $scope.selectedForm,
                version: $scope.data.version,
                schema: $scope.schema,
                form: $scope.form,
                action: $scope.action
            }
            $http.post('api/rs', updFormPost)
                .success(function(result, status, headers, config) {
                    console.log(status);
                }).error(function(data, status, headers, config) {
                    alert("failure");
                }
            );
        };

        $scope.saveModel = function() {
            console.log('saveModel is called');
        };
    }]);
