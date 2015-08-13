/*
 * Copyright 2015 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
var keyMirror = require('keymirror');
var host = 'example';

module.exports = {

    APIRoot:  '/api/rs',

    ClientId: 'example@Browser',

    APIEndpoints: {
        SIGNIN:         {
            category : 'user',
            name : 'signInUser',
            readOnly: false
        },
        REGISTRATION:   {

        },
        GETMENU: {
            category : 'menu',
            name : 'getMenu',
            readOnly: true,
            data : {
                host : host
            }
        }
    },

    ActionTypes: keyMirror({
        // Auth
        LOGIN_REQUEST: null,
        LOGIN_RESPONSE: null,
        REFRESH: null,
        LOGOUT: null,
        INIT: null,
        SIGNUP_REQUEST: null,

        // Menu
        LOAD_MENU: null,
        RECEIVE_MENU: null,
        // Routes
        REDIRECT: null,

        // Blog
        LOAD_BLOGS: null,
        RECEIVE_BLOGS: null,
        LOAD_BLOG: null,
        RECEIVE_BLOG: null,
        CREATE_BLOG: null,
        RECEIVE_CREATED_BLOG: null,

        // Commerce
        RECEIVE_ALL_PRODUCTS: null, // load mock products
        ADD_PRODUCT_TO_CART: null,
        SET_PRODUCT_VARIANT: null, // set product variation
        SET_QTY: null,
        REMOVE_CART_ITEM: null,
        SET_PRODUCT_INVENTORY: null,
        REMOVE_ONE_FROM_INVENTORY: null,
        TOGGLE_CART: null // Open/close cart
    })

};
