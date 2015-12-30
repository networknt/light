/**
 * Created by steve on 23/08/15.
 */
var React = require('react');

var Ancestor = React.createClass({
    displayName: 'Ancestor',

    render: function() {
        return (
            <div>
                {this.props.ancestor.categoryId}
            </div>
        );
    }
});

module.exports = Ancestor;
