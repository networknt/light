angular.module("lightApp").run(["$templateCache", function($templateCache) {$templateCache.put("tpl/blogHome.html","<div class=\"container\" data-ng-controller=\"BlogCtrl\">\n    <div class=\"row\">\n        <div class=\"col-md-12\">\n            <div class=\"pull-right\" ng-if=\"global.isLogin\">\n                <button class=\"btn btn-info\" ng-click=\"post()\"><i class=\"glyphicon glyphicon-edit\"></i>Post</button>\n            </div>\n            <tabset>\n                <tab heading=\"Recent\">\n                    <h1>Blog</h1>\n                    <!--\n                    <ul class=\"list-inline inner\">\n                        <li ng-repeat=\"tag in global.tagsList\">\n                            <a ng-href=\"{{\'/\'+tag._id}}\" class=\"pure-button pure-button-xsmall info-bg\">{{tag.tag}}</a>\n                        </li>\n                        <li>\n                            <a href=\"/tag\" class=\"pure-button pure-button-xsmall\"><i class=\"fa fa-search\"></i>more</a>\n                        </li>\n                    </ul>\n                    -->\n                    <ul class=\"media-list\">\n                        <li class=\"media\" ng-repeat=\"blog in blogs\">\n                            <div class=\"media-body\">\n                                <div class=\"media-header\">\n                                    <a ng-href=\"{{$index}}\"><i class=\"primary\"></i><h4>{{blog.title}}</h4></a>\n                                </div>\n                                <div class=\"media-content list-content\" marked=\"blog.summary\"></div>\n                                <div class=\"media-footer\">\n                                    <a ng-href=\"{{blog.createUserRid}}\"><i class=\"fa fa-pencil success\"></i>{{blog.createUserId}}</a>\n                                    <i class=\"fa fa-clock-o\">{{blog.createDate | formatDate}}</i>\n                                    <i class=\"fa fa-refresh\">{{blog.updateDate | formatDate}}</i>\n                                    <i class=\"fa fa-comments-o\" title=\"comments\" ng-show=\"blog.comments\"> {{blog.comments}}</i>\n                                    <a ng-repeat=\"tag in article.tagsList\" ng-href=\"{{\'/\'+tag._id}}\" class=\"pure-button pure-button-link\">{{tag.tag}}</a>\n                                </div>\n                            </div>\n                        </li>\n                    </ul>\n                    <pagination total-items=\"page.totalItems\" items-per-page=\"getBlog.data.pageSize\" ng-model=\"getBlog.data.pageNo\" max-size=\"page.maxSize\" class=\"pagination-sm\" boundary-links=\"true\" rotate=\"false\" num-pages=\"page.numPages\" ng-change=\"pageChanged()\"></pagination>\n                    <table>\n                        <tr>\n                            <td>Page: {{getBlog.data.pageNo}} / {{page.numPages}}</td>\n                            <td>&nbsp;</td>\n                            <td>Page Size:</td>\n                            <td><input type=\"number\" min=\"1\" max=\"100\" ng-change=\"filterResult()\" ng-model=\"getBlog.data.pageSize\"></td>\n                        </tr>\n                    </table>\n\n                </tab>\n                <tab heading=\"Hot\">\n                    <div>This is tab hot</div>\n                </tab>\n                <tab heading=\"Updated\">\n                    <div>This is tab updated</div>\n                </tab>\n                <tab heading=\"My posts\" ng-if=\"global.isLogin\">\n                    <div>This is tab my posts</div>\n                </tab>\n            </tabset>\n        </div>\n    </div>\n</div> <!-- /container -->\n");}]);
(function(angular) {
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
})(window.angular);
