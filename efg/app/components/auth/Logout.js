/**
 * Created by hus5 on 7/16/2015.
 */
var React = require('react');
var AuthActionCreators = require('../../actions/AuthActionCreators.js');
var MenuActionCreators = require('../../actions/MenuActionCreators.js');

var RouteStore = require('../../stores/RouteStore.js');

var Logout = React.createClass({

    contextTypes: {
        router: React.PropTypes.func
    },

    componentDidMount: function() {
        AuthActionCreators.logout();
    },

    render: function() {
        return (
            <div>You have been logged out</div>
        );
    }
});

module.exports = Logout;
