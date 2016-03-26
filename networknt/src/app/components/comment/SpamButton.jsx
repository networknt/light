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

        return (
            <div className={"comment-button-wrapper"} style={wrapperStyle}>
                <a className={"spam-button"} onClick={this.props.onSpam}>spam</a>
            </div>
        );
    }
});

module.exports = SpamButton;
