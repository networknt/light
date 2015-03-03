'use strict';

angular.module('lightApp').controller('menuCtrl', ['$scope', '$http', 'authService', function($scope, $http, authService) {
    $scope.menuSettings = {isCollapsed : true};
    $scope.tree = [];

    var getMenuPost = {
        category : 'menu',
        name : 'getMenu',
        readOnly: true,
        data : {
            host : 'example'
        }
    };

    $http.post('api/rs', getMenuPost)
        .success(function(result, status, headers, config) {
            $scope.tree = result.out_Own;

        });

    $scope.toggleCollapsed = function () {
        $scope.menuSettings.isCollapsed =  !$scope.menuSettings.isCollapsed;
    };

    $scope.hasAccess = function(item) {
        //console.log('item = ', item);
        //console.log('currentUser.roles', authService.authentication.currentUser.roles);
        //console.log('itme.roles', item.roles);
        for (var i = 0; i < authService.authentication.currentUser.roles.length; i++) {
            for (var j = 0; j < item.roles.length; j++) {
                if (authService.authentication.currentUser.roles[i] == item.roles[j]) {
                    return true;
                }
            }
        }
        return false;
    };

    $scope.logOut = function () {
        authService.logOut();

    };

}]);
