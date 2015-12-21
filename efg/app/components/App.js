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

const ThemeManager = require('material-ui/lib/styles/theme-manager');
const LightRawTheme = require('material-ui/lib/styles/raw-themes/light-raw-theme');

//console.log('App is called');

function getStateFromStores() {
    return {
        isLoggedIn: AuthStore.isLoggedIn(),
        muiTheme: ThemeManager.getMuiTheme(LightRawTheme)
    };
}

var App = React.createClass({

    childContextTypes: {
        muiTheme: React.PropTypes.object
    },

    getChildContext() {
        return {
            muiTheme: this.state.muiTheme,
        };
    },

    getInitialState: function() {
        return getStateFromStores();
    },

    componentDidMount: function() {
        AuthStore.addChangeListener(this._onChange);
        AuthActionCreators.init();
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
                <Header/>
                {this.props.children}
                <Footer/>
            </div>
        );
    }

});

module.exports = App;
