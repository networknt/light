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
            original = <div><a href={this.state.post.originalUrl} target="_blank">Submitted by {this.state.post.originalAuthor} via {this.state.post.originalSite}</a></div>
        }
        let updateButton = this.state.allowUpdate? <RaisedButton label="Update Post" primary={true} onTouchTap={this._onUpdatePost} /> : '';
        let deleteButton = this.state.allowUpdate? <RaisedButton label="Delete Post" primary={true} onTouchTap={this._onDeletePost} /> : '';
        return (
            <span>
                {updateButton}
                {deleteButton}
                <Paper className="blogPostPaper">
                    <div className="blogPost">
                        <h2 className="title">{this.state.post.title}</h2>
                        <span>Submitted by {this.state.post.createUserId} on {this.state.post.createDate}</span>
                        <div>{tags}</div>
                        {original}
                        <Markdown text={this.state.post.content} />
                    </div>
                </Paper>
                <hr />
            </span>
        )

    }
});

module.exports = NewsPost;
