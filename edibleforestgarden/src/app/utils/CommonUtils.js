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

function findPost(posts, postId) {
    let post = null;
    for(var i = 0; i < posts.length; i++) {
        if(posts[i].postId === postId) {
            post = posts[i];
            break;
        }
    }
    return post;
}

function findProduct(products, productId) {
    let product = null;
    for(var i = 0; i < products.length; i++) {
        if(products[i].productId === productId) {
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

module.exports = {
    findCategory: findCategory,
    findPost: findPost,
    findProduct: findProduct,
    findMenuItem: findMenuItem,
    hasMenuAccess: hasMenuAccess
};