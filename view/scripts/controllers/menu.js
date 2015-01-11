'use strict';

angular.module('lightApp').controller('menuCtrl', ['$scope', '$http', 'authService', function($scope, $http, authService) {
    console.log("Now we are in menuCtrl");
    $scope.menuSettings = {isCollapsed : true};
    $scope.tree = [];

    var getMenuPost = {
        category : 'menu',
        name : 'getMenu',
        readOnly: true,
        data : {
            host : 'www.networknt.com'
        }
    };

    $http.post('api/rs', getMenuPost)
        .success(function(result, status, headers, config) {
            $scope.tree = result.menuItems;
            console.log("get menus", $scope.tree);
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
