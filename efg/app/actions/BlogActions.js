var AppDispatcher = require('../dispatcher/AppDispatcher');
var BlogConstants = require('../constants/BlogConstants');
var AppConstants = require('../constants/AppConstants');
var MockBlogData = require('../components/blog/MockBlogData');
var $ = require('jquery');

var BlogActions = {

    getBlogs: function() {
        $.ajax({
            type: 'POST',
            url: 'http://example:8080/api/rs',
            data: JSON.stringify({
                category : 'blog',
                name : 'getBlogTree',
                readOnly: true,
                "data": {
                    host: AppConstants.host
                }
            }),
            contentType: 'application/json',
            dataType: 'json',
            error: function(jqXHR, status, error) {
                console.log('BlogActions.getBlogs - Error received, using mock data.', error);
                AppDispatcher.dispatch({
                    type: BlogConstants.ActionTypes.BLOGS_RESPONSE,
                    json: MockBlogData.getBlogs(),
                    error: null
                });
            },
            success: function(result, status, xhr) {
                AppDispatcher.dispatch({
                    type: BlogConstants.ActionTypes.BLOGS_RESPONSE,
                    json: result,
                    error: null
                });
            }
        });
    },

    getBlogPosts: function(rid) {
        $.ajax({
            type: 'POST',
            url: 'http://example:8080/api/rs',
            data: JSON.stringify({
                category : 'blog',
                name : 'getBlogPost',
                readOnly: true,
                "data": {
                    host: AppConstants.host,
                    "@rid": rid,
                    pageNo: 0,
                    pageSize: 10,
                    sortDir: 'desc',
                    sortedBy: 'createDate'
                }
            }),
            contentType: 'application/json',
            dataType: 'json',
            error: function(jqXHR, status, error) {
                console.log('BlogActions.getBlogPosts - Error received, using mock data.', error);
                AppDispatcher.dispatch({
                    type: BlogConstants.ActionTypes.BLOG_POSTS_RESPONSE,
                    json: MockBlogData.getBlogPosts(),
                    error: null
                });
            },
            success: function(result, status, xhr) {
                AppDispatcher.dispatch({
                    type: BlogConstants.ActionTypes.BLOG_POSTS_RESPONSE,
                    json: result,
                    error: null
                });
            }
        });
    },

    getPost: function (rid) {
        $.ajax({
            type: 'POST',
            url: 'http://example:8080/api/rs',
            data: JSON.stringify({
                category : 'blog',
                name : 'getPost',
                readOnly: true,
                "data": {
                    host: AppConstants.host,
                    "@rid": rid
                }
            }),
            contentType: 'application/json',
            dataType: 'json',
            error: function(jqXHR, status, error) {
                console.log('BlogActions.getBlogPosts - Error received, using mock data.', error);
                AppDispatcher.dispatch({
                    type: BlogConstants.ActionTypes.BLOG_POST_RESPONSE,
                    json: MockBlogData.getPost(),
                    error: null
                });
            },
            success: function(result, status, xhr) {
                AppDispatcher.dispatch({
                    type: BlogConstants.ActionTypes.BLOG_POST_RESPONSE,
                    json: result,
                    error: null
                });
            }
        });
    }

};

module.exports = BlogActions;