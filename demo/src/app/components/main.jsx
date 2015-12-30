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
import Dialog from 'material-ui/lib/dialog';
import Colors from 'material-ui/lib/styles/colors';
import RaisedButton from 'material-ui/lib/raised-button';
import { History } from 'react-router'
import AuthStore from '../stores/AuthStore';
import CartStore from '../stores/CartStore';
import CheckoutButton from './cart/CheckoutButton';
import TreeNode from './TreeNode';
import ProductActionCreators from '../actions/ProductActionCreators';

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
        location: React.PropTypes.object
    },

    childContextTypes : {
        muiTheme: React.PropTypes.object
    },

    getInitialState() {
        let muiTheme = ThemeManager.getMuiTheme(LightRawTheme);
        return {
            leftNavOpen: false,
            shoppingCartOpen: false,
            muiTheme: muiTheme,
            isLoggedIn: AuthStore.isLoggedIn,
            cartItemCount: 0,
            category: [
                {
                    "@rid": "#43:0",
                    "host": "example",
                    "description": "Computer Component",
                    "categoryId": "computer",
                    "createDate": "2015-09-25T02:32:54.765",
                    "out_Own": [
                        {
                            "@rid": "#43:1",
                            "host": "example",
                            "description": "Computer Case",
                            "categoryId": "case",
                            "createDate": "2015-09-25T02:33:25.915",
                            "in_Own": [
                                "#43:0"
                            ],
                            "out_Own": [
                                {
                                    "@rid": "#43:3",
                                    "host": "example",
                                    "description": "Desktop Case",
                                    "categoryId": "desktopCase",
                                    "createDate": "2015-09-25T02:34:11.850",
                                    "in_Own": [
                                        "#43:1"
                                    ]
                                },
                                {
                                    "@rid": "#43:4",
                                    "host": "example",
                                    "description": "Server Case",
                                    "categoryId": "serverCase",
                                    "createDate": "2015-09-25T02:34:29.776",
                                    "in_Own": [
                                        "#43:1"
                                    ]
                                }
                            ]
                        },
                        {
                            "@rid": "#43:2",
                            "host": "example",
                            "description": "Hard Drive",
                            "categoryId": "hardDrive",
                            "createDate": "2015-09-25T02:33:49.007",
                            "in_Own": [
                                "#43:0"
                            ]
                        }
                    ]
                }
            ]
        };
    },

    componentWillMount() {
        let newMuiTheme = this.state.muiTheme;
        newMuiTheme.inkBar.backgroundColor = Colors.yellow200;
        AuthStore.addChangeListener(this._userLoginChange);
        CartStore.addChangeListener(this._cartItemChange);
        this.setState({
            muiTheme: newMuiTheme
        });
    },

    componentWillUnmount: function() {
        AuthStore.removeChangeListener(this._userLoginChange);
        CartStore.removeChangeListener(this._cartItemChange);
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

    _cartItemChange: function() {
        this.setState({
            cartItemCount: CartStore.getCartItemsCount()
        })
    },

    handleLeftNavToggle() {
        this.setState({leftNavOpen: !this.state.leftNavOpen});
    },

    onCategorySelect(node) {
        //console.log('onCategorySelect', node.props.category['@rid'] + ' ' + node.props.category.categoryId);
        // based on the current route, select the entities from actions.
        if(this.props.location.pathname === 'catalog') {
            ProductActionCreators.getCatalogProduct(node.props.category['@rid']);
        }
        //ProductActionCreators.selectCatalog(node, this.state.selected, this.props.onCategorySelect);
    },

    handleItemTouchTap(event, item) {
        //console.log('event', event);
        //console.log('item', item);
        this.setState({leftNavOpen: false});
        this.props.history.push(item.props.value);
    },


    render() {
        var menuButton = (
            <IconButton iconClassName="material-icons">more_vert</IconButton>
        );

        var userButton = (
            <IconButton iconClassName="material-icons" iconStyle={{color: this.state.isLoggedIn? 'black': 'lightgray'}}>person</IconButton>
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
                <CheckoutButton history={this.props.history} />
                <IconMenu iconButtonElement={userButton}
                          openDirection="bottom-left"
                          onItemTouchTap={this.handleItemTouchTap}>
                    {loginMenuItems}
                </IconMenu>
                <IconMenu iconButtonElement={menuButton}
                          openDirection="bottom-left"
                          onItemTouchTap={this.handleItemTouchTap}>
                    {menuItems.map((item, index) => {
                        return (
                            <MenuItem
                                key={index}
                                primaryText={item.text}
                                value={item.route}
                                />
                        );
                    })}
                </IconMenu>
            </div>
        );

        //console.log('history', this.props.history);
        //console.log('location', this.props.location);
        //console.log('children', this.props.children);
        return (
            <div id="page_container">
                <LeftNav open={this.state.leftNavOpen} docked={false} onRequestChange={leftNavOpen => this.setState({leftNavOpen})}>
                    <div>
                        <ul className="category-tree">
                            {this.state.category.map(function(item) {
                                return <TreeNode key={item.categoryId}
                                                 category={item}
                                                 onCategorySelect={this.onCategorySelect}/>;
                            }.bind(this))}
                        </ul>
                    </div>
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