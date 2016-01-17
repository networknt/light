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

    getBlogTree: function() {
        let getBlogTree = {
            category: 'blog',
            name: 'getBlogTree',
            readOnly: true
        };
        $.ajax({
            type: 'GET',
            url: '/api/rs',
            data:  { cmd: encodeURIComponent(JSON.stringify(getBlogTree))}
        }).done(function(data) {
            //console.log('done', data);
            ServerActionCreators.getBlogTreeResponse(data, null);

        }).fail(function(error) {
            //console.log('error', error);
            ServerActionCreators.getBlogTreeResponse(null, error);
        });
    },

    getNewsTree: function() {
        let getNewsTree = {
            category: 'news',
            name: 'getNewsTree',
            readOnly: true
        };
        $.ajax({
            type: 'GET',
            url: '/api/rs',
            data:  { cmd: encodeURIComponent(JSON.stringify(getNewsTree))}
        }).done(function(data) {
            //console.log('done', data);
            ServerActionCreators.getNewsTreeResponse(data, null);

        }).fail(function(error) {
            //console.log('error', error);
            ServerActionCreators.getNewsTreeResponse(null, error);
        });
    },

    getBlogPost: function(rid, pageNo, pageSize) {
        let getBlogPost = {
            category: 'blog',
            name: 'getBlogPost',
            readOnly: true,
            data: {
                pageSize: pageSize,
                pageNo: pageNo,
                '@rid': rid
            }
        };
        //console.log('WebAPIUtils.getBlogPost', getBlogPost);
        $.ajax({
            type: 'GET',
            url: '/api/rs',
            data:  { cmd: encodeURIComponent(JSON.stringify(getBlogPost))}
        }).done(function(data) {
            //console.log('product', data);
            ServerActionCreators.getBlogPostResponse(data, null);

        }).fail(function(error) {
            //console.log('error', error);
            ServerActionCreators.getBlogPostResponse(null, error);
        });
    },

    getNewsPost: function(rid, pageNo, pageSize) {
        let getNewsPost = {
            category: 'news',
            name: 'getNewsPost',
            readOnly: true,
            data: {
                pageSize: pageSize,
                pageNo: pageNo,
                '@rid': rid
            }
        };
        $.ajax({
            type: 'GET',
            url: '/api/rs',
            data:  { cmd: encodeURIComponent(JSON.stringify(getNewsPost))}
        }).done(function(data) {
            ServerActionCreators.getNewsPostResponse(data, null);

        }).fail(function(error) {
            ServerActionCreators.getNewsPostResponse(null, error);
        });
    },

    getBlog: function() {
        let getBlog = {
            category: 'blog',
            name: 'getBlog',
            readOnly: true
        };
        $.ajax({
            type: 'GET',
            url: '/api/rs',
            data:  { cmd: encodeURIComponent(JSON.stringify(getBlog))}
        }).done(function(data) {
            ServerActionCreators.getBlogResponse(data, null);

        }).fail(function(error) {
            ServerActionCreators.getBlogResponse(null, error);
        });
    },

    /**
     * This method will be used by blog, forum and news.
      * @param action
     */
    addPost: function(action) {
        $.ajax({
            type: 'POST',
            url: '/api/rs',
            data: JSON.stringify(action),
            contentType: 'application/json',
            dataType: 'json'
        }).done(function(data) {
            ServerActionCreators.addPostResponse(data, null);
        }).fail(function(error) {
            ServerActionCreators.addPostResponse(null, error);
        });
    },

    /**
     * This method will be used by blog, forum and news.
     * @param action
     */
    updPost: function(action) {
        $.ajax({
            type: 'POST',
            url: '/api/rs',
            data: JSON.stringify(action),
            contentType: 'application/json',
            dataType: 'json'
        }).done(function(data) {
            ServerActionCreators.updPostResponse(data, null);
        }).fail(function(error) {
            ServerActionCreators.updPostResponse(null, error);
        });
    },

    /**
     * This method will be used by blog, forum and news.
     * @param rid
     */
    delPost: function(rid) {
        let delPost = {
            category : 'blog',
            name : 'delPost',
            readOnly: false,
        };
        let data = {};
        data['@rid'] = rid;
        delPost.data = data;

        $.ajax({
            type: 'POST',
            url: '/api/rs',
            data: JSON.stringify(delPost),
            contentType: 'application/json',
            dataType: 'json'
        }).done(function(data) {
            ServerActionCreators.delPostResponse(data, null);
        }).fail(function(error) {
            ServerActionCreators.delPostResponse(null, error);
        });
    },

    getCatalogTree: function() {
        var getCatalogTree = {
            category: 'catalog',
            name: 'getCatalogTree',
            readOnly: true
        };
        $.ajax({
            type: 'GET',
            url: '/api/rs',
            data:  { cmd: encodeURIComponent(JSON.stringify(getCatalogTree))}
        }).done(function(data) {
            ServerActionCreators.getCatalogTreeResponse(data, null);
        }).fail(function(error) {
            ServerActionCreators.getCatalogTreeResponse(null, error);
        });
    },

    getCatalogProduct: function(rid) {
        var getCatalogProduct = {
            category: 'catalog',
            name: 'getCatalogProduct',
            readOnly: true,
            data: {
                pageSize: 10,
                pageNo: 1,
                '@rid': rid
            }
        };
        $.ajax({
            type: 'GET',
            url: '/api/rs',
            data:  { cmd: encodeURIComponent(JSON.stringify(getCatalogProduct))}
        }).done(function(data) {
            //console.log('product', data);
            ServerActionCreators.getCatalogProductResponse(data, null);

        }).fail(function(error) {
            //console.log('error', error);
            ServerActionCreators.getCatalogProductResponse(null, error);
        });
    },

    addProduct: function(action) {
        $.ajax({
            type: 'POST',
            url: '/api/rs',
            data: JSON.stringify(action),
            contentType: 'application/json',
            dataType: 'json'
        }).done(function(data) {
            ServerActionCreators.addProductResponse(data, null);
        }).fail(function(error) {
            ServerActionCreators.addProductResponse(null, error);
        });
    },

    updProduct: function(action) {
        $.ajax({
            type: 'POST',
            url: '/api/rs',
            data: JSON.stringify(action),
            contentType: 'application/json',
            dataType: 'json'
        }).done(function(data) {
            ServerActionCreators.updProductResponse(data, null);
        }).fail(function(error) {
            ServerActionCreators.updProductResponse(null, error);
        });
    },

    delProduct: function(rid) {
        let delPost = {
            category : 'blog',
            name : 'delPost',
            readOnly: false,
        };
        let data = {};
        data['@rid'] = rid;
        delPost.data = data;

        $.ajax({
            type: 'POST',
            url: '/api/rs',
            data: JSON.stringify(delPost),
            contentType: 'application/json',
            dataType: 'json'
        }).done(function(data) {
            ServerActionCreators.delProductResponse(data, null);
        }).fail(function(error) {
            ServerActionCreators.delProductResponse(null, error);
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

    updateShippingAddress: function(data) {
        var updAddress = {
            category: 'shipping',
            name: 'updAddress',
            readOnly: false,
            data: data
        };
        $.ajax({
            type: 'POST',
            url: '/api/rs',
            data: JSON.stringify(updAddress),
            contentType: 'application/json',
            dataType: 'json'
        }).done(function(data) {
            //console.log('updShippingAddress done', data);
            ServerActionCreators.updateShippingAddressResponse(data, null);
        }).fail(function(error) {
            //console.log('updShippingAddress error', error);
            ServerActionCreators.updateShippingAddressResponse(null, error);
        });
    },

    confirmShippingAddress: function(data) {
        var confirmAddress = {
            category: 'shipping',
            name: 'cnfAddress',
            readOnly: true,
            data: data
        };
        $.ajax({
            type: 'POST',
            url: '/api/rs',
            data: JSON.stringify(confirmAddress),
            contentType: 'application/json',
            dataType: 'json'
        }).done(function(data) {
            ServerActionCreators.confirmShippingAddressResponse(data, null);
        }).fail(function(error) {
            ServerActionCreators.confirmShippingAddressResponse(null, error);
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
    },

    submitForm: function(action) {
        console.log('WebAPIUtils submitForm is called', action);
        $.ajax({
            type: 'POST',
            url: '/api/rs',
            data: JSON.stringify(action),
            contentType: 'application/json',
            dataType: 'json'
        }).done(function(data) {
            ServerActionCreators.submitFormResponse(data, null);
        }).fail(function(error) {
            ServerActionCreators.submitFormResponse(null, error);
        });
    },

    execQueryCmd: function(action) {
        $.ajax({
            type: 'POST',
            url: '/api/rs',
            data: JSON.stringify(action),
            contentType: 'application/json',
            dataType: 'json'
        }).done(function(data) {
            ServerActionCreators.execQueryCmdResponse(data, null);
        }).fail(function(error) {
            ServerActionCreators.execQueryCmdResponse(null, error);
        });
    },

    downloadEvent: function(action) {
        $.ajax({
            type: 'POST',
            url: '/api/rs',
            data: JSON.stringify(action),
            contentType: 'application/json',
            dataType: 'json'
        }).done(function(data) {
            ServerActionCreators.downloadEventResponse(data, null);
        }).fail(function(error) {
            ServerActionCreators.downloadEventResponse(null, error);
        });
    }

};
