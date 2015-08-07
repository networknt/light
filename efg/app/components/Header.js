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
        console.log('logout is called.');
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
                        return <NavItemLink key={item.menuItemId} to={item.menuItemId}>{item.label}</NavItemLink>
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
                            console.log('this one has outOwn', item.out_Own);
                            return <DropdownButton key={item.menuItemId} title={item.label} >
                            {item.out_Own.map(function(subItem, subIndex) {
                                return <NavItemLink key={subItem.menuItemId} to={subItem.menuItemId}>{subItem.label}</NavItemLink>
                            }, this)}
                            </DropdownButton>
                        } else {
                            return <NavItemLink key={item.menuItemId} to={item.menuItemId}>{item.label}</NavItemLink>
                        }
                    }
                }, this)}
            </Nav>
        );

        /*
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
         <Nav navbar>
         <NavItemLink to="about" eventKey={1}>About</NavItemLink>
         <NavItemLink to="contact" eventKey={2}>Contact</NavItemLink>
         <NavItemLink to="blogs" eventKey={3}>Blogs</NavItemLink>
         <DropdownButton eventKey={4} title='Admin' >
         <MenuItemLink to="roleAdmin" eventKey={5} onSelect={()=>{}}>Role Admin</MenuItemLink>
         <MenuItemLink to="userAdmin" eventKey={6} onSelect={()=>{}}>User Admin</MenuItemLink>
         <MenuItemLink to="blogAdmin" eventKey={7}>Blog Admin</MenuItemLink>
         <MenuItemLink to="accessAdmin" eventKey={8}>Access Admin</MenuItemLink>
         </DropdownButton>
         </Nav>

        */
        return (
            <div>
                <Navbar brand={<a href='/'>React-Bootstrap</a>} toggleNavKey={0}>
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
