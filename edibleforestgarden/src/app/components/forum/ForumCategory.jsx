/**
 * This is the component for forum entry point to load category tree for the left nav.
 *
 */
var React = require('react');
var ForumActionCreators = require('../../actions/ForumActionCreators');
import CircularProgress from 'material-ui/lib/circular-progress';

var ForumCategory = React.createClass({
    displayName: 'ForumCategory',

    componentWillMount: function() {
        // If this component is loaded, then category tree should be there but in the case of
        // bookmark, category tree might be loaded in Forum.jsx in case it is routed directly.
        ForumActionCreators.getForumTree();
    },

    render: function() {
        return (<CircularProgress mode="indeterminate"/>);
    }
});

module.exports = ForumCategory;
