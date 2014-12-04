'use strict';

angular.module('lightApp').controller('MenuCtrl', ['$rootScope', '$scope', '$http', function($rootScope, $scope, $http) {
    $scope.menuSettings = {isCollapsed : true};
    $scope.tree = [];

    var getMenuPost = {
        category : 'menu',
        name : 'getMenu',
        readOnly: true,
        data : {
            host : 'injector'
        }
    };

    $http.post('api/rs', getMenuPost)
        .success(function(result, status, headers, config) {
            $scope.tree = result.menuItems;
            //console.log($scope.tree);
        })
        .error(function(data, status, headers, config) {
        }
    );

    $scope.toggleCollapsed = function () {
        $scope.menuSettings.isCollapsed =  !$scope.menuSettings.isCollapsed;
    };

    $scope.hasAccess = function(item) {
        //console.log($rootScope.currentUser.roles);
        //console.log(item);
        for (var i = 0; i < $rootScope.currentUser.roles.length; i++) {
            for (var j = 0; j < item.roles.length; j++) {
                if ($rootScope.currentUser.roles[i] == item.roles[j]) {
                    return true;
                }
            }
        }
        return false;
    };
}]);
