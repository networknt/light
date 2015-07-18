/**
 * Created by steve on 08/07/15.
 */
var ServerActionCreators = require('../actions/ServerActionCreators.js');
var AppConstants = require('../constants/AppConstants.js');
var request = require('superagent');

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

        request.post(APIRoot)
            .send(APIEndpoints.SIGNIN)
            .set('Accept', 'application/json')
            .end(function(error, res){
                if (res) {
                    console.log('res =', res);
                    if (res.error) {
                        var errorMsgs = _getErrors(res);
                        ServerActionCreators.receiveLogin(null, errorMsgs);
                    } else {
                        console.log('res.text', res.text);
                        ServerActionCreators.receiveLogin(res.text, null);
                    }
                }
            });
    },

    loadBlogs: function() {
        console.log('WebAPIUtils logBlogs is called');
        request.get('http://example:8080/api/rs?cmd=' +  encodeURIComponent(JSON.stringify(APIEndpoints.BLOGS)))
            .set('Accept', 'application/json')
            .end(function(error, res){
                if (res) {
                    console.log('loadBlogs res', res);
                    var json = JSON.parse(res.text);
                    ServerActionCreators.receiveBlogs(json);
                }
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
