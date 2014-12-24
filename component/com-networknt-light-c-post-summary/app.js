(function(angular) {
    'use strict';
    angular.module('lightApp', [])
        .controller('Controller', ['$scope', function($scope) {
            $scope.customer = {
                name: 'Naomi',
                address: '1600 Amphitheatre'
            };
        }])
})(window.angular);