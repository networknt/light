angular.module("lightApp").run(["$templateCache", function($templateCache) {$templateCache.put("tpl/site-info.html","<div class=\"row\">\n    <div class=\"col-xs-12 col-sm-12 col-md-12\">\n        <div class=\"panel panel-default\">\n            <div class=\"panel-body\">\n                <ul class=\"inner list-inline\">\n                    <li>Total visits：{{global.visitors}}</li>\n                    <li>Total articles: {{global.articles}}</li>\n                    <li>Total comments: {{global.comments}}</li>\n                    <li>Total members：{{global.users}}</li>\n                    <li>Online users：{{global.onlineNum}}</li>\n                    <li>Online members：{{global.onlineUsers}}</li>\n                    <li>Max online users：{{global.maxOnlineNum}}</li>\n                    <li>Online Since：{{global.maxOnlineTime  | date:\'yy-MM-dd HH:mm\'}}</li>\n                </ul>\n            </div>\n        </div>\n    </div>\n</div>\n");}]);
(function(angular) {
    'use strict';
    angular.module('lightApp')
        .directive('comNetworkntLightSiteInfo', function() {
            return {
                templateUrl: 'tpl/site-info.html'
            };
        });
})(window.angular);
