'use strict';

angular.module('lightApp')
    .controller('BlogCtrl', function ($scope, $http, $location, toaster, modelDataService) {

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
        $scope.displayMode = 'Summary';
        $scope.page = { maxSize: 5, totalItems: 0, numPages: 0 };
        $scope.blogs = [];

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
                    console.log($scope.blogs);
                    console.log($scope.page.totalItems);
                    $scope.page.numPages = Math.ceil($scope.page.totalItems / $scope.getBlog.data.pageSize);
                    //console.log($scope.page.numPages);
                }).error(function (data, status, headers, config) {
                    console.log("enter error!!!");
                }
            );
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

        $scope.toggleDisplayMode = function () {
            if($scope.displayMode === 'Summary') {
                $scope.displayMode = 'Detail';
            } else {
                $scope.displayMode = 'Summary';
            }
        };

        $scope.isDetailMode = function () {
            return $scope.displayMode === 'Detail';
        };

        //manually select a page to trigger an ajax request to populate the grid on page load
        $scope.pageChanged();

    });
