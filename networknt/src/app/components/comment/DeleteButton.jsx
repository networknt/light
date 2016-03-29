var React = require('react');

var DeleteButton = React.createClass({

    propTypes: {
        onDelComment       : React.PropTypes.func.isRequired
    },

    render: function() {
        var onDelComment;

        var wrapperStyle = {
            display: 'inline-block'
        };

        var delButtonClass = "delete-button";
        return (
            <div className={"comment-button-wrapper"} style={wrapperStyle}>
                <a className={delButtonClass} onClick={this.props.onDelComment}>delete</a>
            </div>
        );
    }
});

module.exports = DeleteButton;
