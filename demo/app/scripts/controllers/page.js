'use strict';

/**
 * This is a generic page controller. It handles pages without special handling.
 * Pages are always loaded from file system first from /src folder, if it cannot be found then they are
 * loaded from the database by REST API calls. This is the dev environment and production is always loaded
 * from REST API.
 */
angular.module('lightApp').controller('pageCtrl', ['$scope', '$routeParams', '$http', 'toaster', 'modelDataService', function($scope, $routeParams, $http, toaster, modelDataService) {

    var getPagePost = {
        category : 'page',
        name : 'getPage',
        readOnly: true,
        data : {
            pageId : $routeParams.pageId
        }
    };

    $http.post('api/rs', getPagePost)
        .success(function(result, status, headers, config) {
            $scope.html = result.content;
            //console.log($scope.html);
        })

}]);
