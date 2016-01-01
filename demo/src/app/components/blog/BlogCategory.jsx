/**
 * This is the component for blog entry point to load category tree for the left nav.
 *
 */
var React = require('react');
var BlogActionCreators = require('../../actions/BlogActionCreators');
import CircularProgress from 'material-ui/lib/circular-progress';

var BlogCategory = React.createClass({
    displayName: 'BlogCategory',

    componentWillMount: function() {
        BlogActionCreators.getBlogTree();
    },

    render: function() {
        return (<CircularProgress mode="indeterminate"/>);
    }
});

module.exports = BlogCategory;
