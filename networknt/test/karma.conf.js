// Karma configuration
// http://karma-runner.github.io/0.12/config/configuration-file.html
// Generated on 2014-07-26 using
// generator-karma 0.8.3

module.exports = function(config) {
  'use strict';

  config.set({
    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,

    // base path, that will be used to resolve files and exclude
    basePath: '../',

    // testing framework to use (jasmine/mocha/qunit/...)
    frameworks: ['jasmine'],

    // list of files / patterns to load in the browser
    files: [
'bower_components/jquery/dist/jquery.js',
'bower_components/json3/lib/json3.js',
'bower_components/underscore/underscore.js',
'bower_components/angular/angular.js',
'bower_components/angular-resource/angular-resource.js',
'bower_components/angular-cookies/angular-cookies.js',
'bower_components/angular-sanitize/angular-sanitize.js',
'bower_components/angular-animate/angular-animate.js',
'bower_components/angular-touch/angular-touch.js',
'bower_components/angular-route/angular-route.js',
'bower_components/angular-translate/angular-translate.js',
'bower_components/angular-mocks/angular-mocks.js',
'bower_components/angular-ui-select/dist/select.js',
'bower_components/bootstrap/dist/js/bootstrap.js',
'bower_components/angular-strap/dist/angular-strap.js',
'bower_components/angular-strap/dist/angular-strap.tpl.js',
'bower_components/angular-ui-tree/dist/angular-ui-tree.js',
'bower_components/angular-ui-tree-filter/dist/angular-ui-tree-filter.js',
'bower_components/angular-ui-ace/ui-ace.js',
'bower_components/angular-marked/angular-marked.js',
'bower_components/tv4/tv4.js',
'bower_components/objectpath/lib/ObjectPath.js',
'bower_components/angular-loading-bar/build/loading-bar.js',
'bower_components/angular-local-storage/dist/angular-local-storage.js',
'bower_components/angular-ui-utils/highlight.js',
'bower_components/angular-schema-form/dist/schema-form.js',
'bower_components/angular-schema-form/dist/bootstrap-decorator.js',
'bower_components/angular-schema-form-marked/bootstrap-marked.min.js',
'bower_components/angular-schema-form-ui-ace/bootstrap-ui-ace.min.js',
'bower_components/angular-schema-form-strapselect/bootstrap-strapselect.min.js',
'bower_components/angular-schema-form-file-reader/bootstrap-file-reader.min.js',
'bower_components/angular-schema-form-ui-select/bootstrap-ui-select.min.js',
'bower_components/momentjs/moment.js',
'bower_components/ace-builds/src-min-noconflict/ace.js',
'bower_components/marked/lib/marked.js',
'bower_components/angular-schema-form-ui-select/angular-underscore.js',
'bower_components/angular-schema-form-ui-select/ui-sortable.js',
'bower_components/angular-schema-form-marked/bootstrap-marked.js',
'bower_components/angular-schema-form-ui-ace/bootstrap-ui-ace.js',
'bower_components/schema-form-datetimepicker/date.js',
'bower_components/schema-form-datetimepicker/schema-form-date-time-picker.min.js',
'app/scripts/ui-bootstrap-custom-tpls-0.12.0.js',
'app/scripts/toaster.js',
'app/scripts/app.js',
'app/scripts/locale_en-us.js',
'app/scripts/services.js',
'app/scripts/filters.js',
'app/scripts/directives.js',
'app/scripts/controllers/main.js',
'app/scripts/controllers/signin.js',
'app/scripts/controllers/classFeed.js',
'app/scripts/controllers/contact.js',
'app/scripts/controllers/menu.js',
'app/scripts/controllers/form.js',
'app/scripts/controllers/blog.js',
'app/scripts/controllers/page.js',
'app/scripts/controllers/forum.js',
'test/spec/controllers/*.js'
     ],

    // list of files / patterns to exclude
    exclude: [],

    // web server port
    port: 8080,

    // Start these browsers, currently available:
    // - Chrome
    // - ChromeCanary
    // - Firefox
    // - Opera
    // - Safari (only Mac)
    // - PhantomJS
    // - IE (only Windows)
    browsers: [
      'PhantomJS'
    ],

    // Which plugins to enable
    plugins: [
      'karma-phantomjs-launcher',
      'karma-jasmine'
    ],

    // Continuous Integration mode
    // if true, it capture browsers, run tests and exit
    singleRun: false,

    colors: true,

    // level of logging
    // possible values: LOG_DISABLE || LOG_ERROR || LOG_WARN || LOG_INFO || LOG_DEBUG
    logLevel: config.LOG_INFO

    // Uncomment the following lines if you are using grunt's server to run the tests
    // proxies: {
    //   '/': 'http://localhost:9000/'
    // },
    // URL root prevent conflicts with the site root
    // urlRoot: '_karma_'
  });
};
