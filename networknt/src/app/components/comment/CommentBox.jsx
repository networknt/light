import React from 'react';
import CommonUtils from '../../utils/CommonUtils';
import CommentActionCreators from '../../actions/CommentActionCreators';
import CommentStore from '../../stores/CommentStore';
import CommentForm from './CommentForm';
import CommentThread from './CommentThread';

import CircularProgress from 'material-ui/lib/circular-progress';

var CommentBox = React.createClass({

    propTypes: {
        parentRid: React.PropTypes.string
    },

    getInitialState: function() {
        return {
            comments: []
        };
    },

    componentDidMount: function() {
        console.log("CommentBox.componentDidMount", this.props.parentRid);
        CommentStore.addChangeListener(this._onCommentChange);
        CommentActionCreators.getCommentTree(this.props.parentRid);
    },

    componentWillUnmount: function() {
        CommentStore.removeChangeListener(this._onCommentChange);
    },

    _onCommentChange: function() {
        console.log('onCommentChange', CommentStore.getComments());
        this.setState({
            comments: CommentStore.getComments()
        });
    },

    _onAddComment: function(parentRid, message) {
        let data = {
            '@rid': parentRid,
            comment: message
        };
        CommentActionCreators.addComment(data);
    },

    /*
    updateCommentsAfterUpvote: function(commentId, isUpvoting) {
        var updatedComments = helpers.findAndUpdateUpvoted(this.state.comments, commentId, isUpvoting);
        this.setState({comments: updatedComments});
    },
    updateAfterUpvote: function(commentId, isUpvoting) {
        this.updateCommentsAfterUpvote(commentId, isUpvoting);

        var ajaxURL = '/comments/'
        if (isUpvoting) {
            ajaxURL += 'upvote/' + commentId;
        } else {
            ajaxURL += 'remove-upvote/' + commentId;
        }

        $.ajax({
            type: 'POST',
            url: ajaxURL
        });
    },
    handleUpvote: function(commentId) {
        this.updateAfterUpvote(commentId, true);
    },
    handleRemoveUpvote: function(commentId) {
        this.updateAfterUpvote(commentId, false);
    },
    updatePost: function(postId, isUpvoting) {
        var newPost = this.state.post;
        var ajaxURL = '/posts/'

        if (isUpvoting) {
            newPost.score++;
            newPost.upvoted = true;
            ajaxURL += 'upvote/' + postId;
        } else {
            newPost.score--;
            newPost.upvoted = false;
            ajaxURL += 'remove-upvote/' + postId;
        }

        this.setState({post: newPost});
        $.ajax({
            type: 'POST',
            url: ajaxURL
        });
    },
    handlePostUpvote: function(postId) {
        this.updatePost(postId, true);
    },
    handlePostRemoveUpvote: function(postId) {
        this.updatePost(postId, false);
    },
    handleReply: function(parentId, message) {
        $.ajax({
            url: '/comments/reply/' + postId,
            dataType: 'text',
            type: 'POST',
            data: {
                message: message,
                parent_id: parentId
            },
            success: function(data) {
                this.loadCommentsFromServer();
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },

    loadPostFromServer: function() {
        $.ajax({
            url: this.props.GETPostURL,
            dataType: 'json',
            success: function(data) {
                this.setState({post: data});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },
    */

    _onUpVote: function(rid) {

    },

    _onRemoveUpVote: function(rid) {

    },

    _onDownVote: function(rid) {

    },

    _onRemoveDownVote: function(rid) {

    },

    render: function() {
        console.log('this.state', this.state);
        //let commentThread = (<CircularProgress mode="indeterminate"/>);
        var CommentThreadProps = {
            comments: this.state.comments,
            onAddComment: this._onAddComment,
            onUpVote: this._onUpVote,
            onRemoveUpVote: this._onRemoveUpVote,
            onDownVote: this._onDownVote,
            onRemoveDownVote: this_onRemoveDownVote
        };

        let commentThread = (<div></div>);
        if(this.state.comments && this.state.comments.length > 0) {
            commentThread = (
                <div className="comment-thread-app">
                    <CommentThread {...CommentThreadProps} />
                </div>
            )
        }

        var CommentThreadProps = {
            //handleReply: this.handleReply,
            comments: this.state.comments
            //usernameRoute: this.props.usernameRoute,
            //upvoteImageURL: this.props.upvoteImageURL,
            //upvotedImageURL: this.props.upvotedImageURL,
            //handleUpvote: this.handleUpvote,
            //handleRemoveUpvote: this.handleRemoveUpvote
        };

        return (
            <div>
                <h3>{this.state.comments.length} comments</h3>
                <CommentForm onCommentSubmit={this._onAddComment} parentRid={this.props.parentRid} />
                {commentThread}
            </div>
        );
    }
});

module.exports = CommentBox;
