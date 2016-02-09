var React = require('react');
var WebAPIUtils = require('../../utils/WebAPIUtils');
var NewsStore = require('../../stores/NewsStore');
import NewsCategoryStore from '../../stores/NewsCategoryStore';
var NewsActionCreators = require('../../actions/NewsActionCreators');
var classNames = require('classnames');
import RaisedButton from 'material-ui/lib/raised-button';
require('rc-pagination/assets/index.css');
import Pagination from 'rc-pagination';
import Locale from 'rc-pagination/lib/locale/en_US';
require('rc-select/assets/index.css');
import Select from 'rc-select';
import CommonUtils from '../../utils/CommonUtils';

// Using the general Summary component for News/Blog/Forum summaries.
// import NewsSummary from './NewsSummary';
import Summary from '../common/Summary.jsx';

var NewsRecentPost = React.createClass({
    displayName: 'NewsRecentPost',

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
        this.context.router.push('/news/' + categoryId + '/' + entityId);
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
                <div className="header">
                    <h2 className="headerContent">Recent News Posts</h2>
                </div>
                <div className="leftColumn">
                    {
                        this.state.posts.map(function(post, index) {
                            var boundClick = this._routeToPost.bind(this, post.parentId, post.entityId);
                            return (
                                <Summary post={post} onClick ={boundClick} key={index}/>
                            );
                        }, this)
                    }
                    <Pagination locale={Locale} selectComponentClass={Select} showSizeChanger={true} pageSizeOptions={['10', '25', '50', '100']} onShowSizeChange={this._onPageSizeChange} onChange={this._onPageNoChange} current={this.state.pageNo} pageSize={this.state.pageSize} total={this.state.total}/>
                </div>
            </div>
        );
    }
});

module.exports = NewsRecentPost;
