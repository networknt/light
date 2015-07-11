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
var About = require('./components/common/About.js');
var Contact = require('./components/common/Contact.js');

var Admin = React.createClass({
    render: function() {
        return <div>Admin</div>;
    }
});

var RoleAdmin = React.createClass({
    render: function() {
        return <div>RoleAdmin</div>;
    }
});
var UserAdmin = React.createClass({
    render: function() {
        return <div>UserAdmin</div>;
    }
});
var BlogAdmin = React.createClass({
    render: function() {
        return <div>BlogAdmin</div>;
    }
});
var AccessAdmin = React.createClass({
    render: function() {
        return <div>AccessAdmin</div>;
    }
});

module.exports = (
    <Route name="app" path="/" handler={App}>
        <Route name="about" path="/about" handler={About} />
        <Route name="contact" path="/contact" handler={Contact} />
        <Route name="roleAdmin" path="/admin/roleAdmin" handler={RoleAdmin} />
        <Route name="userAdmin" path="/admin/userAdmin" handler={UserAdmin} />
        <Route name="blogAdmin" path="/admin/blogAdmin" handler={BlogAdmin} />
        <Route name="accessAdmin" path="/admin/accessAdmin" handler={AccessAdmin} />
        <Route name="login" path="/login" handler={Login}/>
        <Route name="signup" path="/signup" handler={Signup}/>
    </Route>
);
