(function(angular) {
    'use strict';
    angular.module('lightApp')
        .directive('myCustomer', function() {
            return {
                templateUrl: 'tpl/my-customer.html'
            };
        })
        .directive('comNetworkntLightPostSummary', function() {
            return {
                templateUrl: 'tpl/post-summary.html'
            };
        });

})(window.angular);
