import React from 'react';
import Comment from './Comment';
import CommentNode from './CommentNode';

var CommentThread = React.createClass({
    propTypes: {
        comments           : React.PropTypes.array,
        onUpVote           : React.PropTypes.func.isRequired,
        onRemoveUpVote     : React.PropTypes.func.isRequired,
        onAddComment       : React.PropTypes.func.isRequired,
        onDownVote         : React.PropTypes.func.isRequired,
        onRemoveDownVote   : React.PropTypes.func.isRequired
    },

    onCommentSelect: function(node) {
        // set the select state for the selected category
        if (this.state.selected && this.state.selected.isMounted()) {
            this.state.selected.setState({selected: false});
        }
        this.setState({selected: node});
        node.setState({selected: true});
        if (this.props.onCommentSelect) {
            this.props.onCommentSelect(node);
        }
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
        var comments = this.props.comments.map(function(comment) {
            commentProps.comment = comment;
            return <CommentNode key={comment.commentId}
                                comment = {comment}
                                onCommentSelect={this.onCommentSelect} />
        }.bind(this));

        return (
            <ul className="comment-tree">
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
