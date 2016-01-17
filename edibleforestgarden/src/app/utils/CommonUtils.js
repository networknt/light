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
        }
    }
    return post;
}

function findProduct(products, productId) {
    let product = null;
    for(var i = 0; i < products.length; i++) {
        if(products[i].productId === productId) {
            product = products[i];
        }
    }
    return product;
}

module.exports = {
    findCategory: findCategory,
    findPost: findPost,
    findProduct: findProduct

};