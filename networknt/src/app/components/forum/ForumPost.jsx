'use strict';

var React = require('react');
var ReactPropTypes = React.PropTypes;

import AppConstants from '../../constants/AppConstants';
import Paper from 'material-ui/lib/paper';
import Markdown from '../Markdown';
import ForumActionCreators from '../../actions/ForumActionCreators';
import PostActionCreators from '../../actions/PostActionCreators';
import ForumStore from '../../stores/ForumStore';
import PostStore from '../../stores/PostStore';
import EntityStore from '../../stores/EntityStore';
import CommonUtils from '../../utils/CommonUtils';
import RaisedButton from 'material-ui/lib/raised-button';
import CommentBox from '../comment/CommentBox';
import moment from 'moment';

import Toolbar from 'material-ui/lib/toolbar/toolbar';
import ToolbarGroup from 'material-ui/lib/toolbar/toolbar-group';
import ToolbarSeparator from 'material-ui/lib/toolbar/toolbar-separator';
import ToolbarTitle from 'material-ui/lib/toolbar/toolbar-title';

var ForumPost = React.createClass({
    displayName: 'ForumPost',

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
        let post = CommonUtils.findPost(ForumStore.getPosts(), this.props.params.entityId);
        // get post from forum store as part of the list. If not there, get individual post.
        if(!post) {
            PostActionCreators.getPost(this.props.params.entityId);
        }
        console.log('componentDidMount', post);
        this.setState({
            post: post? post : {},
            allowUpdate: ForumStore.getAllowUpdate()
        })
    },

    _onPostChange: function() {
        console.log('ForumPost._onPostChange', PostStore.getResult(), PostStore.getErrors());
        // TODO display toaster


    },

    _onEntityChange: function() {
        console.log('_onEntityChange', EntityStore.getEntity());
        this.setState({
            post: EntityStore.getEntity()
        })
    },

    _onUpdatePost: function () {
        console.log("_onUpdatePost is called");
        this.context.router.push('/forum/postUpdate/' + this.props.params.entityId);
    },

    _onDeletePost: function () {
        console.log("_onDeletePost is called");
        ForumActionCreators.delPost(this.state.post.rid);
    },

    _routeToTag: function(tagId) {
        this.context.router.push('/tag/' + encodeURIComponent(tagId));
    },

    render: function() {
        console.log('ForumPost.state.post', this.state.post);
        let time = moment(this.state.post.createDate).format("DD-MM-YYYY HH:mm:ss");
        let tags = '';
        if(this.state.post && this.state.post.tags) {
            tags = this.state.post.tags.map((tag, index) => {
                let boundTagClick = this._routeToTag.bind(this, tag);
                return <span key={index}><a href='#' onClick={boundTagClick}>{tag}</a>&nbsp;&nbsp;&nbsp;</span>
            });
        }
        let original = '';
        if(this.state.post && this.state.post.originalAuthor && this.state.post.originalSite && this.state.post.originalUrl) {
            original = <div><a href={this.state.post.originalUrl} target="_blank">Submitted by {this.state.post.originalAuthor} via {this.state.post.originalSite}</a></div>
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
        let commentBox = '';
        if(this.state.post && this.state.post.rid) {
            commentBox = (<CommentBox parentRid = {this.state.post.rid}/>)
        }
        return (
            <div>
                <div className="leftColumn">
                    <div className="header">
                        <h2 className="headerContent">{this.state.post.title}</h2>
                        <p className="headerSubContent">Submitted by {this.state.post.createUserId} on {time}</p>
                    </div>
                    {updateSection}
                    <Paper className="postPaper">
                        <div className="blogPost">
                            <div>{tags}</div>
                            {original}
                            <Markdown text={this.state.post.content} />
                        </div>
                    </Paper>
                    {commentBox}
                </div>
            </div>
        )

    }
});

module.exports = ForumPost;
