/**
 * Created by steve on 08/07/15.
 */
var React = require('react');
var Router = require('react-router')
    , RouteHandler = Router.RouteHandler
    , Route = Router.Route;

var ReactPropTypes = React.PropTypes;
var AuthActionCreators = require('../actions/AuthActionCreators.js');

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



var Header = React.createClass({

    propTypes: {
        isLoggedIn: ReactPropTypes.bool,
        email: ReactPropTypes.string
    },

    logout: function(e) {
        console.log('logout is called.');
        e.preventDefault();
        AuthActionCreators.logout();
    },

    render: function() {
        var rightNav = this.props.isLoggedIn ? (
            <Nav navbar right>
                <NavItemLink to="logout" onClick={this.logout}>Logout</NavItemLink>
            </Nav>
        ) : (
            <Nav navbar right>
                <NavItemLink to="login" eventKey={8}>Login</NavItemLink>
                <NavItemLink to="signup" eventKey={9}>Signup</NavItemLink>
            </Nav>
        );

        return (
            <div>
                <Navbar brand={<a href='/'>React-Bootstrap</a>} toggleNavKey={0}>
                    <CollapsibleNav eventKey={0}>
                        <Nav navbar>
                            <NavItemLink to="about" eventKey={1}>About</NavItemLink>
                            <NavItemLink to="contact" eventKey={2}>Contact</NavItemLink>
                            <DropdownButton eventKey={3} title='Admin' >
                                <MenuItemLink to="roleAdmin" eventKey={4} onSelect={()=>{}}>Role Admin</MenuItemLink>
                                <MenuItemLink to="userAdmin" eventKey={5} onSelect={()=>{}}>User Admin</MenuItemLink>
                                <MenuItemLink to="blogAdmin" eventKey={6}>Blog Admin</MenuItemLink>
                                <MenuItemLink to="accessAdmin" eventKey={7}>Access Admin</MenuItemLink>
                            </DropdownButton>
                        </Nav>
                        {rightNav}
                    </CollapsibleNav>
                </Navbar>
            </div>
        );
    }
});

module.exports = Header;
