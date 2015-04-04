/**
 * Created by husteve on 10/1/2014.
 */
angular.module('lightApp').directive('sortBy', function () {
    return {
        templateUrl: 'views/sort-by.html',
        restrict: 'E',
        transclude: true,
        replace: true,
        scope: {
            sortdir: '=',
            sortedby: '=',
            sortvalue: '@',
            onsort: '='
        },
        link: function (scope, element, attrs) {
            scope.sort = function () {
                if (scope.sortedby == scope.sortvalue)
                    scope.sortdir = scope.sortdir == 'asc' ? 'desc' : 'asc';
                else {
                    scope.sortedby = scope.sortvalue;
                    scope.sortdir = 'asc';
                }
                scope.onsort(scope.sortedby, scope.sortdir);
            }
        }
    };
}).directive('onBlurChange', function ($parse) {
    return function (scope, element, attr) {
        var fn = $parse(attr['onBlurChange']);
        var hasChanged = false;
        element.on('change', function (event) {
            hasChanged = true;
        });

        element.on('blur', function (event) {
            if (hasChanged) {
                scope.$apply(function () {
                    fn(scope, {$event: event});
                });
                hasChanged = false;
            }
        });
    };
}).directive('onEnterBlur', function() {
    return function(scope, element, attrs) {
        element.bind("keydown keypress", function(event) {
            if(event.which === 13) {
                element.blur();
                event.preventDefault();
            }
        });
    };
}).directive('dynamic', function ($compile) {
    return {
        restrict: 'A',
        replace: true,
        link: function (scope, ele, attrs) {
            scope.$watch(attrs.dynamic, function(html) {
                ele.html(html);
                $compile(ele.contents())(scope);
            });
        }
    };
});
