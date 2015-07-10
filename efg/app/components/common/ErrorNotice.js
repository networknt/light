/**
 * Created by steve on 08/07/15.
 */
var React = require('react');

var ErrorNotice = React.createClass({
    render: function() {
        return (
            <div className="error-notice">
                <ul>
                    {this.props.errors.map(function(error, index){
                        return <li className="error-notice__error" key={"error-"+index}>{error}</li>;
                    })}
                </ul>
            </div>
        );
    }
});

module.exports = ErrorNotice;

