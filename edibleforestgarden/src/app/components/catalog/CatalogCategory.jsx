/**
 * This is the component for catalog entry point to load category tree for the left nav.
 *
 */
var React = require('react');
var CatalogActionCreators = require('../../actions/CatalogActionCreators');
import CircularProgress from 'material-ui/lib/circular-progress';

var CatalogCategory = React.createClass({
    displayName: 'CatalogCategory',

    componentWillMount: function() {
        CatalogActionCreators.getCatalogTree();
    },

    render: function() {
        return (<CircularProgress mode="indeterminate"/>);
    }
});

module.exports = CatalogCategory;
