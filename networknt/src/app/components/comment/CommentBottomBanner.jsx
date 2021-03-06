import React from 'react';
import UpVoteButton from './UpVoteButton';
import DownVoteButton from './DownVoteButton';
import SpamButton from './SpamButton';
import ReplyBox from './ReplyBox';
import DeleteButton from './DeleteButton';
import UpdateBox from './UpdateBox';

var CommentBottomBanner = React.createClass({
    propTypes: {
        onUpVote:           React.PropTypes.func.isRequired,
        upVoted:            React.PropTypes.bool,
        onDownVote:         React.PropTypes.func.isRequired,
        downVoted:          React.PropTypes.bool,
        onSpam:             React.PropTypes.func.isRequired,
        spamed:             React.PropTypes.bool,
        onAddComment:       React.PropTypes.func.isRequired,
        onDelComment:       React.PropTypes.func.isRequired
    },

    render: function() {
        var UpVoteButtonProps = {
            upVoted: this.props.upVoted,
            onUpVote: this.props.onUpVote
        };

        var DownVoteButtonProps = {
            downVoted: this.props.downVoted,
            onDownVote: this.props.onDownVote
        };

        var SpamButtonProps = {
            spamed: this.props.spamed,
            onSpam: this.props.onSpam
        };

        var DelButtonProps = {
            onDelComment: this.props.onDelComment
        };

        var delButton = '';
        if(this.props.allowUpdate === true) {
            delButton = <DeleteButton {...DelButtonProps}/>;
        }

        return (
            <div className="comment-bottom-banner">
                <UpVoteButton {...UpVoteButtonProps} />{' '}
                <DownVoteButton {...DownVoteButtonProps} />{' '}
                <SpamButton {...SpamButtonProps} />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <ReplyBox onAddComment={this.props.onAddComment} />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                {delButton}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <UpdateBox onUpdComment={this.props.onUpdComment} />
            </div>
        );
    }
});

module.exports = CommentBottomBanner;