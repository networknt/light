/**
 * Created by steve on 08/07/15.
 */
var React = require('react');
var Router = require('react-router')
    , RouteHandler = Router.RouteHandler
    , Route = Router.Route;

var ReactPropTypes = React.PropTypes;
var AuthActionCreators = require('../actions/AuthActionCreators.js');
var MenuActionCreators = require('../actions/MenuActionCreators.js');
var MenuStore = require('../stores/MenuStore.js');
var AuthStore = require('../stores/AuthStore.js');

var ReactBootstrap = require('react-bootstrap')
    , Nav = ReactBootstrap.Nav
    , Navbar = ReactBootstrap.Navbar
    , CollapsibleNav = ReactBootstrap.CollapsibleNav
    , NavItem = ReactBootstrap.NavItem
    , DropdownButton = ReactBootstrap.DropdownButton
    , MenuItem = ReactBootstrap.MenuItem
    , ListGroup = ReactBootstrap.ListGroup
    , NavBrand = ReactBootstrap.NavBrand
    , NavDropdown = ReactBootstrap.NavDropdown;

var ReactRouterBootstrap = require('react-router-bootstrap')
    , NavItemLink = ReactRouterBootstrap.NavItemLink
    , MenuItemLink = ReactRouterBootstrap.MenuItemLink
    , ButtonLink = ReactRouterBootstrap.ButtonLink
    , ListGroupItemLink = ReactRouterBootstrap.ListGroupItemLink
    , LinkContainer = ReactRouterBootstrap.LinkContainer;

var CheckoutButton = require('./cart/CheckoutButton.js');


var Header = React.createClass({
    getInitialState: function() {
        return {
            menu: MenuStore.getMenu(),
            errors: []
        };
    },

    componentWillMount: function() {
        MenuActionCreators.loadMenu();
    },

    componentDidMount: function() {
        MenuStore.addChangeListener(this._onChange);
    },

    componentWillUnmount: function() {
        MenuStore.removeChangeListener(this._onChange);
    },

    _onChange: function() {
        this.setState({
            menu: MenuStore.getMenu(),
            errors: MenuStore.getErrors()
        });
    },

    logout: function(e) {
        //console.log('logout is called.');
        e.preventDefault();
        AuthActionCreators.logout();
    },

    hasAccess: function(item) {
        //console.log('AuthStore.getRoles() = ', AuthStore.getRoles());
        //console.log('item.roles', item.roles);
        for (var i = 0; i < AuthStore.getRoles().length; i++) {
            if (item.roles != null) {
                for (var j = 0; j < item.roles.length; j++) {
                    if (AuthStore.getRoles()[i] == item.roles[j]) {
                        return true;
                    };
                };
            };
        };
        return false;
    },

    render: function() {
        //console.log('this.state.menu = ', this.state.menu);
        var outOwn = this.state.menu.out_Own? this.state.menu.out_Own : [];
        //console.log('outOwn = ', outOwn);
        var rightNav = (
            <Nav navbar right>
                {outOwn.map(function(item, index){
                    //console.log('item = ', item);
                    if(!item.left && this.hasAccess(item)) {
                        if(item.menuItemId == 'cart') {
                            return (
                                <NavItem eventKey={item.menuItemId}>
                                    <CheckoutButton/>
                                </NavItem>
                            );
                        } else {
                            return (
                                <LinkContainer to={item.menuItemId}>
                                    <NavItem eventKey={item.menuItemId}>{item.label}</NavItem>
                                </LinkContainer>
                            );
                        }
                    }
                }, this)}
            </Nav>
        );

        var leftNav = (
            <Nav navbar>
                {outOwn.map(function(item, index){
                    //console.log('item = ', item);
                    if(item.left && this.hasAccess(item)) {
                        if(item.out_Own) {
                            //console.log('this one has outOwn', item.out_Own);
                            return (
                                <NavDropdown eventKey={item.menuItemId} title={item.label} >
                                    {
                                        item.out_Own.map(function(subItem, subIndex) {
                                            return (
                                                <LinkContainer to={subItem.menuItemId}>
                                                    <NavItem eventKey={subItem.menuItemId} >{subItem.label}</NavItem>
                                                </LinkContainer>
                                            );
                                        }, this)
                                    }
                                </NavDropdown>
                            );
                        } else {
                            return (
                                <LinkContainer to={item.menuItemId}>
                                    <NavItem eventKey={item.menuItemId} >{item.label}</NavItem>
                                </LinkContainer>
                            );
                        }
                    }
                }, this)}
            </Nav>
        );

        return (
            <div>
                <Navbar toggleNavKey={0}>
                    <NavBrand><a href="/">React-Bootstrap</a></NavBrand>
                    <CollapsibleNav eventKey={0}>
                        {leftNav}
                        {rightNav}
                    </CollapsibleNav>
                </Navbar>
            </div>
        );
    }
});

module.exports = Header;
