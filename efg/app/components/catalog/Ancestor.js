/**
 * Created by steve on 23/08/15.
 */
var React = require('react');

var Ancestor = React.createClass({
    render: function() {
        return (
            <div>
                {this.props.ancestor.catalogId}
            </div>
        );
    }
});

module.exports = Ancestor;
