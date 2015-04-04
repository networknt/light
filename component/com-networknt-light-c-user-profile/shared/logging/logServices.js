'use strict';
angular.module('lightApp')
/**
* Service that gives us a nice Angular-esque wrapper around the
* stackTrace.js pintStackTrace() method.
*/
.factory(
    "traceService",
    function() {
        return({
            print: printStackTrace
        });
    }
)
/**
 * Override Angular's built in exception handler, and tell it to
 * use our new exceptionLoggingService which is defined below
 */
.provider(
    "$exceptionHandler",{
        $get: function(exceptionLoggingService){
            return(exceptionLoggingService);
        }
    }
)
/**
 * Exception Logging Service, currently only used by the $exceptionHandler
 * it preserves the default behaviour ( logging to the console) but
 * also posts the error server side after generating a stacktrace.
 */
.factory("exceptionLoggingService",["$log","$window", "traceService", function($log, $window, traceService) {
    function error(exception, cause) {

        // preserve the default behaviour which will log the error
        // to the console, and allow the application to continue running.
        $log.error.apply($log, arguments);

        // now try to log the error to the server side.
        try{
            var errorMessage = exception.toString();

            // use our traceService to generate a stack trace
            var stackTrace = traceService.print({e: exception});

            var escape = function(x) { return x.replace(/"/g, '\\"'); };
            var XHR = window.XMLHttpRequest || function() {
                    try { return new ActiveXObject("Msxml3.XMLHTTP"); } catch (e0) {}
                    try { return new ActiveXObject("Msxml2.XMLHTTP.6.0"); } catch (e1) {}
                    try { return new ActiveXObject("Msxml2.XMLHTTP.3.0"); } catch (e2) {}
                    try { return new ActiveXObject("Msxml2.XMLHTTP"); } catch (e3) {}
                    try { return new ActiveXObject("Microsoft.XMLHTTP"); } catch (e4) {}
            };
            var xhr = new XHR();
            xhr.open('POST', '/api/rs', true);
            xhr.setRequestHeader('Content-type', 'application/json');

            var error = '{"category": "log", "name": "logEvent", "readOnly":false, "data": {' +
                '"message": "' + escape(errorMessage || '') + '",' +
                '"type": "exception", ' +
                '"url": "' + escape(window.location.href) + '",' +
                '"stackTrace": "' + (stackTrace) + '",' +
                '"cause": "' + escape(cause || '') + '"' +
                '}}';
            console.log("error", error);
            xhr.send(error);
        } catch (loggingError){
             $log.warn("Error server-side logging failed");
             $log.log(loggingError);
        }
    }
    return(error);
}])
/**
 * Application Logging Service to give us a way of logging
 * error / debug statements from the client to the server.
 */
.factory("lightLoggingService", ["$log","$window",function($log, $window) {
    return({
        error: function(message){
            // preserve default behaviour
            $log.error.apply($log, arguments);
            // send server side
            //var escape = function(x) { return x.replace('\\', '\\\\').replace('\"', '\\"'); };
            var escape = function(x) { return x.replace(/"/g, '\\"'); };
            var XHR = window.XMLHttpRequest || function() {
                    try { return new ActiveXObject("Msxml3.XMLHTTP"); } catch (e0) {}
                    try { return new ActiveXObject("Msxml2.XMLHTTP.6.0"); } catch (e1) {}
                    try { return new ActiveXObject("Msxml2.XMLHTTP.3.0"); } catch (e2) {}
                    try { return new ActiveXObject("Msxml2.XMLHTTP"); } catch (e3) {}
                    try { return new ActiveXObject("Microsoft.XMLHTTP"); } catch (e4) {}
                };
            var xhr = new XHR();
            xhr.open('POST', '/api/rs', true);
            xhr.setRequestHeader('Content-type', 'application/json');

            var error = '{"category": "log", "name": "logEvent", "readOnly":false, "data": {' +
                '"message": "' + escape(message || '') + '",' +
                '"type": "error", ' +
                '"url": "' + escape(window.location.href) + '"' +
                '}}';
            console.log("error", error);
            xhr.send(error);
        },
        debug: function(message){
            $log.log.apply($log, arguments);
            var escape = function(x) { return x.replace('\\', '\\\\').replace('\"', '\\"'); };
            var XHR = window.XMLHttpRequest || function() {
                    try { return new ActiveXObject("Msxml3.XMLHTTP"); } catch (e0) {}
                    try { return new ActiveXObject("Msxml2.XMLHTTP.6.0"); } catch (e1) {}
                    try { return new ActiveXObject("Msxml2.XMLHTTP.3.0"); } catch (e2) {}
                    try { return new ActiveXObject("Msxml2.XMLHTTP"); } catch (e3) {}
                    try { return new ActiveXObject("Microsoft.XMLHTTP"); } catch (e4) {}
                };
            var xhr = new XHR();
            xhr.open('POST', '/api/rs', true);
            xhr.setRequestHeader('Content-type', 'application/json');

            var error = '{"category": "log", "name": "logEvent", "readOnly":false, "data": {' +
                '"message": "' + escape(message || '') + '",' +
                '"type": "debug", ' +
                '"url": "' + escape(window.location.href) + '"' +
                '}}';
            console.log("error", error);
            xhr.send(error);
        }
    });
}]);