/**
 * Created by steve on 08/07/15.
 */
var ServerActionCreators = require('../actions/ServerActionCreators.js');
var AppConstants = require('../constants/AppConstants.js');
var $ = require('jquery');

function _getErrors(res) {
    var errorMsgs = ["Something went wrong, please try again"];
    var json = JSON.parse(res.text);
    if (json) {
        if (json['errors']) {
            errorMsgs = json['errors'];
        } else if (json['error']) {
            errorMsgs = [json['error']];
        }
    }
    return errorMsgs;
}

var APIEndpoints = AppConstants.APIEndpoints;
var APIRoot = AppConstants.APIRoot;
var host = AppConstants.host;
var ClientId = AppConstants.ClientId;

module.exports = {

    signup: function(email, username, password, passwordConfirmation) {
        request.post(APIEndpoints.REGISTRATION)
            .send({ user: {
                email: email,
                username: username,
                password: password,
                password_confirmation: passwordConfirmation,
                clientId: ClientId
            }})
            .set('Accept', 'application/json')
            .end(function(error, res) {
                if (res) {
                    if (res.error) {
                        var errorMsgs = _getErrors(res);
                        ServerActionCreators.receiveLogin(null, errorMsgs);
                    } else {
                        json = JSON.parse(res.text);
                        ServerActionCreators.receiveLogin(json, null);
                    }
                }
            });
    },

    login: function(userIdEmail, password, rememberMe) {
        console.log('login in WebAPIUtils is been called');

        APIEndpoints.SIGNIN.data = {
            userIdEmail: userIdEmail,
            password: password,
            rememberMe: rememberMe,
            clientId: ClientId
        };

        console.log('SIGNIN', APIEndpoints.SIGNIN);
        $.ajax({
            type: 'POST',
            contentType: 'application/json',
            url: 'http://example:8080/api/rs',
            data: JSON.stringify(APIEndpoints.SIGNIN),
            dataType: 'json',
            error: function(jqXHR, status, error) {
                console.log('login error', error);
                ServerActionCreators.receiveLogin(null, error);
            },
            success: function(result, status, xhr) {
                console.log('login success', result);
                ServerActionCreators.receiveLogin(result, null);
            }
        });
    },

    loadMenu: function() {
        console.log('WebAPIUtils loadMenus is called');
        $.ajax({
            type: 'POST',
            url: 'http://example:8080/api/rs',
            data: JSON.stringify(APIEndpoints.GETMENU),
            contentType: 'application/json',
            dataType: 'json'
        }).done(function(data) {
            console.log('done', data);
            ServerActionCreators.receiveMenu(data, null);
        }).fail(function(error) {
            console.log('error', error);
            ServerActionCreators.receiveMenu(null, error);
        });
    },

    loadBlogs: function() {
        var getBlogs = {
            category: 'demo',
            name: 'getDropdown',
            readOnly: true
        }
        console.log('WebAPIUtils logBlogs is called');
        $.ajax({
            type: 'GET',
            url: 'http://example:8080/api/rs',
            data:  { cmd: encodeURIComponent(JSON.stringify(getBlogs))}
        }).done(function(data) {
            console.log('done', data);
            ServerActionCreators.receiveBlogs(data, null);

        }).fail(function(error) {
            console.log('error', error);
            ServerActionCreators.receiveBlogs(null, error);
        });
    },

    loadBlog: function(blogId) {
        request.get(APIEndpoints.STORIES + '/' + storyId)
            .set('Accept', 'application/json')
            .end(function(error, res){
                if (res) {
                    var json = JSON.parse(res.text);
                    ServerActionCreators.receiveBlog(json);
                }
            });
    },

    createBlog: function(title, body) {
        request.post(APIEndpoints.STORIES)
            .set('Accept', 'application/json')
            .send({ blog: { title: title, body: body } })
            .end(function(error, res){
                if (res) {
                    if (res.error) {
                        var errorMsgs = _getErrors(res);
                        ServerActionCreators.receiveCreatedBlog(null, errorMsgs);
                    } else {
                        var json = JSON.parse(res.text);
                        ServerActionCreators.receiveCreatedBlog(json, null);
                    }
                }
            });
    }

};
