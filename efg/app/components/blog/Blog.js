var React =  require('react');
var {Link} = require('react-router');
var BlogStore = require('../../stores/BlogStore');
var BlogActions = require('../../actions/BlogActions');
var {List, ListItem, Paper, RaisedButton} = require('material-ui');
var AppConstants = require('../../constants/AppConstants');

var Navigation = require('react-router').Navigation;

var Blog = React.createClass({
    mixins: [Navigation],

    componentDidMount: function() {
        BlogStore.addChangeListener(this._receiveBlogPosts);
        BlogActions.getBlogPosts("#" + this.props.params.blogRid);
    },

    getInitialState: function() {
        return {
            blogPosts: []
        };
    },

    _receiveBlogPosts: function() {
        this.setState({
            blogPosts: BlogStore.getBlogPosts()
        })
    },
    _routeToPost: function(postRid) {
        this.transitionTo("/blog/" + this.props.params.blogRid + "/" + postRid.substring(1));
    },

    render: function() {
        return (
            <div>
                <div className="blogHeader">
                    <h2 className="mainBlogHeader">NetworkNt Blogs</h2>
                </div>
                <div className="blogRoot">
                    <div className="blogPostsRoot">
                        <div className="blogPostsleftColumn">
                            {
                                this.state.blogPosts.map(function(post) {
                                    var date = new Date(post.createDate);
                                    var boundClick = this._routeToPost.bind(this, post.rid);
                                    return (
                                        <span>
                                            <Paper className="blogPostsPaper">
                                                <div className="blogPost">
                                                    <h2>
                                                        <strong className="strongDate">{AppConstants.monthNames[date.getMonth()]} {date.getDay()},</strong> <span className="year">{date.getFullYear()}</span>
                                                    </h2>
                                                    <h1 className="title"><a onClick={boundClick}>{post.title}</a></h1>
                                                    <p className="content">
                                                        {post.content}
                                                    </p>
                                                </div>
                                            </Paper>
                                            <hr />
                                        </span>
                                    );
                                }, this)
                            }
                        </div>
                        <div className="blogPostsRightColumn">
                            <div className="blogInfo">
                                <h1>Blog Information</h1>
                                <p>In this section, you will see some information and references pertaining to the opened blog.</p>
                                <p>Also, having the screen width be less then 64em will hide it, leaving reading room for mobile users only concerned with reading post content on the go.</p>
                                <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ad adipisci alias cum, cumque cupiditate ea eum itaque, minus molestias necessitatibus nihil pariatur perspiciatis quam quas quod rem repellat, sint voluptate.</p>
                            </div>
                        </div>
                    </div>

                    <Link to="/blog">
                        <RaisedButton label="Back"/>
                    </Link>
                </div>
            </div>
        );
    }
});

module.exports = Blog;