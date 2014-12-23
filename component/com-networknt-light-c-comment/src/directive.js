(function(angular) {
    'use strict';
    angular.module('lightApp')
        .directive('myCustomer', function() {
            return {
                templateUrl: 'tpl/my-customer.html'
            };
        });
})(window.angular);
