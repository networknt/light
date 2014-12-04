/**
 * Created by steve on 10/08/14.
 */
'use strict';

/**
 * @ngdoc function
 * @name lightApp.controller:MenuAdminCtrl
 * @description
 * # MenuAdminCtrl
 * Controller of the lightApp
 */
angular.module('lightApp')
    .controller('MenuAdminCtrl', ['$scope', '$http', '$location', 'modelDataService', 'toaster', function ($scope, $http, $location, modelDataService, toaster) {
        $scope.getAllMenu = {
            category : 'menu',
            name : 'getAllMenu',
            readOnly: true
        };

        $scope.delMenu = {
            category : 'menu',
            name : 'delMenu',
            readOnly: false
        };

        $scope.delMenuItem = {
            category : 'menu',
            name : 'delMenuItem',
            readOnly: false
        };

        $scope.menus = [];
        $scope.menuItems = [];

        $scope.menuHeaders = [
            {
                title: 'Delete',
                value: 'delete'
            },
            {
                title: 'Host',
                value: 'host'
            },
            {
                title: 'Create User Id',
                value: 'createUserId'
            },
            {
                title: 'Create Date',
                value: 'createDate'
            },
            {
                title: 'Update User Id',
                value: 'updateUserId'
            },
            {
                title: 'Update Date',
                value: 'updateDate'
            }
        ];
        $scope.menuItemHeaders = [
            {
                title: 'Delete',
                value: 'delete'
            },
            {
                title: 'Label',
                value: 'label'
            },
            {
                title: 'Host',
                value: 'host'
            },
            {
                title: 'Path',
                value: 'path'
            },
            {
                title: 'Click',
                value: 'click'
            },
            {
                title: 'Template',
                value: 'tpl'
            },
            {
                title: 'Controller',
                value: 'ctrl'
            },
            {
                title: 'Position Left',
                value: 'left'
            },
            {
                title: 'Roles',
                value: 'roles'
            },
            {
                title: 'Create User Id',
                value: 'createUserId'
            },
            {
                title: 'Create Date',
                value: 'createDate'
            },
            {
                title: 'Update User Id',
                value: 'updateUserId'
            },
            {
                title: 'Update Date',
                value: 'updateDate'
            }
        ];

        $scope.sort = {
            sortDir : 'desc',
            sortedBy : 'updateDate'
        };
        $scope.menuFilter = {};
        $scope.menuItemFilter = {};

        //call back function that we passed to our custom directive sortBy, will be called when clicking on any field to sort
        $scope.onSort = function (sortedBy, sortDir) {
            $scope.sort.sortDir = sortDir;
            $scope.sort.sortedBy = sortedBy;
        };

        $scope.addMenu = function () {
            $location.path("/form/com.networknt.light.menu.addMenu");
        }

        $scope.addMenuItem = function () {
            $location.path("/form/com.networknt.light.menu.addMenuItem");
        }

        $scope.deleteMenu = function(menu) {
            $scope.delMenu.data = menu;
            $http.post('api/rs', $scope.delMenu)
                .success(function (data, status, headers, config) {
                    $scope.menus.splice($scope.menus.indexOf(menu), 1);
                    $scope.fetchResult(); // when delete a host, all the menuItems belong to the host will be deleted.
                    toaster.pop('success', status, data, 3000);
                })
        };

        $scope.deleteMenuItem = function(menuItem) {
            $scope.delMenuItem.data = menuItem;
            $http.post('api/rs', $scope.delMenuItem)
                .success(function (data, status, headers, config) {
                    $scope.menuItems.splice($scope.menuItems.indexOf(menuItem), 1);
                    toaster.pop('success', status, data, 3000);
                })
        };

        $scope.updateMenu = function(menu) {
            modelDataService.setModelData(menu);
            $location.path("/form/com.networknt.light.menu.updateMenu");
        };

        $scope.updateMenuItem = function(menuItem) {
            modelDataService.setModelData(menuItem);
            $location.path("/form/com.networknt.light.menu.updateMenuItem");
        };

        //The function that is responsible of fetching the result from the server and setting the grid to the new result
        $scope.fetchResult = function () {
            $http.post('api/rs', $scope.getAllMenu)
                .success(function (result, status, headers, config) {
                    $scope.menus = result.menus;
                    $scope.menuItems = result.menuItems;
                    //console.log('menus', $scope.menus);
                    //console.log('menuItems', $scope.menuItems);
                })
        };
        $scope.fetchResult();

    }]);
