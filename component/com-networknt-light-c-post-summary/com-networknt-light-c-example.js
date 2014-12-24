angular.module("lightApp").run(["$templateCache", function($templateCache) {$templateCache.put("tpl/my-customer.html","Name: {{customer.name}} Address: {{customer.address}}");}]);
(function(angular) {
    'use strict';
    angular.module('lightApp')
        .directive('myCustomer', function() {
            return {
                templateUrl: 'tpl/my-customer.html'
            };
        });
})(window.angular);
