'use strict';

var React = require('react');
var ReactPropTypes = React.PropTypes;

import AppConstants from '../../constants/AppConstants';
import Paper from 'material-ui/lib/paper';
import Markdown from '../Markdown';
import NewsActionCreators from '../../actions/NewsActionCreators';
import PostActionCreators from '../../actions/PostActionCreators';
import NewsStore from '../../stores/NewsStore';
import PostStore from '../../stores/PostStore';
import EntityStore from '../../stores/EntityStore';
import CommonUtils from '../../utils/CommonUtils';
import RaisedButton from 'material-ui/lib/raised-button';
import moment from 'moment';

import Toolbar from 'material-ui/lib/toolbar/toolbar';
import ToolbarGroup from 'material-ui/lib/toolbar/toolbar-group';
import ToolbarSeparator from 'material-ui/lib/toolbar/toolbar-separator';
import ToolbarTitle from 'material-ui/lib/toolbar/toolbar-title';

var NewsPost = React.createClass({
    displayName: 'NewsPost',

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: function() {
        return {
            post: {},
            allowUpdate: false
        };
    },

    componentWillMount: function() {
        PostStore.addChangeListener(this._onPostChange);
        EntityStore.addChangeListener(this._onEntityChange);
    },

    componentWillUnmount: function() {
        PostStore.removeChangeListener(this._onPostChange);
        EntityStore.removeChangeListener(this._onEntityChange);
    },

    componentDidMount: function() {
        let post = CommonUtils.findPost(NewsStore.getPosts(), this.props.params.entityId);
        // get post from news store as part of the list. If not there, get individual post.
        if(!post) {
            PostActionCreators.getPost(this.props.params.entityId);
        }
        this.setState({
            post: post? post : {},
            allowUpdate: NewsStore.getAllowUpdate()
        })
    },

    _onPostChange: function() {
        console.log('NewsPost._onPostChange', PostStore.getResult(), PostStore.getErrors());
        // TODO display toaster

    },

    _onEntityChange: function() {
        this.setState({
            post: EntityStore.getEntity()
        })
    },

    _onUpdatePost: function () {
        console.log("_onUpdatePost is called");
        this.context.router.push('/news/postUpdate/' + this.props.params.entityId);
    },

    _onDeletePost: function () {
        console.log("_onDeletePost is called");
        NewsActionCreators.delPost(this.state.post.rid);
    },

    _routeToTag: function(tagId) {
        this.context.router.push('/tag/' + encodeURIComponent(tagId));
    },

    render: function() {
        let time = moment(this.state.post.createDate).format("DD-MM-YYYY HH:mm:ss");
        let tags = '';
        if(this.state.post.tags) {
            tags = this.state.post.tags.map((tag, index) => {
                let boundTagClick = this._routeToTag.bind(this, tag);
                return <span key={index}><a href='#' onClick={boundTagClick}>{tag}</a>&nbsp;&nbsp;&nbsp;</span>
            });
        }
        let original = '';
        if(this.state.post.originalAuthor && this.state.post.originalSite && this.state.post.originalUrl) {
            original = <p className="headerAdditionalContent">Original Author: {this.state.post.originalAuthor} Source: <a href={this.state.post.originalUrl}>{this.state.post.originalSite}</a></p>;
        }
        let updateSection = this.state.allowUpdate ?
            <Toolbar>
                <ToolbarGroup float="left">
                    <ToolbarTitle text={this.state.post.title} />
                </ToolbarGroup>
                <ToolbarGroup float="right">
                    <ToolbarSeparator />
                    <RaisedButton label="Update Post" primary={true} onTouchTap={this._onUpdatePost} />
                    <RaisedButton label="Delete Post" primary={true} onTouchTap={this._onDeletePost} />
                </ToolbarGroup>
            </Toolbar>
            : '';
        return (
            <div>
                <div className="leftColumn">
                    <div className="header">
                        <h2 className="headerContent">{this.state.post.title}</h2>
                        <p className="headerSubContent">Submitted by {this.state.post.createUserId} on {time}</p>
                        {original}
                    </div>
                    {updateSection}
                    <Paper className="postPaper">
                        <div className="blogPost">
                            <div>Tags: {tags}</div>
                            <Markdown text={this.state.post.content} />
                        </div>
                    </Paper>
                    <hr />
                </div>
            </div>
        )

    }
});

module.exports = NewsPost;
