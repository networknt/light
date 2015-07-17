/**
 * Created by steve on 09/07/15.
 */
var React = require('react');
var RouteHandler = require('react-router').RouteHandler;
var Header = require('../components/Header.js');
var Footer = require('../components/Footer.js');
var AuthStore = require('../stores/AuthStore.js');
var RouteStore = require('../stores/RouteStore.js');
var AuthActionCreators = require('../actions/AuthActionCreators.js');

function getStateFromStores() {
    return {
        isLoggedIn: AuthStore.isLoggedIn()
    };
}

var App = React.createClass({

    getInitialState: function() {
        return getStateFromStores();
    },

    componentDidMount: function() {
        AuthStore.addChangeListener(this._onChange);
        var accessToken = localStorage.getItem('accessToken');
        if(accessToken) {
            console.log('init is triggered');
            AuthActionCreators.init();
        }
    },

    componentWillUnmount: function() {
        AuthStore.removeChangeListener(this._onChange);
    },

    _onChange: function() {
        this.setState(getStateFromStores());
    },

    render: function() {
        return (
            <div className="app">
                <Header isLoggedIn={this.state.isLoggedIn} />
                <RouteHandler/>
                <Footer/>
            </div>
        );
    }

});

module.exports = App;
