'use strict';

angular.module('lightApp')
.controller('UserPublicProfileCtrl', ['$scope', '$routeParams', '$http', 'toaster', 'modelDataService', 'authService', function($scope, $routeParams, $http, toaster, modelDataService, authService) {

    $scope.getPublicUserInfo = {
        category : 'user',
        name : 'getUser',
        readOnly: true,
        data: {}
    };

    $scope.userInfo = null;

    $scope.fetchUserInfo = function () {
        var userId = $routeParams.id;
        if (userId != null && userId.length > 0) {
            $scope.getPublicUserInfo.data["userId"] = userId;
            $http.post('api/rs', $scope.getPublicUserInfo)
                .success(function(result, status, headers, config) {
                    console.log("getPublicUserInfo Success:", result);
                    $scope.userInfo = result;
                })
                .error(function(result, status, headers, config){
                    console.log("getPublicUserInfo Error:", result, "status:", status);
                });
        }
    };

    $scope.fetchUserInfo();
}]);
