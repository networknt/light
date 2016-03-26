import React from 'react';
import Comment from './Comment';
import CommentNode from './CommentNode';

var CommentThread = React.createClass({
    propTypes: {
        comments           : React.PropTypes.array,
        onAddComment       : React.PropTypes.func.isRequired,
        onUpVote           : React.PropTypes.func.isRequired,
        onDownVote         : React.PropTypes.func.isRequired,
        onSpam             : React.PropTypes.func.isRequired
    },

    render: function() {
        var props = this.props;

        var commentProps = {
            comment: {},
            onAddComment: props.onAddComment,
            onUpVote: props.onUpVote,
            onDownVote: props.onDownVote,
            onSpam: props.onSpam
        };
        var comments = this.props.comments.map(function(comment) {
            commentProps.comment = comment;
            commentProps.key = comment.commentId;
            return <CommentNode {...commentProps} />
        }.bind(this));

        return (
            <ul className="comment-tree">
                {comments}
            </ul>
        );
    }
});

module.exports = CommentThread;
