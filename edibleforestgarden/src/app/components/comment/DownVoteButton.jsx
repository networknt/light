var React = require('react');

var DownVoteButton = React.createClass({

    propTypes: {
        downVoted            : React.PropTypes.bool.isRequired,
        onDownVote           : React.PropTypes.func.isRequired
    },

    getImage: function() {
        if (this.props.downVoted) {
            return '/images/downvoted.svg';
        } else {
            return '/images/downvote.svg';
        }
    },

    render: function() {
        var imageSrc = this.getImage();

        var wrapperStyle = {
            display: 'inline-block'
        };

        return (
            <div className={"comment-button-wrapper"} style={wrapperStyle}>
                <img src={imageSrc}
                    onClick={this.props.onDownVote}
                />
            </div>
        );
    }
});

module.exports = DownVoteButton;
