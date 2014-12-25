'use strict';

/*
 This is a generic page controller. It handles pages without special handling.
 */
angular.module('lightApp').controller('pageCtrl', ['$scope', '$routeParams', '$http', 'toaster', 'modelDataService', function($scope, $routeParams, $http, toaster, modelDataService) {

    var getPagePost = {
        category : 'page',
        name : 'getPage',
        readOnly: true,
        data : {
            id : $routeParams.id
        }
    };

    $http.post('api/rs', getPagePost)
        .success(function(result, status, headers, config) {
            $scope.html = result.content;
            //console.log($scope.html);
        })

}]);
