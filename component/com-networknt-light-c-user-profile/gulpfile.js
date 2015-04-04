/* global require */

var gulp = require('gulp');
var webserver = require('gulp-webserver');
var templateCache = require('gulp-angular-templatecache');
var minifyHtml = require('gulp-minify-html');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var streamqueue = require('streamqueue');
var jscs = require('gulp-jscs');

gulp.task('minify', function() {
  var stream = streamqueue({objectMode: true});
  stream.queue(
              gulp.src('./views/*.html')
                  .pipe(minifyHtml({
                    empty: true,
                    spare: true,
                    quotes: true
                  }))
                  .pipe(templateCache({
                    module: 'lightApp',
                    root: 'views/'
                  }))
  );
  stream.queue(gulp.src('./scripts/*.js'));
  stream.done();
});

gulp.task('non-minified-dist', function() {
  var stream = streamqueue({objectMode: true});
  stream.queue(
              gulp.src('./views/*.html')
                  .pipe(templateCache({
                    module: 'lightApp',
                    root: 'views/'
                  }))
    );
  stream.queue(gulp.src('./scripts/*.js'));
  stream.done();
});

gulp.task('jscs', function() {
  gulp.src('./scripts/**/*.js')
      .pipe(jscs());
});

gulp.task('default', [
  'minify',
  'non-minified-dist'
]);

gulp.task('watch', function() {
  gulp.watch('./scripts/**/*', ['default']);
});

gulp.task('webserver', function() {
  gulp.src('.')
    .pipe(webserver({
      livereload: true,
      fallback: 'index.html',
      proxies: [
        {
          source: '/api',
          target: 'http://example:8080/api'
        }
      ],
      port: 8001,
      open: true
    }));
});
