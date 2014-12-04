'use strict';

/**
 * @ngdoc function
 * @name lightApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the lightApp
 */
angular.module('lightApp')
    .controller('MainCtrl', function ($scope, $http, $location, toaster, modelDataService) {

        $scope.getFeed = {
            host: 'injector',
            app: 'main',
            category : 'feed',
            name : 'getFeed',
            readOnly: true,

            //default criteria that will be sent to the server
            data : {
                pageSize: 10,
                pageNo: 1,
                sortDir: 'asc',
                sortedBy: 'requestId'
            }
        };
        $scope.displayMode = 'Summary';
        $scope.selectedAll = false;
        $scope.page = { maxSize: 5, totalItems: 0, numPages: 0 };
        $scope.feeds = [];
        $scope.dataFeedTypes = [];
        $scope.createUserIds = [];
        $scope.updateUserIds = [];
        $scope.processTypeCds = [];
        $scope.processSubtypeCds = [];

        $scope.headers = [
            {
                title: 'Check All',
                value: 'checkAll'
            },
            {
                title: 'RequestId',
                value: 'requestId'
            },
            {
                title: 'DataFeedType',
                value: 'dataFeedType'
            },
            {
                title: 'CreateUserId',
                value: 'createUserId'
            },
            {
                title: 'UpdateUserId',
                value: 'updateUserId'
            },
            {
                title: 'ProcessTypeCd',
                value: 'processTypeCd'
            },
            {
                title: 'ProcessSubtypeCd',
                value: 'processSubtypeCd'
            }];

        $scope.inject = function(index) {
            console.log(index);
            if(angular.isDefined(index)) {
                var feed = $scope.feeds[index];
                // remove loanNumber so that the system will generate a new one. user can override of cause.
                delete feed.loanNumber;
                modelDataService.setModelData(feed);
            }
            // TODO resolve url by dataFeedType
            $location.path("/form/com.cibc.rop.class.feed");
        };

        //The function that is responsible of fetching the result from the server and setting the grid to the new result
        $scope.fetchResult = function () {
            $http.post('api/rs', $scope.getFeed)
                .success(function (result, status, headers, config) {
                    $scope.feeds = result.feeds;
                    $scope.dataFeedTypes = result.dataFeedTypes;
                    $scope.createUserIds = result.createUserIds;
                    $scope.updateUserIds = result.updateUserIds;
                    $scope.processTypeCds = result.processTypeCds;
                    $scope.processSubtypeCds = result.processSubtypeCds;
                    $scope.page.totalItems = result.total;
                    console.log($scope.feeds);
                    console.log($scope.page.totalItems);
                    $scope.page.numPages = Math.ceil($scope.page.totalItems / $scope.getFeed.data.pageSize);
                    //console.log($scope.page.numPages);
                }).error(function (data, status, headers, config) {
                    console.log("enter error!!!");
                }
            );
        };

        //call back function that we passed to our custom directive sortBy, will be called when clicking on any field to sort
        $scope.onSort = function (sortedBy, sortDir) {
            $scope.getFeed.data.sortDir = sortDir;
            $scope.getFeed.data.sortedBy = sortedBy;
            $scope.getFeed.data.pageNo = 1;
            $scope.fetchResult();
        };

        //Will be called when filtering the grid, will reset the page number to one
        $scope.filterResult = function () {
            $scope.getFeed.data.pageNo = 1;
            $scope.fetchResult();
        };

        $scope.pageChanged = function() {
            $scope.fetchResult();
        };

        // will pick up environment and inject all of them.
        $scope.injectAll = function() {
            var feedsToInject = [];
            angular.forEach($scope.feeds, function(feed) {
                if(feed.selected) {
                    // remove loanNumber so that the system will generate a new one. user can override of cause.
                    delete feed.loanNumber;
                    feedsToInject.push(feed);
                }
            });
            if(feedsToInject.length > 0) {
                modelDataService.setModelData({feeds: JSON.stringify(feedsToInject, null, '\t')});
                $location.path("/form/com.cibc.rop.feeds");
            } else {
                toaster.pop('error', "data", "Nothing is selected to inject!", 5000);
            }
        };

        $scope.checkAll = function () {
            $scope.selectedAll = !$scope.selectedAll;
            angular.forEach($scope.feeds, function(feed) {
                feed.selected = $scope.selectedAll;
            })
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
