/**
 * Created by hus5 on 7/17/2015.
 */
var React = require('react');
var WebAPIUtils = require('../../utils/WebAPIUtils.js');
var BlogStore = require('../../stores/BlogStore.js');
var ErrorNotice = require('./ErrorNotice.js');
var BlogActionCreators = require('../../actions/BlogActionCreators.js');
var Router = require('react-router');
var Link = Router.Link;
var timeago = require('timeago');

var Blogs = React.createClass({

    getInitialState: function() {
        return {
            blogs: BlogStore.getAllBlogs(),
            errors: []
        };
    },

    componentDidMount: function() {
        BlogStore.addChangeListener(this._onChange);
        BlogActionCreators.loadBlogs();
    },

    componentWillUnmount: function() {
        BlogStore.removeChangeListener(this._onChange);
    },

    _onChange: function() {
        this.setState({
            blogs: BlogStore.getAllBlogs(),
            errors: BlogStore.getErrors()
        });
    },

    render: function() {
        var errors = (this.state.errors.length > 0) ? <ErrorNotice errors={this.state.errors}/> : <div></div>;
        return (
            <div>
                {errors}
                <div className="row">
                    <BlogsList blogs={this.state.blogs} />
                </div>
            </div>
        );
    }
});

var BlogItem = React.createClass({
    render: function() {
        console.log('blogItem', this.props.blog);
        return (
            <li className="blog">
                <div className="blog__title">
                    <Link to="blog" params={ {blogId: this.props.blog.id} }>
            {this.props.blog.label}
                    </Link>
                </div>
                <div className="blog__body">{this.props.blog.value}</div>
            </li>
        );
    }
});

var BlogsList = React.createClass({
    render: function() {
        console.log('BlogList blogs ', this.props.blogs);
        return (
            <ul className="large-8 medium-10 small-12 small-centered columns">
        {this.props.blogs.map(function(blog, index){
            return <BlogItem blog={blog} key={"blog-" + index}/>
        })}
            </ul>
        );
    }
});

module.exports = Blogs;
