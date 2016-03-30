'use strict';
/**
    This file contains all the utility functions used by the front end code.
  */
function findCategory(category, categoryId) {
    for(var i = 0; i < category.length; i++) {
        if(category[i].categoryId === categoryId) return category[i];
        if(typeof category[i].out_Own !== 'undefined' && category[i].out_Own.length > 0) {
            return findCategory(category[i].out_Own, categoryId);
        }
    }
    return null;
}

function findPost(posts, entityId) {
    let post = null;
    for(var i = 0; i < posts.length; i++) {
        if(posts[i].entityId === entityId) {
            post = posts[i];
            break;
        }
    }
    return post;
}

function findProduct(products, entityId) {
    let product = null;
    for(var i = 0; i < products.length; i++) {
        if(products[i].entityId === entityId) {
            product = products[i];
            break;
        }
    }
    return product;
}

function findMenuItem(menuItems, menuItemId) {
    let menuItem = null;
    for(var i = 0; i < menuItems.length; i++) {
        if(menuItems[i].menuItemId === menuItemId) {
            menuItem = menuItems[i];
            break;
        }
    }
    return menuItem;
}


function hasMenuAccess(menuItem, roles) {
    for (var i = 0; i < roles.length; i++) {
        for (var j = 0; j < menuItem.roles.length; j++) {
            if (roles[i] == menuItem.roles[j]) {
                return true;
            }
        }
    }
    return false;
}


function getCommentCountString(numComments) {
    var commentString = '';
    commentString += numComments + ' comment';

    if (numComments !== 1) {
        commentString += 's';
    }

    return commentString;
}


module.exports = {
    findCategory: findCategory,
    findPost: findPost,
    findProduct: findProduct,
    findMenuItem: findMenuItem,
    hasMenuAccess: hasMenuAccess,
    getCommentCountString: getCommentCountString
};