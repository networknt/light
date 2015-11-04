var React =  require('react');
var BlogRow = require('./BlogRow');
var {List, Paper, ListItem} = require('material-ui');
var BlogStore = require('../../stores/BlogStore');
var BlogAction = require('../../actions/BlogActions');

var Blogs = React.createClass({

    getInitialState: function() {
        return {
            blogs: []
        }
    },

    componentDidMount: function() {
        BlogStore.addChangeListener(this._onChange);
        BlogAction.getBlogs();
    },

    _onChange: function() {
        this.setState({
            blogs: BlogStore.getBlogs()
        });
    },
    _createItems: function (blogs) {
        var children;
        if (blogs.out_Own) {
            children = blogs.out_Own.map(function (child) {
                return this._createItems(child);
            }.bind(this));
        }
        return (
            <ListItem
                leftAvatar={this._getLeftAvatar(blogs)}
                primaryText={this._getPrimaryText(blogs)}
                secondaryText={blogs.description}
                onTouchTap={this._onTouchTap}
                nestedItems={children}></ListItem>
        );
    },

    _getLeftAvatar: function(blogs) {
        var count = "0";
        if (blogs.out_HasPost != null && blogs.out_HasPost.length > 0) {
            count = blogs.out_HasPost.length.toString();
        }
        return (
            <div className="blogLeftAvatar">{count}</div>
        );
    },

    _getPrimaryText: function(blogs) {
        return (
            <p>
                {blogs.blogId}
            </p>
        );
    },

    render: function() {
        return (
            <div className="blogs">
                <div className="blogHeader">
                        <h2 className="mainBlogHeader">NetworkNt Blogs</h2>
                </div>

                <div className="blogsSection">
                    <div className="blogsDescription">
                        <h1>These are the blogs</h1>

                        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Aliquam commodi dolores, esse et id minus mollitia nemo, odio omnis qui rem voluptatem voluptatibus? Commodi dolorum fugit numquam quasi, similique soluta!</p>
                    </div>
                    <Paper className="blogsList">
                        <List>
                            {
                                this.state.blogs.map(function (blog) {
                                    return this._createItems(blog);
                                }.bind(this))
                            }
                        </List>
                    </Paper>
                </div>
            </div>
        );
    },

    componentWillUnmount: function() {
        BlogStore.removeChangeListener(this._onChange);
    }

});

module.exports = Blogs;