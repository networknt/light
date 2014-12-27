(function(angular) {
    'use strict';
    angular.module('lightApp')
        .controller('BlogCtrl', ['$scope', '$http', '$location', 'authService', 'toaster', 'modelDataService', function ($scope, $http, $location, authService, toaster, modelDataService) {

            $scope.getBlog = {
                category : 'blog',
                name : 'getBlog',
                readOnly: true,
                //default criteria that will be sent to the server
                data : {
                    pageSize: 10,
                    pageNo: 1,
                    sortDir: 'desc',
                    sortedBy: 'createdDate'
                }
            };
            $scope.page = { maxSize: 10, totalItems: 0, numPages: 0 };
            $scope.blogs = [];
            $scope.allowPost = false;

            $scope.post = function(index) {
                console.log(index);
                if(angular.isDefined(index)) {
                    var blog = $scope.blogs[index];
                    modelDataService.setModelData(blog);
                }
                $location.path("/form/com.networknt.light.blog.post");
            };

            //The function that is responsible of fetching the result from the server
            $scope.fetchResult = function () {
                $http.post('api/rs', $scope.getBlog)
                    .success(function (result, status, headers, config) {
                        $scope.blogs = result.blogs;
                        $scope.page.totalItems = result.total;
                        $scope.allowPost = result.allowPost;
                        console.log($scope.blogs);
                        console.log($scope.page.totalItems);
                        console.log($scope.allowPost);
                        $scope.page.numPages = Math.ceil($scope.page.totalItems / $scope.getBlog.data.pageSize);
                    })
            };

            //call back function that we passed to our custom directive sortBy, will be called when clicking on any field to sort
            $scope.onSort = function (sortedBy, sortDir) {
                $scope.getBlog.data.sortDir = sortDir;
                $scope.getBlog.data.sortedBy = sortedBy;
                $scope.getBlog.data.pageNo = 1;
                $scope.fetchResult();
            };

            //Will be called when filtering the grid, will reset the page number to one
            $scope.filterResult = function () {
                $scope.getBlog.data.pageNo = 1;
                $scope.fetchResult();
            };

            $scope.pageChanged = function() {
                $scope.fetchResult();
            };

            //manually select a page to trigger an ajax request to populate the grid on page load
            $scope.pageChanged();

        }]);
})(window.angular);
