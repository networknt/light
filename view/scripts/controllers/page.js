'use strict';

/**
 * This is a generic page controller. It handles pages without special handling.
 * Pages are always loaded from file system first from /src folder, if it cannot be found then they are
 * loaded from the database by REST API calls. This is the dev environment and production is always loaded
 * from REST API.
 */
angular.module('lightApp').controller('pageCtrl', ['$scope', '$routeParams', '$http', 'toaster', 'modelDataService', function($scope, $routeParams, $http, toaster, modelDataService) {

    console.log('id =', $routeParams.id);
    console.log('file =', '/' + $routeParams.id + '.html');

    var getPage = {
        category : 'page',
        name : 'getPage',
        readOnly: true,
        data : {
            id : $routeParams.id
        }
    };


    $scope.html = '';
    $http.get('/src/' + $routeParams.id + '.html').success (function(data){
        $scope.html = data;
    }).error(function() {
        console.log("Could not load file from src folder, try REST API...");
        $http.get('api/rs', {params: { cmd: encodeURIComponent(JSON.stringify(getPage))}})
            .success(function (result, status, headers, config) {
                $scope.html = result.content;
            })
    })

}]);
