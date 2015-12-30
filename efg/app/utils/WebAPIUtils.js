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
var Host = AppConstants.Host;
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
        //console.log('login in WebAPIUtils is been called');

        var signIn =  {
            category : 'user',
            name : 'signInUser',
            readOnly: false,
            data: {
                userIdEmail: userIdEmail,
                password: password,
                rememberMe: rememberMe,
                clientId: ClientId
            }
        };


        //console.log('login', signIn);
        $.ajax({
            type: 'POST',
            contentType: 'application/json',
            url: '/api/rs',
            data: JSON.stringify(signIn),
            dataType: 'json',
            error: function(jqXHR, status, error) {
                //console.log('login error', error);
                ServerActionCreators.receiveLogin(null, error);
            },
            success: function(result, status, xhr) {
                //console.log('login success', result);
                ServerActionCreators.receiveLogin(result, null);
            }
        });
    },

    loadMenu: function() {
        var getMenu = {
            category : 'menu',
            name : 'getMenu',
            readOnly: true,
            data : {
                host : Host
            }

        }
        //console.log('WebAPIUtils loadMenus is called', getMenu);
        $.ajax({
            type: 'POST',
            url: '/api/rs',
            data: JSON.stringify(getMenu),
            contentType: 'application/json',
            dataType: 'json'
        }).done(function(data) {
            //console.log('getMenu done', data);
            ServerActionCreators.receiveMenu(data, null);
        }).fail(function(error) {
            //console.log('getMenu error', error);
            ServerActionCreators.receiveMenu(null, error);
        });
    },

    loadBlogs: function() {
        var getBlogs = {
            category: 'demo',
            name: 'getDropdown',
            readOnly: true
        }
        //console.log('WebAPIUtils logBlogs is called');
        $.ajax({
            type: 'GET',
            url: '/api/rs',
            data:  { cmd: encodeURIComponent(JSON.stringify(getBlogs))}
        }).done(function(data) {
            //console.log('done', data);
            ServerActionCreators.receiveBlogs(data, null);

        }).fail(function(error) {
            //console.log('error', error);
            ServerActionCreators.receiveBlogs(null, error);
        });
    },

    loadBlog: function(categoryId) {
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
    },

    loadCatalog: function() {
        var getCatalogTree = {
            category: 'catalog',
            name: 'getCatalogTree',
            readOnly: true,
            data: {
                host: AppConstants.host
            }
        };

        //console.log('WebAPIUtils loadCatalog is called', getCatalogTree);
        $.ajax({
            type: 'GET',
            url: '/api/rs',
            data:  { cmd: encodeURIComponent(JSON.stringify(getCatalogTree))}
        }).done(function(data) {
            //console.log('catalog', data);
            ServerActionCreators.receiveCatalog(data, null);
        }).fail(function(error) {
            //console.log('error', error);
            ServerActionCreators.receiveCatalog(null, error);
        });
    },

    loadProducts: function(rid) {
        var getCatalogProduct = {
            category: 'catalog',
            name: 'getCatalogProduct',
            readOnly: true,
            data: {
                pageSize: 10,
                pageNo: 1,
                '@rid': rid
            }
        }
        //console.log('WebAPIUtils getCatalogProduct is called');
        $.ajax({
            type: 'GET',
            url: '/api/rs',
            data:  { cmd: encodeURIComponent(JSON.stringify(getCatalogProduct))}
        }).done(function(data) {
            //console.log('product', data);
            ServerActionCreators.receiveProducts(data, null);

        }).fail(function(error) {
            //console.log('error', error);
            ServerActionCreators.receiveProducts(null, error);
        });
    },

    getUser: function(userId) {
        var getUser = {
            category: 'user',
            name: 'getUser',
            readOnly: true,
            data: {
                userId: userId
            }
        };
        //console.log('WebAPIUtils getUser is called');
        $.ajax({
            type: 'GET',
            url: '/api/rs',
            data:  { cmd: encodeURIComponent(JSON.stringify(getUser))}
        }).done(function(data) {
            //console.log('retrieveUserProfile user', data);
            ServerActionCreators.receiveUser(data, null);

        }).fail(function(error) {
            //console.log('retrieveUserProfile error', error);
            ServerActionCreators.receiveUser(null, error);
        });
    },

    updShippingAddress: function(data) {
        var updAddress = {
            category: 'shipping',
            name: 'updAddress',
            readOnly: false,
            data: data
        };
        //console.log('WebAPIUtils updShippingAddress is called');
        $.ajax({
            type: 'POST',
            url: '/api/rs',
            data: JSON.stringify(updAddress),
            contentType: 'application/json',
            dataType: 'json'
        }).done(function(data) {
            //console.log('updShippingAddress done', data);
            ServerActionCreators.receiveUpdShippingAddress(data, null);
        }).fail(function(error) {
            //console.log('updShippingAddress error', error);
            ServerActionCreators.receiveUpdShippingAddress(null, error);
        });
    },

    cnfShippingAddress: function(data) {
        var cnfAddress = {
            category: 'shipping',
            name: 'cnfdAddress',
            readOnly: true,
            data: data
        };
        $.ajax({
            type: 'POST',
            url: '/api/rs',
            data: JSON.stringify(cnfAddress),
            contentType: 'application/json',
            dataType: 'json'
        }).done(function(data) {
            ServerActionCreators.CnfShippingAddressRes(data, null);
        }).fail(function(error) {
            ServerActionCreators.CnfShippingAddressRes(null, error);
        });
    },

    getForm: function(formId) {
        var getForm = {
            category: 'form',
            name: 'getForm',
            readOnly: true,
            data: {
                formId: formId
            }
        };
        //console.log('WebAPIUtils getForm is called', formId);
        $.ajax({
            type: 'GET',
            url: '/api/rs',
            data:  { cmd: encodeURIComponent(JSON.stringify(getForm))}
        }).done(function(data) {
            //console.log('receiveForm form', data);
            ServerActionCreators.receiveForm(data, null);

        }).fail(function(error) {
            //console.log('receiveForm error', error);
            ServerActionCreators.receiveForm(null, error);
        });
    },

    getClientToken: function() {
        var getClientToken = {
            category: 'payment',
            name: 'getClientToken',
            readOnly: true
        };
        $.ajax({
            type: 'GET',
            url: '/api/rs',
            data:  { cmd: encodeURIComponent(JSON.stringify(getClientToken))}
        }).done(function(data) {
            //console.log('receiveClientToken token ', data);
            ServerActionCreators.receiveClientToken(data, null);

        }).fail(function(error) {
            //console.log('receiveClientToken error', error);
            ServerActionCreators.receiveClientToken(null, error);
        });
    },

    addOrder: function(order) {
        var addOrder = {
            category: 'order',
            name: 'addOrder',
            readOnly: false,
            data: order
        };
        //console.log('WebAPIUtils addOrder is called');
        $.ajax({
            type: 'POST',
            url: '/api/rs',
            data: JSON.stringify(addOrder),
            contentType: 'application/json',
            dataType: 'json'
        }).done(function(data) {
            console.log('addOrder done', data);
            ServerActionCreators.receiveAddOrder(data, null);
        }).fail(function(error) {
            //console.log('addOrder error', error);
            ServerActionCreators.receiveAddOrder(null, error);
        });
    },

    addTransaction: function(transaction, orderId) {
        var addTransaction = {
            category: 'payment',
            name: 'addTransaction',
            readOnly: false,
            data: {
                transaction: transaction,
                orderId: orderId
            }
        };
        $.ajax({
            type: 'POST',
            url: '/api/rs',
            data: JSON.stringify(addTransaction),
            contentType: 'application/json',
            dataType: 'json'
        }).done(function(data) {
            ServerActionCreators.receiveAddTransaction(data, null);
        }).fail(function(error) {
            ServerActionCreators.receiveAddTransaction(null, error);
        });
    }

};
