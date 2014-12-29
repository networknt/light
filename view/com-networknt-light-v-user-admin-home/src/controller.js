(function(angular) {
    'use strict';
    angular.module('lightApp').controller('UserAdminHomeCtrl', ['$scope', '$http', '$location', 'toaster', 'modelDataService', function ($scope, $http, $location, toaster, modelDataService) {
        $scope.getAllUser = {
            category : 'user',
            name : 'getAllUser',
            readOnly: true,
            data : {
                pageSize : 10,
                pageNo : 1,
                sortDir : 'desc',
                sortedBy : 'createDate'
            }
        };
        $scope.delUser = {
            category : 'user',
            name : 'delUser',
            readOnly: false
        };
        $scope.lockUser = {
            category : 'user',
            name : 'lockUser',
            readOnly: false
        };
        $scope.unlockUser = {
            category : 'user',
            name : 'unlockUser',
            readOnly: false
        };

        $scope.page = { maxSize: 5, totalItems: 0, numPages: 0 };
        $scope.users = [];
        $scope.roles = [];
        $scope.hosts = [];
        $scope.userHeaders = [
            {
                title: 'Delete',
                value: 'delete'
            },
            {
                title: 'Locked',
                value: 'locked'
            },
            {
                title: 'User Id',
                value: 'userId'
            },
            {
                title: 'Host',
                value: 'host'
            },
            {
                title: 'Roles',
                value: 'roles'
            },
            {
                title: 'Email',
                value: 'email'
            },
            {
                title: 'First Name',
                value: 'firstName'
            },
            {
                title: 'Last Name',
                value: 'lastName'
            },
            {
                title: 'Up Users',
                value: 'upUsers'
            },
            {
                title: 'Down Users',
                value: 'downUsers'
            },
            {
                title: 'Karma',
                value: 'karma'
            },
            {
                title: 'Create Date',
                value: 'createDate'
            },
            {
                title: 'Update Date',
                value: 'updateDate'
            },
            {
                title: 'Log in Date',
                value: 'logInDate'
            },
            {
                title: 'Log out Date',
                value: 'logOutDate'
            }
        ];

        $scope.roleHeaders = [
            {
                title: 'Delete',
                value: 'delete'
            },
            {
                title: 'Role Id',
                value: 'id'
            },
            {
                title: 'Host',
                value: 'host'
            },
            {
                title: 'Description',
                value: 'desc'
            },
            {
                title: 'Create User Id',
                value: 'createUserId'
            },
            {
                title: 'Create Date',
                value: 'createDate'
            }
        ];

        $scope.hostHeaders = [
            {
                title: 'Delete',
                value: 'delete'
            },
            {
                title: 'Host',
                value: 'host'
            },
            {
                title: 'Base',
                value: 'base'
            },
            {
                title: 'TransferMinSize',
                value: 'transferMinSize'
            },
            {
                title: 'DirectoryListingEnabled',
                value: 'directoryListingEnabled'
            }
        ];

        $scope.roleSort = {
            sortDir : 'asc',
            sortedBy : 'id'
        };
        $scope.hostSort = {
            sortDir : 'asc',
            sortedBy : 'host'
        };
        $scope.roleFilter = {};
        $scope.hostFilter = {};

        $scope.fetchResult = function () {
            $http.post('api/rs', $scope.getAllUser)
                .success(function (result, status, headers, config) {
                    $scope.users = result.users;
                    $scope.roles = result.roles;
                    $scope.hosts = result.hosts;
                    $scope.page.totalItems = result.total;
                    //console.log($scope.users);
                    console.log($scope.roles);
                    console.log($scope.hosts);
                    //console.log($scope.page.totalItems);
                    $scope.page.numPages = Math.ceil($scope.page.totalItems / $scope.getAllUser.data.pageSize);
                    //console.log($scope.page.numPages);
                })
        };

        $scope.onUserSort = function (sortedBy, sortDir) {
            $scope.getAllUser.data.sortDir = sortDir;
            $scope.getAllUser.data.sortedBy = sortedBy;
            $scope.getAllUser.data.pageNo = 1;
            $scope.fetchResult();
        };

        $scope.onRoleSort = function (sortedBy, sortDir) {
            $scope.roleSort.sortDir = sortDir;
            $scope.roleSort.sortedBy = sortedBy;
        };

        $scope.onHostSort = function (sortedBy, sortDir) {
            $scope.hostSort.sortDir = sortDir;
            $scope.hostSort.sortedBy = sortedBy;
        };

        //Will be called when filtering the grid, will reset the page number to one
        $scope.filterResult = function () {
            $scope.getAllUser.data.pageNo = 1;
            $scope.fetchResult();
        };

        $scope.pageChanged = function() {
            $scope.fetchResult();
        };

        $scope.lock = function(user) {
            $scope.lockUser.data = user;
            $http.post('api/rs', $scope.lockUser)
                .success(function (data, status, headers, config) {
                    user.locked = true;
                    toaster.pop('success', status, data, 3000);
                })
        };

        $scope.unlock = function(user) {
            $scope.unlockUser.data = user;
            $http.post('api/rs', $scope.unlockUser)
                .success(function (data, status, headers, config) {
                    user.locked = false;
                    toaster.pop('success', status, data, 3000);
                })
        };

        $scope.deleteUser = function(user) {
            $scope.delUser.data = user;
            $http.post('api/rs', $scope.delUser)
                .success(function (data, status, headers, config) {
                    $scope.users.splice($scope.users.indexOf(user), 1);
                    toaster.pop('success', status, data, 3000);
                })
        };

        $scope.updateUser = function(user) {
            modelDataService.setModelData(user);
            $location.path("/form/com.networknt.light.user.update.permission");
        };

        $scope.deleteRole = function(role) {
            $scope.delRole.data = role;
            $http.post('api/rs', $scope.delRole)
                .success(function (data, status, headers, config) {
                    $scope.roles.splice($scope.roles.indexOf(role), 1);
                    toaster.pop('success', status, data, 3000);
                })
        };

        $scope.updateRole = function(role) {
            modelDataService.setModelData(role);
            $location.path("/form/com.networknt.light.user.update.role");
        };

        $scope.addRole = function() {
            $location.path("/form/com.networknt.light.user.add.role");
        };

        $scope.pageChanged();
    }]);
})(window.angular);
