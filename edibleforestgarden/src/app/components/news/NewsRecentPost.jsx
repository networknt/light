var React = require('react');
var WebAPIUtils = require('../../utils/WebAPIUtils');
var NewsStore = require('../../stores/NewsStore');
import NewsCategoryStore from '../../stores/NewsCategoryStore';
var NewsActionCreators = require('../../actions/NewsActionCreators');
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

var NewsRecentPost = React.createClass({
    displayName: 'NewsRecentPost',

    getInitialState: function() {
        return {
            posts: [],
            total: 0,
            pageSize: 10,
            pageNo: 1
        };
    },

    componentWillMount: function() {
        NewsStore.addChangeListener(this._onNewsChange);
        NewsActionCreators.getRecentNewsPost(this.state.pageNo, this.state.pageSize);
    },

    componentWillUnmount: function() {
        NewsStore.removeChangeListener(this._onNewsChange);
    },

    _onNewsChange: function() {
        this.setState({
            posts: NewsStore.getPosts(),
            total: NewsStore.getTotal()
        });
    },

    _routeToPost: function(categoryId, entityId) {
        this.props.history.push('/news/' + categoryId + '/' + entityId);
    },

    _onPageNoChange: function (key) {
        this.setState({
            pageNo: key
        });
        // use key instead of this.state.pageNo as setState is async.
        NewsActionCreators.getRecentNewsPost(key, this.state.pageSize);
    },

    _onPageSizeChange: function (current, pageSize) {
        //console.log("_onPageSizeChange is called", current, pageSize);
        this.setState({
            pageSize: pageSize
        });
        NewsActionCreators.getRecentNewsPost(this.state.pageNo, pageSize);
    },

    render: function() {
        return (
            <div>
                <div className="blogHeader">
                    <h2>Recent News Posts</h2>
                </div>
                <div className="blogRoot">
                    <div className="leftColumn">
                        {
                            this.state.posts.map(function(post, index) {
                                var boundClick = this._routeToPost.bind(this, post.parentId, post.entityId);
                                return (
                                    <span key={index}>
                                        <Paper className="blogPostPaper">
                                            <div className="blogPost">
                                                <h2 className="title"><a onClick={boundClick}>{post.title}</a></h2>
                                                <span>Submitted by {post.createUserId} on {post.createDate}</span>
                                                <Markdown text={post.summary} />
                                            </div>
                                        </Paper>
                                    </span>
                                );
                            }, this)
                        }
                        <Pagination locale={Locale} selectComponentClass={Select} showSizeChanger={true} pageSizeOptions={['10', '25', '50', '100']} onShowSizeChange={this._onPageSizeChange} onChange={this._onPageNoChange} current={this.state.pageNo} pageSize={this.state.pageSize} total={this.state.total}/>
                    </div>
                </div>
            </div>
        );
    }
});

module.exports = NewsRecentPost;
