var React = require('react');
var WebAPIUtils = require('../../utils/WebAPIUtils');
var BlogStore = require('../../stores/BlogStore');
import BlogCategoryStore from '../../stores/BlogCategoryStore';
var BlogActionCreators = require('../../actions/BlogActionCreators');
var classNames = require('classnames');
import RaisedButton from 'material-ui/lib/raised-button';
require('rc-pagination/assets/index.css');
import Pagination from 'rc-pagination';
import Locale from 'rc-pagination/lib/locale/en_US';
require('rc-select/assets/index.css');
import Select from 'rc-select';
import CommonUtils from '../../utils/CommonUtils';

// Using the general Summary component for News/Blog/Forum summaries.
// import BlogSummary from './BlogSummary';
import Summary from '../common/Summary.jsx';

var BlogRecentPost = React.createClass({
    displayName: 'BlogRecentPost',

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: function() {
        return {
            posts: [],
            total: 0,
            pageSize: 10,
            pageNo: 1
        };
    },

    componentWillMount: function() {
        BlogStore.addChangeListener(this._onBlogChange);
        BlogActionCreators.getRecentBlogPost(this.state.pageNo, this.state.pageSize);
    },

    componentWillUnmount: function() {
        BlogStore.removeChangeListener(this._onBlogChange);
    },

    _onBlogChange: function() {
        this.setState({
            posts: BlogStore.getPosts(),
            total: BlogStore.getTotal()
        });
    },

    _routeToPost: function(categoryId, entityId) {
        console.log('BlogRecentPost', categoryId, entityId);
        this.context.router.push('/blog/' + categoryId + '/' + entityId);
    },

    _onPageNoChange: function (key) {
        this.setState({
            pageNo: key
        });
        // use key instead of this.state.pageNo as setState is async.
        BlogActionCreators.getRecentBlogPost(key, this.state.pageSize);
    },

    _onPageSizeChange: function (current, pageSize) {
        //console.log("_onPageSizeChange is called", current, pageSize);
        this.setState({
            pageSize: pageSize
        });
        BlogActionCreators.getRecentBlogPost(this.state.pageNo, pageSize);
    },

    render: function() {
        return (
            <div>
                <div className="header">
                    <h2 className="headerContent">Recent Blog Posts</h2>
                </div>
                <div className="leftColumn">
                    {
                        this.state.posts.map(function(post, index) {
                            var boundClick = this._routeToPost.bind(this, post.parentId, post.entityId);
                            return (
                                <span key={index}>
                                    <Summary post={post} onClick={boundClick} />
                                </span>
                            );
                        }, this)
                    }
                    <Pagination locale={Locale} selectComponentClass={Select} showSizeChanger={true} pageSizeOptions={['10', '25', '50', '100']} onShowSizeChange={this._onPageSizeChange} onChange={this._onPageNoChange} current={this.state.pageNo} pageSize={this.state.pageSize} total={this.state.total}/>
                </div>
            </div>
        );
    }
});

module.exports = BlogRecentPost;
