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

        var getFeedPost = {
            host: 'injector',
            app: 'main',
            category : 'feed',
            name : 'getFeed',
            readOnly: true,
            data : {}
        };

        $scope.page = { maxSize: 5, currentPage: 1, numPerPage: 10, totalItems: 20, numPages: 0 }
        $scope.feeds = [];

        $http.post('api/rs', getFeedPost)
            .success(function(result, status, headers, config) {
                console.log(result);
                $scope.feeds = result.data;

                $scope.page.totalItems = $scope.feeds.length;
                console.log($scope.page.totalItems);
                $scope.page.numPages = function () {
                    return Math.ceil($scope.feeds.length / $scope.page.numPerPage);
                };

                console.log($scope.feeds);

            }).error(function(data, status, headers, config) {

            }
        );


        $scope.pageChanged = function() {
            console.log('Page changed to: ' + $scope.page.currentPage);
            var begin = (($scope.page.currentPage - 1) * $scope.page.numPerPage)
                , end = begin + $scope.page.numPerPage;
            $scope.filteredFeeds = $scope.feeds.slice(begin, end);
        };

    }]);
