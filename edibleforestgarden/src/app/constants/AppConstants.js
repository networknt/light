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
var keyMirror = require('fbjs/lib/keyMirror');

module.exports = {
    APIRoot:  '/api/rs',
    ClientId: 'edibleforestgarden.ca@Browser',

    ActionTypes: keyMirror({
        // Server
        SERVER_ERROR_RESPONSE: null,

        // Auth
        LOGIN_REQUEST: null,
        LOGIN_RESPONSE: null,
        REFRESH: null,
        LOGOUT: null,
        INIT: null,
        SIGNUP_REQUEST: null,

        // Menu
        GET_MENU: null,
        GET_MENU_RESPONSE: null,

        // Role
        GET_ROLE_RESPONSE: null,

        // Access
        GET_ALL_ACCESS_RESPONSE: null,

        // Routes
        REDIRECT: null,

        // Tag
        GET_TAG_ENTITY_RESPONSE: null,

        // Blog
        GET_BLOG_TREE_RESPONSE: null,
        GET_BLOG_POST_RESPONSE: null,
        GET_RECENT_BLOG_POST_RESPONSE: null,
        GET_BLOG: null,
        GET_BLOG_RESPONSE: null,
        ADD_POST_RESPONSE: null,
        UPD_POST_RESPONSE: null,
        DEL_POST_RESPONSE: null,

        LOAD_BLOGS: null,
        LOAD_BLOG: null,
        RECEIVE_BLOG: null,
        CREATE_BLOG: null,
        RECEIVE_CREATED_BLOG: null,

        // News
        GET_NEWS_TREE_RESPONSE: null,
        GET_NEWS_POST_RESPONSE: null,
        GET_RECENT_NEWS_POST_RESPONSE: null,
        GET_NEWS_RESPONSE: null,


        // Forum
        GET_FORUM_TREE_RESPONSE: null,

        // Catalog
        GET_CATALOG_TREE: null,
        GET_CATALOG_TREE_RESPONSE: null,
        GET_CATALOG_PRODUCT_RESPONSE: null,
        ADD_PRODUCT_RESPONSE: null,
        UPD_PRODUCT_RESPONSE: null,
        DEL_PRODUCT_RESPONSE: null,
        ADD_PRODUCT_TO_CART: null,
        SET_PRODUCT_VARIANT: null, // set product variation
        SET_QTY: null,
        REMOVE_CART_ITEM: null,
        RESET_CART_ITEM: null,
        SET_PRODUCT_INVENTORY: null,
        REMOVE_ONE_FROM_INVENTORY: null,
        TOGGLE_CART: null, // Open/close cart
        GET_CATALOG_RESPONSE: null,

        SELECT_CATALOG: null,
        LOAD_PRODUCTS: null,
        RECEIVE_USER: null,

        // Form
        GET_FORM: null,
        RECEIVE_FORM: null,
        SUBMIT_FORM_RESPONSE: null,
        SET_FORM_MODEL: null,

        // Page
        GET_PAGE: null,
        GET_PAGE_RESPONSE: null,

        // Address
        UPDATE_SHIPPING_ADDRESS: null,
        UPDATE_SHIPPING_ADDRESS_RESPONSE: null,
        CONFIRM_SHIPPING_ADDRESS: null,
        CONFIRM_SHIPPING_ADDRESS_RESPONSE: null,

        // Payment
        GET_CLIENT_TOKEN: null,
        RECEIVE_CLIENT_TOKEN: null,
        ADD_TRANSACTION: null,
        RECEIVE_ADD_TRANSACTION: null,

        // Order
        ADD_ORDER: null,
        RECEIVE_ADD_ORDER: null,

        // Db
        EXEC_QUERY_CMD_RESPONSE: null,
        DOWNLOAD_EVENT_RESPONSE: null

    }),

    monthNames: ["January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    ]

};
