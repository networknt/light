var React = require('react');
var WebAPIUtils = require('../../utils/WebAPIUtils');
var ForumStore = require('../../stores/ForumStore');
import ForumCategoryStore from '../../stores/ForumCategoryStore';
var ForumActionCreators = require('../../actions/ForumActionCreators');
var classNames = require('classnames');
import RaisedButton from 'material-ui/lib/raised-button';
require('rc-pagination/assets/index.css');
import Pagination from 'rc-pagination';
import Locale from 'rc-pagination/lib/locale/en_US';
require('rc-select/assets/index.css');
import Select from 'rc-select';
import CommonUtils from '../../utils/CommonUtils';

// Using the general Summary component for News/Blog/Forum summaries.
// import ForumSummary from './ForumSummary';
import Summary from '../common/Summary.jsx';

var ForumRecentPost = React.createClass({
    displayName: 'ForumRecentPost',

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
        ForumStore.addChangeListener(this._onForumChange);
        ForumActionCreators.getRecentForumPost(this.state.pageNo, this.state.pageSize);
    },

    componentWillUnmount: function() {
        ForumStore.removeChangeListener(this._onForumChange);
    },

    _onForumChange: function() {
        this.setState({
            posts: ForumStore.getPosts(),
            total: ForumStore.getTotal()
        });
    },

    _routeToPost: function(categoryId, entityId) {
        console.log('ForumRecentPost', categoryId, entityId);
        this.context.router.push('/forum/' + categoryId + '/' + entityId);
    },

    _onPageNoChange: function (key) {
        this.setState({
            pageNo: key
        });
        // use key instead of this.state.pageNo as setState is async.
        ForumActionCreators.getRecentForumPost(key, this.state.pageSize);
    },

    _onPageSizeChange: function (current, pageSize) {
        //console.log("_onPageSizeChange is called", current, pageSize);
        this.setState({
            pageSize: pageSize
        });
        ForumActionCreators.getRecentForumPost(this.state.pageNo, pageSize);
    },

    render: function() {
        return (
            <div>
                <div className="header">
                    <h2 className="headerContent">Recent Forum Posts</h2>
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

module.exports = ForumRecentPost;
