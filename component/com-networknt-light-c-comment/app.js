(function(angular) {
    'use strict';
    angular.module('lightApp', ['ngSanitize', 'ngAnimate'])
        .controller('Controller', ['$scope', function($scope) {
            $scope.comments = [
                {
                    name: '@caitp',
                    date: new Date(),
                    profileUrl: 'https://github.com/caitp',
                    text: 'UI-Comments is designed to simplify the process of creating comment systems similar to Reddit, Imgur or Discuss in AngularJS.',
                    children: [{
                        name: '@bizarro-caitp',
                        date: new Date(),
                        profileUrl: 'https://github.com/bizarro-caitp',
                        text: 'We support nested comments, in a very simple fashion. It\'s great!',
                        children: [{
                            name: '@caitp',
                            date: new Date(),
                            profileUrl: 'https://github.com/caitp',
                            text: 'These nested comments can descend arbitrarily deep, into many levels. This can be used to reflect a long and detailed conversation about typical folly which occurs in comments',
                            children: [{
                                name: '@bizarro-caitp',
                                date: new Date(),
                                profileUrl: 'https://github.com/bizarro-caitp',
                                text: 'Having deep conversations on the internet can be used to drive and derive data about important topics, from marketing demographic information to political affiliation and even sexual orientation if you care to find out about that. Isn\'t that exciting?'
                            }]
                        },{
                            name: '@bizarro-caitp',
                            date: new Date(),
                            profileUrl: 'https://github.com/bizarro-caitp',
                            text: 'Is it REALLY all that wonderful? People tend to populate comments with innane nonsense that ought to get them hellbanned!',
                            comments: [{
                                name: '@caitp',
                                date: new Date(),
                                profileUrl: 'https://github.com/caitp',
                                text: 'Oh whatever lady, whatever'
                            }]
                        }]
                    }]
                }, {
                    name: '@caitp',
                    date: new Date(),
                    profileUrl: 'https://github.com/caitp',
                    text: 'We can have multiple threads of comments at a given moment...',
                }, {
                    name: '@bizarro-caitp',
                    date: new Date(),
                    profileUrl: 'https://github.com/bizarro-caitp',
                    text: 'We can do other fancy things too, maybe...',
                    children: [{
                        name: '@caitp',
                        date: new Date(),
                        profileUrl: 'https://github.com/caitp',
                        text: '...other fancy things, you say?',
                    }, {
                        name: '@caitp',
                        date: new Date(),
                        profileUrl: 'https://github.com/caitp',
                        text: 'suddenly I\'m all curious, what else can we do...',
                        children: [{
                            name: '@bizarro-caitp',
                            date: new Date(),
                            profileUrl: 'https://github.com/bizarro-caitp',
                            text: 'Oh, you\'ll see...',
                        }]
                    }]
                }];

            $scope.addParentComment = function(comment) {
                var parentComment = angular.extend(comment, {
                    name: '@'+comment.name,
                    date: new Date(),
                    profileUrl: 'https://github.com/' + comment.name
                });
                $scope.comments.push(parentComment);
            };
        }])
})(window.angular);