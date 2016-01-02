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
import CategoryStore from '../stores/CategoryStore';
import CheckoutButton from './cart/CheckoutButton';
import TreeNode from './TreeNode';
import ProductActionCreators from '../actions/ProductActionCreators';
import BlogActionCreators from '../actions/BlogActionCreators';
import AuthActionCreators from '../actions/AuthActionCreators';
import CircularProgress from 'material-ui/lib/circular-progress';

// Define menu items for LeftNav
let menuItems = [
    { route: '/', text: 'Home' },
    { route: '/blog', text: 'Blog' },
    { route: '/news', text: 'News' },
    { route: '/forum', text: 'Forum' },
    { route: '/catalog', text: 'Catalog' },
    { route: '/admin', text: 'Admin' },
    { route: '/user', text: 'User' },
    { route: '/about', text: 'About' },
    { route: '/contact', text: 'Contact' }
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
            category: []
        };
    },

    componentWillMount() {
        let newMuiTheme = this.state.muiTheme;
        newMuiTheme.inkBar.backgroundColor = Colors.yellow200;
        AuthStore.addChangeListener(this._userLoginChange);
        CartStore.addChangeListener(this._cartItemChange);
        CategoryStore.addChangeListener(this._categoryChange);
        AuthActionCreators.init();
        this.setState({
            muiTheme: newMuiTheme
        });
    },

    componentWillUnmount: function() {
        AuthStore.removeChangeListener(this._userLoginChange);
        CartStore.removeChangeListener(this._cartItemChange);
        CategoryStore.removeChangeListener(this._categoryChange);
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

    _onTitleTouchTap: function() {
        this.props.history.push('/');
    },

    _categoryChange: function() {
        this.setState({
            category: CategoryStore.getCategory()
        });
        // create a fake node to trigger the first node loading for the category
        let node = {};
        let props = {};
        props.category = this.state.category[0];
        node.props = props;
        node.fake = true;
        this.onCategorySelect(node);
    },

    handleLeftNavToggle() {
        this.setState({leftNavOpen: !this.state.leftNavOpen});
    },

    onCategorySelect(node) {
        //console.log('onCategorySelect', node);
        // set the select state for the selected category
        if(node.fake) {

        } else {
            if (this.state.selected && this.state.selected.isMounted()) {
                this.state.selected.setState({selected: false});
            }
            this.setState({selected: node});
            node.setState({selected: true});
            if (this.props.onCategorySelect) {
                this.props.onCategorySelect(node);
            }
        }
        // based on the current route, select the entities from actions.
        //console.log('onCategorySelect mainPath', this.getFirstPath(this.props.location.pathname));
        switch(this.getFirstPath(this.props.location.pathname)) {
            case 'catalog':
                ProductActionCreators.getCatalogProduct(node.props.category['@rid']);
                break;
            case 'blog':
                // route to Blog with a specific categoryId in the path
                let rid = node.props.category['@rid'].substring(1);
                this.props.history.push('/blog/' + rid);
                //console.log('pushed to ', 'blog/' + rid);
                // if the current location is blog/:blogRid and has different blogRid then the component won't
                // be mount again and there is no way for the component to reload the blogPost. Work around here.
                let secondPath = this.getSecondPath(this.props.location.pathname);
                //console.log('before workaround', this.props.location.pathname, secondPath, rid);
                if(secondPath != null && secondPath != rid) {
                    //console.log('The main window has the same route, force to reload blogPost...');
                    BlogActionCreators.getBlogPost(node.props.category['@rid']);
                }
                break;
            case 'forum':
                break;
            case 'news':
                break;
        }
    },

    handleItemTouchTap(event, item) {
        // clear category as context has switched. waiting for the new category to be loaded.
        this.setState({leftNavOpen: false, category: []});
        this.props.history.push(item.props.value);
    },

    getFirstPath(path) {
        let url = path.split('/');
        //console.log('url', url);
        return url[1];
    },

    getSecondPath(path) {
        let url = path.split('/');
        if(url.length > 2) {
            return url[2];
        } else {
            return null;
        }
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

        var leftNavContent;
        //console.log('Main.render pahtnaeme', this.getFirstPath(this.props.location.pathname));
        switch(this.getFirstPath(this.props.location.pathname)) {
            case 'blog':
            case 'news':
            case 'forum':
            case 'catalog':
                leftNavContent = this.state.category.length > 0 ? (
                    <div>
                        <ul className="category-tree">
                            {this.state.category.map(function(item) {
                                return <TreeNode key={item.categoryId}
                                                 category={item}
                                                 onCategorySelect={this.onCategorySelect}/>;
                            }.bind(this))}
                        </ul>
                    </div>
                ) : (<CircularProgress mode="indeterminate"/>);
                break;
            case 'admin':
                leftNavContent = (<div>Not implemented yet</div>);
                break;
            case 'user':
                leftNavContent = (<div>Not implemented yet</div>);
                break;
            default:
                leftNavContent = (<div>No Context Navigation</div>);
        }


        //console.log('history', this.props.history);
        //console.log('location', this.props.location);
        //console.log('children', this.props.children);
        return (
            <div id="page_container">
                <LeftNav open={this.state.leftNavOpen} docked={false} onRequestChange={leftNavOpen => this.setState({leftNavOpen})}>
                    {leftNavContent}
                </LeftNav>
                <header>
                    <AppBar title='Edible Forest Garden' onTitleTouchTap={this._onTitleTouchTap} onLeftIconButtonTouchTap={this.handleLeftNavToggle} iconElementRight={rightMenu} zDepth={0}/>
                </header>
                {this.props.children}
            </div>
        );
    }
});

export default Main;