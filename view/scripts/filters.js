'use strict';

angular.module('lightApp')
    .filter('formatDate', ['$filter', '$locale',
        function ($filter, $locale) {
            return function (date, full) {
                var o = Date.now() - date,
                    dateFilter = $filter('date');
                if (full) {
                    return dateFilter(date, 'dd MMM yyyy HH:mm');
                } else if (o > 259200000) {
                    return dateFilter(date, 'MM-dd HH:mm');
                } else if (o > 86400000) {
                    return Math.floor(o / 86400000) + 'days ago';
                } else if (o > 3600000) {
                    return Math.floor(o / 3600000) + 'hours ago';
                } else if (o > 60000) {
                    return Math.floor(o / 60000) + 'minutes ago';
                } else {
                    return 'just now';
                }
            };
        }
    ])
    .filter('formatTime', ['$locale',
        function ($locale) {
            return function (seconds) {
                var re = '',
                    q = 0,
                    o = seconds > 0 ? Math.round(+seconds) : Math.floor(Date.now() / 1000),
                    TIME = $locale.DATETIME;
                function calculate(base) {
                    q = o % base;
                    o = (o - q) / base;
                    return o;
                }
                calculate(60);
                re = q + TIME.second;
                calculate(60);
                re = (q > 0 ? (q + TIME.minute) : '') + re;
                calculate(24);
                re = (q > 0 ? (q + TIME.hour) : '') + re;
                return o > 0 ? (o + TIME.day + re) : re;
            };
        }
    ])
    .filter('timeago', function() {
        return function(date) {
            return moment(date).fromNow();
        };
    })
    .filter('trust', ['$sce', function ($sce) {
        return function (val) {
            return $sce.trustAsHtml(val);
        };
    }])
