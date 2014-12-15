'use strict';

/**
 * @ngdoc function
 * @name lightApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the lightApp
 */
angular.module('lightApp')
    .controller('mainCtrl', ['$scope', '$http', function ($scope, $http) {

        var getRecentPost = {
            category : 'post',
            name : 'getRecentPost',
            readOnly: true,
            data : {}
        };

        $scope.forumPosts = [];
        $scope.newsPosts = [];
        $scope.blogPosts = [];

        $http.post('api/rs', getRecentPost)
            .success(function(result, status, headers, config) {
                console.log(result);
                $scope.forumPosts = result.forumPosts;
                $scope.newsPosts = result.newsPosts;
                $scope.blogPosts = result.blogPosts;

            });

    }]);
