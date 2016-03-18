import React from 'react';

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

        var CommentProps = {
            comment: {},
            onUpVote: props.onUpVote,
            onRemoveUpVote: props.onRemoveUpVote,
            onAddComment: props.onAddComment,
            onDownVote: props.onDownVote,
            onRemoveDownVote: props.onRemoveDownVote
        };

        var comments = this.props.comments.map(function(comment) {
            CommentProps.comment = comment;

            return (
                <Comment {...CommentProps} />
            );
        });

        return (
            <ul className="comment-thread">
                {comments}
            </ul>
        );
    }
});

module.exports = CommentThread;
