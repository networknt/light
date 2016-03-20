var React = require('react');
var WebAPIUtils = require('../../utils/WebAPIUtils');
var ForumStore = require('../../stores/ForumStore');
import ForumCategoryStore from '../../stores/ForumCategoryStore';
var ForumActionCreators = require('../../actions/ForumActionCreators');
var classNames = require('classnames');
import RaisedButton from 'material-ui/lib/raised-button';
import Toolbar from 'material-ui/lib/toolbar/toolbar';
import ToolbarGroup from 'material-ui/lib/toolbar/toolbar-group';
import ToolbarSeparator from 'material-ui/lib/toolbar/toolbar-separator';
import ToolbarTitle from 'material-ui/lib/toolbar/toolbar-title';
require('rc-pagination/assets/index.css');
import Pagination from 'rc-pagination';
import Locale from 'rc-pagination/lib/locale/en_US';
require('rc-select/assets/index.css');
import Select from 'rc-select';
import CommonUtils from '../../utils/CommonUtils';
import ForumSummary from './ForumSummary';
import List from 'material-ui/lib/lists/list';
import ListItem from 'material-ui/lib/lists/list-item';
import DropDownMenu from 'material-ui/lib/DropDownMenu';
import MenuItem from 'material-ui/lib/menus/menu-item';
import Menu from 'material-ui/lib/menus/menu';
import ArrowDropRight from 'material-ui/lib/svg-icons/navigation-arrow-drop-right';

var Forum = React.createClass({
    displayName: 'Forum',

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: function() {
        let rid = null;
        if(ForumCategoryStore.getCategory().length  !== 0) {
            rid = ForumCategoryStore.getCategory()[0]['@rid'];
            if(this.props.params.categoryId) {
                let category = CommonUtils.findCategory(ForumCategoryStore.getCategory(), this.props.params.categoryId);
                rid = category['@rid'];
            }
        }
        return {
            posts: [],
            ancestors: [],
            allowUpdate: false,
            total: 0,
            pageSize: 10,
            pageNo: 1,
            rid: rid
        };
    },

    componentWillMount: function() {
        ForumStore.addChangeListener(this._onForumChange);
        ForumCategoryStore.addChangeListener(this._forumCategoryChange);

        // need to make sure that category tree is loaded in case of bookmark.
        if(ForumCategoryStore.getCategory().length === 0) {
            ForumActionCreators.getForumTree();
        } else {
            // lookup categoryRid from categoryId in params.
            let category = CommonUtils.findCategory(ForumCategoryStore.getCategory(), this.props.params.categoryId);
            ForumActionCreators.getForumPost(category['@rid'], this.state.pageNo, this.state.pageSize);
        }
    },

    componentWillUnmount: function() {
        ForumStore.removeChangeListener(this._onForumChange);
        ForumCategoryStore.removeChangeListener(this._forumCategoryChange);
    },

    _onForumChange: function() {
        this.setState({
            ancestors: ForumStore.getAncestors(),
            allowUpdate: ForumStore.getAllowUpdate(),
            posts: ForumStore.getPosts(),
            total: ForumStore.getTotal()
        });
    },

    _forumCategoryChange: function() {
        // The Main doesn't care about the post loading anymore. the loading action always starts here.
        let rid = ForumCategoryStore.getCategory()[0]['@rid'];
        if(this.props.params.categoryId) {
            let category = CommonUtils.findCategory(ForumCategoryStore.getCategory(), this.props.params.categoryId);
            rid = category['@rid'];
        }
        this.setState({rid: rid});
        ForumActionCreators.getForumPost(rid, this.state.pageNo, this.state.pageSize);
    },

    _routeToPost: function(entityId) {
        this.context.router.push('/forum/' + this.props.params.categoryId + '/' + entityId);
    },

    _onAddPost: function () {
        //console.log("_onAddPost is called");
        this.context.router.push('/forum/postAdd/' + this.props.params.categoryId);
    },

    _onPageNoChange: function (key) {
        //console.log("_onPageNoChange is called", key);
        this.setState({
            pageNo: key
        });
        // use key instead of this.state.pageNo as setState is async.
        ForumActionCreators.getForumPost(this.state.rid, key, this.state.pageSize);
    },

    _onPageSizeChange: function (current, pageSize) {
        //console.log("_onPageSizeChange is called", current, pageSize);
        this.setState({
            pageSize: pageSize
        });
        ForumActionCreators.getForumPost(this.state.rid, this.state.pageNo, pageSize);
    },

    handledropdownChange : function (event, index, value) {
        this.setState({dropdownvalue: value})
    },

    render: function() {
        console.log('Forum.render', this.state.total, this.state.pageNo, this.state.pageSize);
        let addButton = this.state.allowUpdate ? <RaisedButton label="Add Post" primary={true} onTouchTap={this._onAddPost} /> : '';

        return (
            <div>
                <div className="leftColumn">
                    <div className="header">
                        <h2 className="headerContent">Forum</h2>
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
                                <ForumSummary post={post} onClick ={boundClick} key={index}/>
                            );
                        }, this)
                    }
                    <Pagination locale={Locale} selectComponentClass={Select} showSizeChanger={true} pageSizeOptions={['10', '25', '50', '100']} onShowSizeChange={this._onPageSizeChange} onChange={this._onPageNoChange} current={this.state.pageNo} pageSize={this.state.pageSize} total={this.state.total}/>
                </div>
            </div>
        );
    }
});

module.exports = Forum;
