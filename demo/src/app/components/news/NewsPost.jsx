'use strict';

var React = require('react');
var ReactPropTypes = React.PropTypes;

import AppConstants from '../../constants/AppConstants';
import Paper from 'material-ui/lib/paper';
import Markdown from '../Markdown';
import NewsActionCreators from '../../actions/NewsActionCreators';
import NewsStore from '../../stores/NewsStore';
import PostStore from '../../stores/PostStore';
import CommonUtils from '../../utils/CommonUtils';
import RaisedButton from 'material-ui/lib/raised-button';

import Toolbar from 'material-ui/lib/toolbar/toolbar';
import ToolbarGroup from 'material-ui/lib/toolbar/toolbar-group';
import ToolbarSeparator from 'material-ui/lib/toolbar/toolbar-separator';
import ToolbarTitle from 'material-ui/lib/toolbar/toolbar-title';

var NewsPost = React.createClass({
    displayName: 'NewsPost',

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
        //console.log('NewsPost blogPosts', NewsStore.getNewsPosts());
        //console.log('NewsPost index ', this.props.params.index);
        this.setState({
            post: CommonUtils.findPost(NewsStore.getPosts(), this.props.params.entityId),
            allowUpdate: NewsStore.getAllowUpdate()
        })
    },

    _onPostChange: function() {
        console.log('NewsPost._onPostChange', PostStore.getResult(), PostStore.getErrors());
        // TODO display toaster

    },

    _onUpdatePost: function () {
        console.log("_onUpdatePost is called");
        this.props.history.push('/news/postUpdate/' + this.props.params.entityId);
    },

    _onDeletePost: function () {
        console.log("_onDeletePost is called");
        NewsActionCreators.delPost(this.state.post.rid);
    },

    render: function() {
        let tags = '';
        if(this.state.post.tags) {
            tags = this.state.post.tags.map((tag, index) => {
                return <span key={index}>{tag}&nbsp;&nbsp;&nbsp;</span>
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
                        <p className="headerSubContent">Submitted by {this.state.post.createUserId} on {this.state.post.createDate}</p>
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
