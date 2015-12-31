'use strict';

var React = require('react');
var BlogPost = require('./BlogPost');
var Ancestor = require('../Ancestor');

function getBlogPost(blogPost, index) {
    return (
        <BlogPost blogPost={blogPost} key={index} />
    );
}

function getAncestor(ancestor, index) {
    return (
        <Ancestor ancestor={ancestor} key={index} />
    );
}

var BlogPostList = React.createClass({
    displayName: 'PostList',

    render: function() {
        var blogPosts = Object.keys(this.props.blogPosts).length ===0 ? '' : this.props.blogPosts.map(getBlogPost, this);
        var ancestors = Object.keys(this.props.ancestors).length ===0 ? '' : this.props.ancestors.map(getAncestor, this);
        return (
            <div>
                {ancestors}
                <div className="blogPostList">
                    {blogPosts}
                </div>
            </div>
        )
    }

});

module.exports = BlogPostList;
