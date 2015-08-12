/**
 * Created by steve on 8/12/2015.
 */
var React = require('react');
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
var About = require('../common/About.js');


var CheckoutButton = React.createClass({

    render: function() {
        return (
        <NavItemLink to={About}>
            About
        </NavItemLink>
        )
    }
});

module.exports = CheckoutButton;
