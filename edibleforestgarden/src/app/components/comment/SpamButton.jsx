var React = require('react');

var SpamButton = React.createClass({

    propTypes: {
        spamed             : React.PropTypes.bool.isRequired,
        onSpam             : React.PropTypes.func.isRequired
    },

    render: function() {
        var onSpam;

        var wrapperStyle = {
            display: 'inline-block'
        };

        var spamButtonClass = "spam-button";
        if(this.props.spamed === true) {
            spamButtonClass = "spam-button-spamed";
        }
        return (
            <div className={"comment-button-wrapper"} style={wrapperStyle}>
                <a className={spamButtonClass} onClick={this.props.onSpam}>spam</a>
            </div>
        );
    }
});

module.exports = SpamButton;
