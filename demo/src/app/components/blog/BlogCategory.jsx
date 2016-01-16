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
        // If this component is loaded, then category tree should be there but in the case of
        // bookmark, category tree might be loaded in Blog.jsx in case it is routed directly.
        BlogActionCreators.getBlogTree();
    },

    render: function() {
        return (<CircularProgress mode="indeterminate"/>);
    }
});

module.exports = BlogCategory;
