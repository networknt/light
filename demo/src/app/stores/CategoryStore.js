var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var ActionTypes = AppConstants.ActionTypes;
var CHANGE_EVENT = 'change';

var _category = [
    {
        "@rid": "#43:0",
        "host": "example",
        "description": "Computer Component",
        "categoryId": "computer",
        "createDate": "2015-09-25T02:32:54.765",
        "out_Own": [
            {
                "@rid": "#43:1",
                "host": "example",
                "description": "Computer Case",
                "categoryId": "case",
                "createDate": "2015-09-25T02:33:25.915",
                "in_Own": [
                    "#43:0"
                ],
                "out_Own": [
                    {
                        "@rid": "#43:3",
                        "host": "example",
                        "description": "Desktop Case",
                        "categoryId": "desktopCase",
                        "createDate": "2015-09-25T02:34:11.850",
                        "in_Own": [
                            "#43:1"
                        ]
                    },
                    {
                        "@rid": "#43:4",
                        "host": "example",
                        "description": "Server Case",
                        "categoryId": "serverCase",
                        "createDate": "2015-09-25T02:34:29.776",
                        "in_Own": [
                            "#43:1"
                        ]
                    }
                ]
            },
            {
                "@rid": "#43:2",
                "host": "example",
                "description": "Hard Drive",
                "categoryId": "hardDrive",
                "createDate": "2015-09-25T02:33:49.007",
                "in_Own": [
                    "#43:0"
                ]
            }
        ]
    }
];

var _errors = [];


var CategoryStore = assign({}, EventEmitter.prototype, {

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    },

    getCategory: function() {
        return _category;
    },

    getErrors: function() {
        return _errors;
    }

});

CategoryStore.dispatchToken = AppDispatcher.register(function(payload) {
    var type = payload.type;
    switch(type) {
        case ActionTypes.GET_CATEGORY_TREE_RESPONSE:
            //console.log('FormStore RECEIVE_FORM', payload.json);
            //console.log('FormStore RECEIVE_FORM', payload.json.formId);
            //console.log('FormStore RECEIVE_FORM', _forms);
            _category = payload.json;
            CategoryStore.emitChange();
            break;
    }
    return true;
});

module.exports = CategoryStore;
