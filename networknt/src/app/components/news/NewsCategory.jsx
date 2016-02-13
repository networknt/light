/**
 * This is the component for news entry point to load category tree for the left nav.
 *
 */
var React = require('react');
var NewsActionCreators = require('../../actions/NewsActionCreators');
import CircularProgress from 'material-ui/lib/circular-progress';

var NewsCategory = React.createClass({
    displayName: 'NewsCategory',

    componentWillMount: function() {
        NewsActionCreators.getNewsTree();
    },

    render: function() {
        return (<CircularProgress mode="indeterminate"/>);
    }
});

module.exports = NewsCategory;
