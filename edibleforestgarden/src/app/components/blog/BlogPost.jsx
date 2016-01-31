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
            post: CommonUtils.findPost(BlogStore.getPosts(), this.props.params.postId),
            allowUpdate: BlogStore.getAllowUpdate()
        })
    },

    _onPostChange: function() {
        console.log('BlogPost._onPostChange', PostStore.getResult(), PostStore.getErrors());
        // TODO display toaster

    },

    _onUpdatePost: function () {
        console.log("_onUpdatePost is called");
        this.props.history.push('/blog/postUpdate/' + this.props.params.postId);
    },

    _onDeletePost: function () {
        console.log("_onDeletePost is called");
        BlogActionCreators.delPost(this.state.post.rid);
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

module.exports = BlogPost;
