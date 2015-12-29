import React from 'react';
import AppBar from 'material-ui/lib/app-bar';
import LeftNav from 'material-ui/lib/left-nav';
import Menu from 'material-ui/lib/menus/menu';
import MenuItem from 'material-ui/lib/menus/menu-item';
import IconMenu from 'material-ui/lib/menus/icon-menu';
import IconButton from 'material-ui/lib/icon-button';
import Badge from 'material-ui/lib/badge';
import ThemeManager from 'material-ui/lib/styles/theme-manager';
import LightRawTheme from 'material-ui/lib/styles/raw-themes/light-raw-theme';
import Colors from 'material-ui/lib/styles/colors';
import { History } from 'react-router'
import AuthStore from '../stores/AuthStore';

// Define menu items for LeftNav
let menuItems = [
    { route: '/', text: 'Home' },
    { route: 'blog', text: 'Blog' },
    { route: 'news', text: 'News' },
    { route: 'forum', text: 'Forum' },
    { route: 'catalog', text: 'Catalog' },
    { route: 'admin', text: 'Admin' },
    { route: 'user', text: 'User' },
    { route: 'about', text: 'About' },
    { route: 'contact', text: 'Contact' }
];

const Main = React.createClass({

    propTypes: {
        children: React.PropTypes.node,
        history: React.PropTypes.object,
        location: React.PropTypes.object,
    },

    childContextTypes : {
        muiTheme: React.PropTypes.object
    },

    getInitialState() {
        let muiTheme = ThemeManager.getMuiTheme(LightRawTheme);
        return {
            leftNavOpen: false,
            muiTheme: muiTheme,
            isLoggedIn: AuthStore.isLoggedIn
        };
    },

    componentWillMount() {
        let newMuiTheme = this.state.muiTheme;
        newMuiTheme.inkBar.backgroundColor = Colors.yellow200;
        AuthStore.addChangeListener(this._userLoginChange);
        this.setState({
            muiTheme: newMuiTheme
        });
    },

    componentWillUnmount: function() {
        AuthStore.removeChangeListener(this._userLoginChange);
    },

    getChildContext() {
        return {
            muiTheme: this.state.muiTheme
        };
    },

    _userLoginChange: function() {
        //console.log("Main._userLoginChange", AuthStore.isLoggedIn());
        this.setState({
            isLoggedIn: AuthStore.isLoggedIn()
        })
    },

    handleLeftNavToggle() {
        this.setState({leftNavOpen: !this.state.leftNavOpen});
    },

    handleItemTouchTap(event, item) {
        //console.log('event', event);
        //console.log('item', item);
        this.setState({leftNavOpen: false});
        this.props.history.push(item.props.value);
    },

    render() {
        var userButton = (
            <IconButton iconClassName="material-icons" iconStyle={{color: this.state.isLoggedIn? 'black': 'lightgray'}}>person</IconButton>
        );

        var shoppingCartButton = (
            <Badge
                badgeContent={10}
                parmary={true}
                badgeStyle={{top: 32, right: 16}}
                >
                <IconButton iconClassName="material-icons">shopping_cart</IconButton>
            </Badge>
        );

        var loginMenuItems = [];
        if (this.state.isLoggedIn) {
            loginMenuItems.push(<MenuItem key='logout' value='logout' primaryText='Sign out' />)
        } else {
            loginMenuItems.push(<MenuItem key='login' value='login' primaryText='Log in' />);
            loginMenuItems.push(<MenuItem key='signup' value='signup' primaryText='Sign up' />);
        }

        var rightMenu = (
            <div>
                {shoppingCartButton}
                <IconMenu iconButtonElement={userButton}
                          openDirection="bottom-left"
                          onItemTouchTap={this.handleItemTouchTap}>
                    {loginMenuItems}
                </IconMenu>
            </div>
        );

        //console.log('history', this.props.history);
        //console.log('location', this.props.location);
        //console.log('children', this.props.children);
        return (
            <div id="page_container">
                <LeftNav open={this.state.leftNavOpen} docked={false} onRequestChange={leftNavOpen => this.setState({leftNavOpen})}>
                    <Menu onItemTouchTap={this.handleItemTouchTap}>
                    {menuItems.map((item, index) => {
                        return (
                            <MenuItem
                                key={index}
                                primaryText={item.text}
                                value={item.route}
                                />
                        );
                    })}
                    </Menu>
                </LeftNav>
                <header>
                    <AppBar title='Edible Forest Garden' onLeftIconButtonTouchTap={this.handleLeftNavToggle} iconElementRight={rightMenu} zDepth={0}/>
                </header>
                {this.props.children}
            </div>
        );
    }
});

export default Main;