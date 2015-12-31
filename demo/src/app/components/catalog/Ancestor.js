/**
 * Created by steve on 23/08/15.
 */
var React = require('react');

var Ancestor = React.createClass({
    displayName: 'Ancestor',

    render: function() {
        return (
            <span>{this.props.ancestor.categoryId + '/'}</span>
        );
    }
});

module.exports = Ancestor;
