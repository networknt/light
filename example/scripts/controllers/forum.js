'use strict';

angular.module('lightApp').controller('forumCtrl', ['$scope', '$http', '$location', '$filter', 'toaster', 'modelDataService', function ($scope, $http, $location, $filter, toaster, modelDataService) {
    $scope.getForum = {
        category : 'forum',
        name : 'getForumTree',
        readOnly: true
    };

    $scope.fetchResult = function () {
        $http.post('api/rs', $scope.getForum)
            .success(function (result, status, headers, config) {
                $scope.forums = result;
                console.log($scope.forums);
            })
    };
    $scope.fetchResult();

    $scope.toggle = function(scope) {
        scope.toggle();
    };

    var getRootNodesScope = function() {
        return angular.element(document.getElementById("tree-root")).scope();
    };

    $scope.collapseAll = function() {
        var scope = getRootNodesScope();
        scope.collapseAll();
    };

    $scope.expandAll = function() {
        var scope = getRootNodesScope();
        scope.expandAll();
    };

    $scope.treeFilter = $filter('uiTreeFilter');

    $scope.supportedFields = ['id', 'desc'];

    $scope.gotoForum = function(id) {
        modelDataService.setModelData(id);
        $location.path("/page/com.networknt.light.forum.post");
    }

}]);
