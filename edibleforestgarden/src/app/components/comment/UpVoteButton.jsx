var React = require('react');

var UpVoteButton = React.createClass({

    propTypes: {
        upVoted            : React.PropTypes.bool.isRequired,
        onUpVote           : React.PropTypes.func.isRequired
    },

    getImage: function() {
        if (this.props.upVoted) {
            return '/images/upvoted.svg';
        } else {
            return '/images/upvote.svg';
        }
    },

    render: function() {
        var imageSrc = this.getImage();

        var wrapperStyle = {
            display: 'inline-block'
        };

        return (
            <div className={"comment-button-wrapper"} style={wrapperStyle}>
                <img
                    src={imageSrc}
                    onClick={this.props.onUpVote}
                    />
            </div>
        );
    }
});

module.exports = UpVoteButton;
