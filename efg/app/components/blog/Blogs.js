var React =  require('react');
var BlogRow = require('./BlogRow');
var {List, Paper} = require('material-ui');
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
                                    return (
                                        <BlogRow blog={blog}></BlogRow>
                                    );
                                })
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