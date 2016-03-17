var CommentThread = React.createClass({
    propTypes: {
        comments           : React.PropTypes.array,
        handleUpvote       : React.PropTypes.func.isRequired,
        handleRemoveUpvote : React.PropTypes.func.isRequired,
        handleReply        : React.PropTypes.func.isRequired,
        upvoteImageURL     : React.PropTypes.string.isRequired,
        upvotedImageURL    : React.PropTypes.string.isRequired,
        usernameRoute      : React.PropTypes.string.isRequired
    },
    render: function() {
        var props = this.props;

        var CommentProps = {
            comment: {},
            handleUpvote: props.handleUpvote,
            handleRemoveUpvote: props.handleRemoveUpvote,
            handleReply: props.handleReply,
            upvoteImageURL: props.upvoteImageURL,
            upvotedImageURL: props.upvotedImageURL,
            usernameRoute: props.usernameRoute
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

