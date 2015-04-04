angular.module('lightApp')
.directive('menu', function () {
    return {
        scope: {
            host: '@host',
            header: '@header'
        },
        restrict:'E',
        templateUrl: 'components/menu/menu.html',
        controller: 'menuCtrl',
        link: function(scope, elm, attr) {
        }
    };
});