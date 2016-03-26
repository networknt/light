import React from 'react';
import UpVoteButton from './UpVoteButton';
import DownVoteButton from './DownVoteButton';
import SpamButton from './SpamButton';
import ReplyBox from './ReplyBox';

var CommentBottomBanner = React.createClass({
    propTypes: {
        onUpVote:           React.PropTypes.func.isRequired,
        upVoted:            React.PropTypes.bool,
        onDownVote:         React.PropTypes.func.isRequired,
        downVoted:          React.PropTypes.bool,
        onSpam:             React.PropTypes.func.isRequired,
        spamed:             React.PropTypes.bool,
        onAddComment:       React.PropTypes.func.isRequired
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

        return (
            <div className="comment-bottom-banner">
                <UpVoteButton {...UpVoteButtonProps} />{' '}
                <DownVoteButton {...DownVoteButtonProps} />{' '}
                <SpamButton {...SpamButtonProps} />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <ReplyBox onAddComment={this.props.onAddComment} />
            </div>
        );
    }
});

module.exports = CommentBottomBanner;