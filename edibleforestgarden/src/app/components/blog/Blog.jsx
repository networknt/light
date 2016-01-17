var React = require('react');
var WebAPIUtils = require('../../utils/WebAPIUtils');
var BlogStore = require('../../stores/BlogStore');
import BlogCategoryStore from '../../stores/BlogCategoryStore';
var BlogActionCreators = require('../../actions/BlogActionCreators');
var classNames = require('classnames');
import Paper from 'material-ui/lib/paper';
import Markdown from '../Markdown';
import RaisedButton from 'material-ui/lib/raised-button';
require('rc-pagination/assets/index.css');
import Pagination from 'rc-pagination';
import Locale from 'rc-pagination/lib/locale/en_US';
require('rc-select/assets/index.css');
import Select from 'rc-select';
import CommonUtils from '../../utils/CommonUtils';

var Blog = React.createClass({
    displayName: 'Blog',

    getInitialState: function() {
        let rid = null;
        if(BlogCategoryStore.getCategory().length  !== 0) {
            rid = BlogCategoryStore.getCategory()[0]['@rid'];
            if(this.props.params.categoryId) {
                //console.log('Blog._blogCategoryChange', rid, this.props.params.categoryId, BlogCategoryStore.getCategory());
                let category = CommonUtils.findCategory(BlogCategoryStore.getCategory(), this.props.params.categoryId);
                //console.log('Blog._blogCategoryChange category', category);
                rid = category['@rid'];
            }
        }
        return {
            posts: [],
            ancestors: [],
            allowPost: false,
            total: 0,
            pageSize: 10,
            pageNo: 1,
            rid: rid
        };
    },

    componentWillMount: function() {
        BlogStore.addChangeListener(this._onBlogChange);
        BlogCategoryStore.addChangeListener(this._blogCategoryChange);

        //console.log('Blog.componentWillMount', this.props.params.categoryId, BlogCategoryStore.getCategory());
        // need to make sure that category tree is loaded in case of bookmark.
        if(BlogCategoryStore.getCategory().length === 0) {
            BlogActionCreators.getBlogTree();
        } else {
            // lookup categoryRid from categoryId in params.
            let category = CommonUtils.findCategory(BlogCategoryStore.getCategory(), this.props.params.categoryId);
            //console.log('category', BlogCategoryStore.getCategory(), this.props.params.categoryId, category);
            BlogActionCreators.getBlogPost(category['@rid'], this.state.pageNo, this.state.pageSize);
        }
    },

    componentWillUnmount: function() {
        BlogStore.removeChangeListener(this._onBlogChange);
        BlogCategoryStore.removeChangeListener(this._blogCategoryChange);
    },

    _onBlogChange: function() {
        this.setState({
            ancestors: BlogStore.getAncestors(),
            allowPost: BlogStore.getAllowPost(),
            posts: BlogStore.getPosts(),
            total: BlogStore.getTotal()
        });
    },

    _blogCategoryChange: function() {
        // The Main doesn't care about the post loading anymore. the loading action always starts here.
        let rid = BlogCategoryStore.getCategory()[0]['@rid'];
        if(this.props.params.categoryId) {
            //console.log('Blog._blogCategoryChange', rid, this.props.params.categoryId, BlogCategoryStore.getCategory());
            let category = CommonUtils.findCategory(BlogCategoryStore.getCategory(), this.props.params.categoryId);
            //console.log('Blog._blogCategoryChange category', category);
            rid = category['@rid'];
        }
        this.setState({rid: rid});
        BlogActionCreators.getBlogPost(rid, this.state.pageNo, this.state.pageSize);
    },

    _routeToPost: function(postId) {
        this.props.history.push('/blog/' + this.props.params.categoryId + '/' + postId);
    },

    _onAddPost: function () {
        //console.log("_onAddPost is called");
        this.props.history.push('/blog/postAdd/' + this.props.params.categoryId);
    },

    _onPageNoChange: function (key) {
        //console.log("_onPageNoChange is called", key);
        this.setState({
            pageNo: key
        });
        // use key instead of this.state.pageNo as setState is async.
        BlogActionCreators.getBlogPost(this.state.rid, key, this.state.pageSize);
    },

    _onPageSizeChange: function (current, pageSize) {
        //console.log("_onPageSizeChange is called", current, pageSize);
        this.setState({
            pageSize: pageSize
        });
        BlogActionCreators.getBlogPost(this.state.rid, this.state.pageNo, pageSize);
    },

    render: function() {
        //console.log('total', this.state.total);
        let addButton = this.state.allowPost? <RaisedButton label="Add Post" primary={true} onTouchTap={this._onAddPost} /> : '';
        return (
            <div>
                <div className="blogHeader">
                    <h2>Blogs{addButton}</h2>
                </div>
                <div className="blogRoot">
                    <div className="leftColumn">
                        {
                            this.state.posts.map(function(post, index) {
                                var boundClick = this._routeToPost.bind(this, post.postId);
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
                        <Pagination locale={Locale} selectComponentClass={Select} showSizeChanger={true} pageSizeOptions={['10', '25', '50', '100']} onShowSizeChange={this._onPageSizeChange} onChange={this._onPageNoChange} current={this.state.pageNo} pageSize={this.state.pageSize} total={this.state.total}/>
                    </div>
                    <div className="rightColumn">
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
