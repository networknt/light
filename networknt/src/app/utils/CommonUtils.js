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

function getAgeString(age) {
    var bannerAge = '';

    if (age.seconds === undefined) {
        age.seconds = 0;
    }

    // Format the age accordingly
    if (age.days) {
        bannerAge = age.days;

        if (age.days === 1) {
            bannerAge += ' day ';
        } else {
            bannerAge += ' days ';
        }
    }
    else if (age.hours) {
        bannerAge = age.hours;

        if (age.hours === 1) {
            bannerAge = ' an hour ';
        } else {
            bannerAge += ' hours ';
        }
    }
    else if (age.minutes) {
        bannerAge = age.minutes;

        if (age.minutes === 1) {
            bannerAge += ' minute ';
        } else {
            bannerAge += ' minutes ';
        }
    } else {
        bannerAge = age.seconds;

        if (age.seconds === 1) {
            bannerAge += ' second ';
        } else {
            bannerAge += ' seconds ';
        }
    }

    return bannerAge;
}

function getCommentCountString(numComments) {
    var commentString = '';
    commentString += numComments + ' comment';

    if (numComments !== 1) {
        commentString += 's';
    }

    return commentString;
}

function findAndUpdateUpvoted(currArr, targetId, isUpvoting) {
    for (var i = 0, len = currArr.length; i < len; i++) {
        if (currArr[i].id === targetId) {
            currArr[i].upvoted = isUpvoting;

            if (isUpvoting) {
                currArr[i].score++;
            } else {
                currArr[i].score--;
            }
        }
    }

    return currArr;
}

module.exports = {
    findCategory: findCategory,
    findPost: findPost,
    findProduct: findProduct,
    findMenuItem: findMenuItem,
    hasMenuAccess: hasMenuAccess,
    getAgeString: getAgeString,
    getCommentCountString: getCommentCountString,
    findAndUpdateUpvoted: findAndUpdateUpvoted
};