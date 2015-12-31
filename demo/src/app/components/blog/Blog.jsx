var React = require('react');
var BlogPostList = require('./BlogPostList');
var WebAPIUtils = require('../../utils/WebAPIUtils');
var BlogStore = require('../../stores/BlogStore');
var BlogActionCreators = require('../../actions/BlogActionCreators');
var classNames = require('classnames');

var Blog = React.createClass({
    displayName: 'Blog',

    getInitialState: function() {
        return {
            blogPosts: [
                {
                    "createUserId":"stevehu",
                    "postId":"GT3ZmMIxTxiE8WzGudooQA",
                    "createRid":"#14:0",
                    "rid":"#36:11",
                    "title":"TEst",
                    "content":"This is the post - Edited -5/21/2015",
                    "parentId":"asdga",
                    "createDate":"2015-05-02T14:20:13.512"
                },{
                    "createUserId":"stevehu",
                    "postId":"uGRKOudDR4e3EGwrNYi6cQ",
                    "createRid":"#14:0",
                    "rid":"#36:10",
                    "title":"jdfgjdfgjd",
                    "content":"fgjdfgj",
                    "parentId":"asdga",
                    "createDate":"2015-05-02T12:57:12.315"
                },{
                    "createUserId":"stevehu",
                    "postId":"FY4pUJ1CSzSURhihDlLg5g",
                    "createRid":"#14:0",
                    "rid":"#36:9",
                    "title":"sdfhsdfhsdf",
                    "content":"hsdfhsdfhsdf",
                    "parentId":"asdga",
                    "createDate":"2015-05-02T12:55:51.351"
                }
            ],
            ancestors: [],
            allowUpdate: false
        };
    },

    componentWillMount: function() {
        BlogStore.addChangeListener(this._onBlogChange);
        BlogActionCreators.getBlogTree();
    },

    componentWillUnmount: function() {
        BlogStore.removeChangeListener(this._onBlogChange);
    },

    _onBlogChange: function() {
        this.setState({
            ancestors: BlogStore.getAncestors(),
            allowUpdate: BlogStore.getAllowUpdate(),
            blogPosts: BlogStore.getBlogPosts()
        });
    },

    render: function() {
        return (
            <div>
                <div className="blogHeader">
                    <h2>Blogs</h2>
                </div>
                <div className="blogRoot">
                    <div className="blogLeftColumn">
                        <BlogPostList blogPosts={this.state.blogPosts} ancestors={this.state.ancestors} allowUpdate={this.state.allowUpdate}/>
                    </div>
                    <div className="blogRightColumn">
                        <div className="blogInfo">
                            <h1>Blog Information</h1>
                            <p>In this section, you will see some information and references pertaining to the opened blog.</p>
                            <p>Also, having the screen width be less then 64em will hide it, leaving reading room for mobile users only concerned with reading post content on the go.</p>
                            <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ad adipisci alias cum, cumque cupiditate ea eum itaque, minus molestias necessitatibus nihil pariatur perspiciatis quam quas quod rem repellat, sint voluptate.</p>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
});

module.exports = Blog;
