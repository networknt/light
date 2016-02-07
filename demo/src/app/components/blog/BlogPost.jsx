'use strict';

var React = require('react');
var ReactPropTypes = React.PropTypes;

import AppConstants from '../../constants/AppConstants';
import Paper from 'material-ui/lib/paper';
import Markdown from '../Markdown';
import BlogActionCreators from '../../actions/BlogActionCreators';
import BlogStore from '../../stores/BlogStore';
import PostStore from '../../stores/PostStore';
import CommonUtils from '../../utils/CommonUtils';
import RaisedButton from 'material-ui/lib/raised-button';

import Toolbar from 'material-ui/lib/toolbar/toolbar';
import ToolbarGroup from 'material-ui/lib/toolbar/toolbar-group';
import ToolbarSeparator from 'material-ui/lib/toolbar/toolbar-separator';
import ToolbarTitle from 'material-ui/lib/toolbar/toolbar-title';

var BlogPost = React.createClass({
    displayName: 'BlogPost',

    getInitialState: function() {
        return {
            post: {},
            allowUpdate: false
        };
    },

    componentWillMount: function() {
        PostStore.addChangeListener(this._onPostChange);
    },

    componentWillUnmount: function() {
        PostStore.removeChangeListener(this._onPostChange);
    },

    componentDidMount: function() {
        //console.log('BlogPost blogPosts', BlogStore.getPosts());
        //console.log('BlogPost index ', this.props.params.index);
        this.setState({
            post: CommonUtils.findPost(BlogStore.getPosts(), this.props.params.entityId),
            allowUpdate: BlogStore.getAllowUpdate()
        })
    },

    _onPostChange: function() {
        console.log('BlogPost._onPostChange', PostStore.getResult(), PostStore.getErrors());
        // TODO display toaster

    },

    _onUpdatePost: function () {
        console.log("_onUpdatePost is called");
        this.props.history.push('/blog/postUpdate/' + this.props.params.entityId);
    },

    _onDeletePost: function () {
        console.log("_onDeletePost is called");
        BlogActionCreators.delPost(this.state.post.rid);
    },

    _routeToTag: function(tagId) {
        this.props.history.push('/tag/' + encodeURIComponent(tagId));
    },

    render: function() {
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
        return (
            <div>
                <div className="leftColumn">
                    <div className="header">
                        <h2 className="headerContent">{this.state.post.title}</h2>
                        <p className="headerSubContent">Submitted by {this.state.post.createUserId} on {this.state.post.createDate}</p>
                    </div>
                    {updateSection}
                    <Paper className="postPaper">
                        <div className="blogPost">
                            <div>{tags}</div>
                            {original}
                            <Markdown text={this.state.post.content} />
                        </div>
                    </Paper>
                    <hr />
                </div>
            </div>
        )

    }
});

module.exports = BlogPost;
