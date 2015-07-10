/**
 * Created by steve on 09/07/15.
 */
var React = require('react');
var Router = require('react-router');
var Route = Router.Route;
var DefaultRoute = Router.DefaultRoute;

var App = require('./components/App.js');
var Login = require('./components/auth/Login.js');
//var Stories = require('./components/stories/StoriesPage.react.jsx');
//var Story = require('./components/stories/StoryPage.react.jsx');
//var StoryNew = require('./components/stories/StoryNew.react.jsx');
var Signup = require('./components/auth/Signup.js');

module.exports = (
    <Route name="app" path="/" handler={App}>
        <Route name="login" path="/login" handler={Login}/>
        <Route name="signup" path="/signup" handler={Signup}/>
    </Route>
);

