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
import NewsSummary from './NewsSummary';
import Summary from '../common/Summary.jsx';

import Toolbar from 'material-ui/lib/toolbar/toolbar';
import ToolbarGroup from 'material-ui/lib/toolbar/toolbar-group';
import ToolbarSeparator from 'material-ui/lib/toolbar/toolbar-separator';
import ToolbarTitle from 'material-ui/lib/toolbar/toolbar-title';

var News = React.createClass({
    displayName: 'News',

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: function() {
        return {
            posts: [],
            ancestors: [],
            allowUpdate: false,
            total: 0,
            pageSize: 10,
            pageNo: 1
        };
    },

    componentWillMount: function() {
        NewsStore.addChangeListener(this._onNewsChange);

        NewsCategoryStore.addChangeListener(this._newsCategoryChange);

        // need to make sure that category tree is loaded in case of bookmark.
        if(NewsCategoryStore.getCategory().length === 0) {
            NewsActionCreators.getNewsTree();
        } else {
            // lookup categoryRid from categoryId in params.
            let category = CommonUtils.findCategory(NewsCategoryStore.getCategory(), this.props.params.categoryId);
            NewsActionCreators.getNewsPost(category['@rid'], this.state.pageNo, this.state.pageSize);
        }
    },

    componentWillUnmount: function() {
        NewsStore.removeChangeListener(this._onNewsChange);
        NewsCategoryStore.removeChangeListener(this._newsCategoryChange);
    },

    _onNewsChange: function() {
        this.setState({
            ancestors: NewsStore.getAncestors(),
            allowUpdate: NewsStore.getAllowUpdate(),
            posts: NewsStore.getPosts(),
            total: NewsStore.getTotal()
        });
    },

    _newsCategoryChange: function() {
        // The Main doesn't care about the post loading anymore. the loading action always starts here.
        let rid = NewsCategoryStore.getCategory()[0]['@rid'];
        if(this.props.params.categoryId) {
            let category = CommonUtils.findCategory(NewsCategoryStore.getCategory(), this.props.params.categoryId);
            rid = category['@rid'];
        }
        this.setState({rid: rid});
        NewsActionCreators.getNewsPost(rid, this.state.pageNo, this.state.pageSize);
    },

    _routeToPost: function(entityId) {
        this.context.router.push('/news/' + this.props.params.categoryId + '/' + entityId);
    },

    _onAddPost: function () {
        //console.log("_onAddPost is called");
        this.context.router.push('/news/postAdd/' + this.props.params.categoryId);
    },

    _onPageNoChange: function (key) {
        //console.log("_onPageNoChange is called", key);
        this.setState({
            pageNo: key
        });
        // use key instead of this.state.pageNo as setState is async.
        NewsActionCreators.getNewsPost(this.state.rid, key, this.state.pageSize);
    },

    _onPageSizeChange: function (current, pageSize) {
        //console.log("_onPageSizeChange is called", current, pageSize);
        this.setState({
            pageSize: pageSize
        });
        NewsActionCreators.getNewsPost(this.state.rid, this.state.pageNo, pageSize);
    },

    render: function() {
        //console.log('total', this.state.total);
        let addButton = this.state.allowUpdate? <RaisedButton label="Add Post" primary={true} onTouchTap={this._onAddPost} /> : '';
        return (
            <div>
                <div className="leftColumn">
                    <div className="header">
                        <h2 className="headerContent">News</h2>
                    </div>
                    <Toolbar>
                        <ToolbarGroup float="left">
                            <ToolbarTitle text={this.props.params.categoryId} />
                        </ToolbarGroup>
                        <ToolbarGroup float="right">
                            <ToolbarSeparator />
                            {addButton}
                        </ToolbarGroup>
                    </Toolbar>
                    {
                        this.state.posts.map(function(post, index) {
                            var boundClick = this._routeToPost.bind(this, post.entityId);
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

module.exports = News;
