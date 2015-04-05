/**
 * Created by husteve on 10/1/2014.
 */
angular.module('lightApp')
.directive('onBlurChange', function ($parse) {
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
})
.directive('onEnterBlur', function() {
    return function(scope, element, attrs) {
        element.bind("keydown keypress", function(event) {
            if(event.which === 13) {
                element.blur();
                event.preventDefault();
            }
        });
    };
})
.directive('dynamic', function ($compile) {
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
