/**
 * Created by steve on 23/08/15.
 *
 * Component that displays path of the category for blog/news/forum/catalog on the top of the list.
 *
 * TODO need to make it to select category
 *
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
