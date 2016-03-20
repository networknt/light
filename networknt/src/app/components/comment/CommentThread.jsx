import React from 'react';
import Comment from './Comment';

var CommentThread = React.createClass({
    propTypes: {
        comments           : React.PropTypes.array,
        onUpVote           : React.PropTypes.func.isRequired,
        onRemoveUpVote     : React.PropTypes.func.isRequired,
        onAddComment       : React.PropTypes.func.isRequired,
        onDownVote         : React.PropTypes.func.isRequired,
        onRemoveDownVote   : React.PropTypes.func.isRequired
    },

    render: function() {
        var props = this.props;
        console.log('props', this.props);

        var commentProps = {
            comment: {},
            onUpVote: props.onUpVote,
            onRemoveUpVote: props.onRemoveUpVote,
            onAddComment: props.onAddComment,
            onDownVote: props.onDownVote,
            onRemoveDownVote: props.onRemoveDownVote
        };

        console.log('commentProps', commentProps);
        console.log('comments', this.props.comments);
        var comments = this.props.comments.map(function(comment, index) {
            commentProps.comment = comment;
            commentProps.key = index;

            return (
                <Comment {...commentProps} />
            );
        });
        console.log('comments', comments);
        return (
            <ul className="comment-thread">
                {comments}
            </ul>
        );
    }
    /*
    render: function() {
        var props = this.props;
        console.log('props', this.props);

        var commentProps = {
            comment: {},
            onUpVote: props.onUpVote,
            onRemoveUpVote: props.onRemoveUpVote,
            onAddComment: props.onAddComment,
            onDownVote: props.onDownVote,
            onRemoveDownVote: props.onRemoveDownVote
        };

        console.log('commentProps', commentProps);
        var comments = this.props.comments.map(function(comment, index) {
            commentProps.comment = comment;
            commentProps.key = index;

            return (
                <Comment {...commentProps} />
            );
        });

        return (
            <ul className="comment-thread">
                {comments}
            </ul>
        );
    }
    */
});

module.exports = CommentThread;
