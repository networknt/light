var React = require('react');
var WebAPIUtils = require('../../utils/WebAPIUtils');
var BlogStore = require('../../stores/BlogStore');
var BlogActionCreators = require('../../actions/BlogActionCreators');
var classNames = require('classnames');
import Paper from 'material-ui/lib/paper';
import Markdown from '../Markdown';
import RaisedButton from 'material-ui/lib/raised-button';


var Blog = React.createClass({
    displayName: 'Blog',

    getInitialState: function() {
        return {
            blogPosts: [],
            ancestors: [],
            allowPost: false,
            total: 0
        };
    },

    componentWillMount: function() {
        BlogStore.addChangeListener(this._onBlogChange);
        BlogActionCreators.getBlogPost("#" + this.props.params.blogRid);
    },

    componentWillUnmount: function() {
        BlogStore.removeChangeListener(this._onBlogChange);
    },

    _onBlogChange: function() {
        this.setState({
            ancestors: BlogStore.getAncestors(),
            allowPost: BlogStore.getAllowPost(),
            blogPosts: BlogStore.getBlogPosts(),
            total: BlogStore.getTotal
        });
    },

    _routeToPost: function(index) {
        console.log('_routeToPost', this.props.params.blogRid, index, this.props.history);
        this.props.history.push('/blog/post' + this.props.params.blogRid + '/' + index);
    },

    _onAddPost: function () {
        console.log("_onAddPost is called");
        this.props.history.push('/blogPostAdd/' + this.props.params.blogRid);
    },

    render: function() {
        let addButton = this.state.allowPost? <RaisedButton label="Add Post" primary={true} onTouchTap={this._onAddPost} /> : '';
        return (
            <div>
                <div className="blogHeader">
                    <h2>Blogs{addButton}</h2>
                </div>
                <div className="blogRoot">
                    <div className="blogLeftColumn">
                        {
                            this.state.blogPosts.map(function(post, index) {
                                var boundClick = this._routeToPost.bind(this, index);
                                return (
                                    <span key={index}>
                                        <Paper className="blogPostPaper">
                                            <div className="blogPost">
                                                <h2 className="title"><a onClick={boundClick}>{post.title}</a></h2>
                                                <span>Submitted by {post.createUserId} on {post.createDate}</span>
                                                <Markdown text={post.summary} />
                                            </div>
                                        </Paper>
                                        <hr />
                                    </span>
                                );
                            }, this)
                        }
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

/*
 <BlogPostList blogRid={this.props.params.blogRid} blogPosts={this.state.blogPosts} ancestors={this.state.ancestors} allowUpdate={this.state.allowUpdate}/>



 */