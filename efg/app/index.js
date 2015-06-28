/*
 * Copyright 2015 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

require('bootstrap/less/bootstrap.less');

var React = require('react');

var Router = require('react-router')
    , RouteHandler = Router.RouteHandler
    , Route = Router.Route;

var ReactBootstrap = require('react-bootstrap')
    , Nav = ReactBootstrap.Nav
    , Navbar = ReactBootstrap.Navbar
    , CollapsibleNav = ReactBootstrap.CollapsibleNav
    , NavItem = ReactBootstrap.NavItem
    , DropdownButton = ReactBootstrap.DropdownButton
    , MenuItem = ReactBootstrap.MenuItem
    , ListGroup = ReactBootstrap.ListGroup;

var ReactRouterBootstrap = require('react-router-bootstrap')
    , NavItemLink = ReactRouterBootstrap.NavItemLink
    , MenuItemLink = ReactRouterBootstrap.MenuItemLink
    , ButtonLink = ReactRouterBootstrap.ButtonLink
    , ListGroupItemLink = ReactRouterBootstrap.ListGroupItemLink;

var App = React.createClass({
    render: function() {
        return (
            <div>
                <Navbar brand={<a href='/'>React-Bootstrap</a>} toggleNavKey={0}>
                    <CollapsibleNav eventKey={0}> {/* This is the eventKey referenced */}
                        <Nav navbar>
                            <NavItemLink to="about">About</NavItemLink>
                            <NavItemLink to="contact">Contact</NavItemLink>
                            <DropdownButton eventKey={3} title='Admin'>
                                <MenuItemLink to="roleAdmin">Role Admin</MenuItemLink>
                                <MenuItemLink to="userAdmin">User Admin</MenuItemLink>
                                <MenuItemLink to="blogAdmin">Blog Admin</MenuItemLink>
                                <MenuItemLink to="accessAdmin">Access Admin</MenuItemLink>
                            </DropdownButton>
                        </Nav>
                        <Nav navbar right>
                            <NavItemLink to="signin">Sign In</NavItemLink>
                            <NavItemLink to="signup">Sign Up</NavItemLink>
                        </Nav>
                    </CollapsibleNav>
                </Navbar>
                <RouteHandler/>
                <div>This is just a footer</div>
            </div>
        );
    }
});

var About = React.createClass({
    render: function() {
        return <div>About</div>;
    }
});

var Contact = React.createClass({
    render: function() {
        return <div>Contact</div>;
    }
});

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

var Signin = React.createClass({
    render: function() {
        return <div>Signin</div>;
    }
});
var Signup = React.createClass({
    render: function() {
        return <div>Signup</div>;
    }
});


var routes = (
    <Route handler={App} path="/">
        <Route name="about" path="/about" handler={About} />
        <Route name="contact" path="/contact" handler={Contact} />
        <Route name="roleAdmin" path="/admin/roleAdmin" handler={RoleAdmin} />
        <Route name="userAdmin" path="/admin/userAdmin" handler={UserAdmin} />
        <Route name="blogAdmin" path="/admin/blogAdmin" handler={BlogAdmin} />
        <Route name="accessAdmin" path="/admin/accessAdmin" handler={AccessAdmin} />
        <Route name="signin" path="/signin" handler={Signin} />
        <Route name="signup" path="/signup" handler={Signup} />

    </Route>
);

Router.run(routes, function (Handler) {
    React.render(<Handler/>, document.body);
});
