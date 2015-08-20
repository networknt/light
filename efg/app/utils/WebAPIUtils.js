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
        console.log('login in WebAPIUtils is been called');

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


        console.log('login', signIn);
        $.ajax({
            type: 'POST',
            contentType: 'application/json',
            url: '/api/rs',
            data: JSON.stringify(signIn),
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
        var getMenu = {
            category : 'menu',
            name : 'getMenu',
            readOnly: true,
            data : {
                host : Host
            }

        }
        console.log('WebAPIUtils loadMenus is called');
        $.ajax({
            type: 'POST',
            url: '/api/rs',
            data: JSON.stringify(getMenu),
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
            url: '/api/rs',
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
    },

    loadCatalog: function() {
        var data = [{
            "id": 1,
            "name": "All Categories",
            "children": [
                {
                    "id": 2,
                    "name": "For Sale",
                    "children": [
                        {
                            "id": 3,
                            "name": "Audio & Stereo"
                        },
                        {
                            "id": 4,
                            "name": "Baby & Kids Stuff"
                        },
                        {
                            "id": 5,
                            "name": "Music, Films, Books & Games"
                        }
                    ]
                },
                {
                    "id": 6,
                    "name": "Motors",
                    "children": [
                        {
                            "id": 7,
                            "name": "Car Parts & Accessories"
                        },
                        {
                            "id": 8,
                            "name": "Cars"
                        },
                        {
                            "id": 13,
                            "name": "Motorbike Parts & Accessories"
                        }
                    ]
                },
                {
                    "id": 9,
                    "name": "Jobs",
                    "children": [
                        {
                            "id": 10,
                            "name": "Accountancy"
                        },
                        {
                            "id": 11,
                            "name": "Financial Services & Insurance"
                        },
                        {
                            "id": 12,
                            "name": "Bar Staff & Management"
                        }
                    ]
                }
            ]
        },
        {
            "id": 20,
            "name": "My Categories",
            "children": [
                {
                    "id": 21,
                    "name": "For Sale",
                    "children": [
                        {
                            "id": 23,
                            "name": "Audio & Stereo"
                        },
                        {
                            "id": 24,
                            "name": "Baby & Kids Stuff"
                        },
                        {
                            "id": 25,
                            "name": "Music, Films, Books & Games"
                        }
                    ]
                },
                {
                    "id": 26,
                    "name": "Motors",
                    "children": [
                        {
                            "id": 27,
                            "name": "Car Parts & Accessories"
                        },
                        {
                            "id": 28,
                            "name": "Cars"
                        },
                        {
                            "id": 33,
                            "name": "Motorbike Parts & Accessories"
                        }
                    ]
                },
                {
                    "id": 39,
                    "name": "Jobs",
                    "children": [
                        {
                            "id": 30,
                            "name": "Accountancy"
                        },
                        {
                            "id": 31,
                            "name": "Financial Services & Insurance"
                        },
                        {
                            "id": 32,
                            "name": "Bar Staff & Management"
                        }
                    ]
                }
            ]
        }
        ];
        ServerActionCreators.receiveCatalog(data, null);
    },

    /*
    loadCatalog: function() {
        var getCatalogTree = {
            category: 'catalog',
            name: 'getCatalogTree',
            readOnly: true,
            data: {
                host: AppConstants.host
            }
        };

        console.log('WebAPIUtils loadCatalog is called', getCatalogTree);
        $.ajax({
            type: 'GET',
            url: '/api/rs',
            data:  { cmd: encodeURIComponent(JSON.stringify(getCatalogTree))}
        }).done(function(data) {
            console.log('done', data);
            ServerActionCreators.receiveCatalog(data, null);

        }).fail(function(error) {
            console.log('error', error);
            ServerActionCreators.receiveCatalog(null, error);
        });
    },
    */

    loadProducts: function() {
        var getBlogs = {
            category: 'demo',
            name: 'getDropdown',
            readOnly: true
        }
        console.log('WebAPIUtils logBlogs is called');
        $.ajax({
            type: 'GET',
            url: '/api/rs',
            data:  { cmd: encodeURIComponent(JSON.stringify(getBlogs))}
        }).done(function(data) {
            console.log('done', data);
            ServerActionCreators.receiveBlogs(data, null);

        }).fail(function(error) {
            console.log('error', error);
            ServerActionCreators.receiveBlogs(null, error);
        });
    },

    /**
     * Simulate retreiving data from an database
     */
    getAllProducts: function() {
        // Fetch
        var products =
            [
                {
                    "id": 0,
                    "title": "Silver beet",
                    "description": "Silver beet shallot wakame tomatillo salsify mung bean beetroot groundnut.",
                    "image": "1.png",
                    "variants": [
                        {
                            "sku": 429718,
                            "price": 19.99,
                            "type": "22oz Bottle",
                            "inventory": 8
                        },
                        {
                            "sku": 754473,
                            "price": 29.99,
                            "type": "40oz Bottle",
                            "inventory": 3
                        }
                    ]
                },
                {
                    "id": 1,
                    "title": "Wattle seed",
                    "description": "Wattle seed bunya nuts spring onion okra garlic bitterleaf zucchini.",
                    "image": "2.png",
                    "variants": [
                        {
                            "sku": 122057,
                            "price": 22.99,
                            "type": "22oz Bottle",
                            "inventory": 7
                        }
                    ]
                },
                {
                    "id": 2,
                    "title": "Kohlrabi bok",
                    "description": "Kohlrabi bok choy broccoli rabe welsh onion spring onion tatsoi ricebean kombu chard.",
                    "image": "3.png",
                    "variants": [
                        {
                            "sku": 620515,
                            "price": 5.99,
                            "type": "22oz Bottle",
                            "inventory": 1
                        },
                        {
                            "sku": 336426,
                            "price": 15.99,
                            "type": "40oz Bottle",
                            "inventory": 7
                        }
                    ]
                },
                {
                    "id": 3,
                    "title": "Melon sierra",
                    "description": "Melon sierra leone bologi carrot peanut salsify celery onion jícama summer purslane.",
                    "image": "4.png",
                    "variants": [
                        {
                            "sku": 193901,
                            "price": 12.99,
                            "type": "22oz Bottle",
                            "inventory": 1
                        }
                    ]
                },
                {
                    "id": 4,
                    "title": "Celery carrot",
                    "description": "Celery carrot napa cabbage wakame zucchini celery chard beetroot jícama sierra leone.",
                    "image": "5.png",
                    "variants": [
                        {
                            "sku": 963029,
                            "price": 15,
                            "type": "22oz Bottle",
                            "inventory": 4
                        },
                        {
                            "sku": 702318,
                            "price": 25,
                            "type": "40oz Bottle",
                            "inventory": 8
                        }
                    ]
                },
                {
                    "id": 5,
                    "title": "Catsear",
                    "description": "Catsear cabbage tomato parsnip cucumber pea brussels sprout spring onion shallot swiss .",
                    "image": "6.png",
                    "variants": [
                        {
                            "sku": 117159,
                            "price": 20,
                            "type": "22oz Bottle",
                            "inventory": 5
                        }
                    ]
                },
                {
                    "id": 6,
                    "title": "Mung bean",
                    "description": "Mung bean taro chicory spinach komatsuna fennel.",
                    "image": "7.png",
                    "variants": [
                        {
                            "sku": 352054,
                            "price": 10,
                            "type": "22oz Bottle",
                            "inventory": 5
                        }
                    ]
                },
                {
                    "id": 7,
                    "title": "Epazote",
                    "description": "Epazote soko chickpea radicchio rutabaga desert raisin wattle seed coriander water.",
                    "image": "8.png",
                    "variants": [
                        {
                            "sku": 801488,
                            "price": 34.99,
                            "type": "22oz Bottle",
                            "inventory": 8
                        },
                        {
                            "sku": 151953,
                            "price": 44.99,
                            "type": "40oz Bottle",
                            "inventory": 8
                        }
                    ]
                },
                {
                    "id": 8,
                    "title": "Tatsoi caulie",
                    "description": "Tatsoi caulie broccoli rabe bush tomato fava bean beetroot epazote salad grape.",
                    "image": "9.png",
                    "variants": [
                        {
                            "sku": 316395,
                            "price": 21.50,
                            "type": "22oz Bottle",
                            "inventory": 7
                        },
                        {
                            "sku": 101746,
                            "price": 31.50,
                            "type": "40oz Bottle",
                            "inventory": 6
                        }
                    ]
                },
                {
                    "id": 9,
                    "title": "Endive okra",
                    "description": "Endive okra chard desert raisin prairie turnip cucumber maize avocado.",
                    "image": "10.png",
                    "variants": [
                        {
                            "sku": 510772,
                            "price": 18.50,
                            "type": "22oz Bottle",
                            "inventory": 4
                        },
                        {
                            "sku": 353573,
                            "price": 28.50,
                            "type": "40oz Bottle",
                            "inventory": 8
                        }
                    ]
                },
                {
                    "id": 10,
                    "title": "Bush tomato",
                    "description": "Bush tomato peanut shallot turnip prairie turnip gram desert raisin.",
                    "image": "1.png",
                    "variants": [
                        {
                            "sku": 361028,
                            "price": 9,
                            "type": "22oz Bottle",
                            "inventory": 4
                        },
                        {
                            "sku": 807128,
                            "price": 19,
                            "type": "40oz Bottle",
                            "inventory": 3
                        }
                    ]
                },
                {
                    "id": 11,
                    "title": "Yarrow leek",
                    "description": "Yarrow leek cabbage amaranth onion salsify caulie kale desert raisin prairie turnip garlic.",
                    "image": "2.png",
                    "variants": [
                        {
                            "sku": 351541,
                            "price": 22.50,
                            "type": "22oz Bottle",
                            "inventory": 2
                        },
                        {
                            "sku": 609016,
                            "price": 32.50,
                            "type": "40oz Bottle",
                            "inventory": 4
                        }
                    ]
                }
            ];

        // Simulated callback
        ServerActionCreators.receiveAll(products);
    }


};
