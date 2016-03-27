import React from 'react';
import AppBar from 'material-ui/lib/app-bar';
import Footer from './Footer';
import LeftNav from 'material-ui/lib/left-nav';
import Menu from 'material-ui/lib/menus/menu';
import MenuItem from 'material-ui/lib/menus/menu-item';
import IconMenu from 'material-ui/lib/menus/icon-menu';
import IconButton from 'material-ui/lib/icon-button';
import Badge from 'material-ui/lib/badge';
import ThemeManager from 'material-ui/lib/styles/theme-manager';
import LightRawTheme from 'material-ui/lib/styles/raw-themes/light-raw-theme';
import Colors from 'material-ui/lib/styles/colors';
import RaisedButton from 'material-ui/lib/raised-button';
import Snackbar from 'material-ui/lib/snackbar';
import { History } from 'react-router'
import AuthStore from '../stores/AuthStore';
import CartStore from '../stores/CartStore';
import BlogCategoryStore from '../stores/BlogCategoryStore';
import NewsCategoryStore from '../stores/NewsCategoryStore';
import ForumCategoryStore from '../stores/ForumCategoryStore'
import CatalogCategoryStore from '../stores/CatalogCategoryStore';
import ErrorStore from '../stores/ErrorStore';
import MenuStore from '../stores/MenuStore';
import CheckoutButton from './cart/CheckoutButton';
import TreeNode from './TreeNode';
import CatalogActionCreators from '../actions/CatalogActionCreators';
import BlogActionCreators from '../actions/BlogActionCreators';
import NewsActionCreators from '../actions/NewsActionCreators';
import ForumActionCreators from '../actions/ForumActionCreators';
import AuthActionCreators from '../actions/AuthActionCreators';
import MenuActionCreators from '../actions/MenuActionCreators';
import CircularProgress from 'material-ui/lib/circular-progress';
import CommonUtils from '../utils/CommonUtils';
import AppConstants from '../constants/AppConstants';

const defaultPageNo = 1;
const defaultPageSize = 10;

const Main = React.createClass({

    propTypes: {
        children: React.PropTypes.node,
        history: React.PropTypes.object,
        location: React.PropTypes.object
    },

    childContextTypes : {
        muiTheme: React.PropTypes.object
    },

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState() {
        let muiTheme = ThemeManager.getMuiTheme(LightRawTheme);
        return {
            leftNavOpen: false,
            snackbarOpen: false,
            snackbarMessage: "",
            menuItems: [],
            shoppingCartOpen: false,
            muiTheme: muiTheme,
            isLoggedIn: AuthStore.isLoggedIn,
            cartItemCount: 0,
            blogCategory: [],
            newsCategory: [],
            forumCategory: [],
            catalogCategory: []
        };
    },

    componentWillMount() {
        let newMuiTheme = this.state.muiTheme;
        newMuiTheme.inkBar.backgroundColor = Colors.yellow200;
        AuthStore.addChangeListener(this._userLoginChange);
        CartStore.addChangeListener(this._cartItemChange);
        BlogCategoryStore.addChangeListener(this._blogCategoryChange);
        NewsCategoryStore.addChangeListener(this._newsCategoryChange);
        ForumCategoryStore.addChangeListener(this._forumCategoryChange);
        CatalogCategoryStore.addChangeListener(this._catalogCategoryChange);
        ErrorStore.addChangeListener(this._onErrorChange);
        MenuStore.addChangeListener(this._onMenuChange);
        AuthActionCreators.init();
        MenuActionCreators.getMenu();
        this.setState({
            muiTheme: newMuiTheme
        });
    },

    componentWillUnmount: function() {
        AuthStore.removeChangeListener(this._userLoginChange);
        CartStore.removeChangeListener(this._cartItemChange);
        BlogCategoryStore.removeChangeListener(this._blogCategoryChange);
        NewsCategoryStore.removeChangeListener(this._newsCategoryChange);
        ForumCategoryStore.removeChangeListener(this._forumCategoryChange);
        CatalogCategoryStore.removeChangeListener(this._catalogCategoryChange);
        ErrorStore.removeChangeListener(this._onErrorChange);
        MenuStore.removeChangeListener(this._onMenuChange);
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
        this.context.router.push('/');
    },

    _onErrorChange: function() {
        console.log('error', ErrorStore.getError());
        this.setState({
            snackbarOpen: true,
            snackbarMessage: ErrorStore.getStatus() + " " + ErrorStore.getMessage()
        });
    },

    _onMenuChange: function() {
        //console.log('Main._onMenuChange', JSON.stringify( MenuStore.getMenu(), undefined, 2));
        // only care about the first level menuItems here.
        this.setState({
            menuItems : MenuStore.getMenu().out_Own
        });
    },

    _blogCategoryChange: function() {
        this.setState({
            blogCategory: BlogCategoryStore.getCategory()
        });
        // create a fake node to trigger the first node loading for the category
        let node = {};
        let props = {};
        //console.log('blogCategory', JSON.stringify(BlogCategoryStore.getCategory(), undefined, 2));
        props.category = BlogCategoryStore.getCategory()[0];
        node.props = props;
        node.fake = true;
        this.onBlogCategorySelect(node);
    },

    _newsCategoryChange: function() {
        this.setState({
            newsCategory: NewsCategoryStore.getCategory()
        });
        // create a fake node to trigger the first node loading for the category
        let node = {};
        let props = {};
        props.category = NewsCategoryStore.getCategory()[0];
        node.props = props;
        node.fake = true;
        this.onNewsCategorySelect(node);
    },

    _forumCategoryChange: function() {
        this.setState({
            forumCategory: ForumCategoryStore.getCategory()
        });
        // create a fake node to trigger the first node loading for the category
        let node = {};
        let props = {};
        props.category = ForumCategoryStore.getCategory()[0];
        node.props = props;
        node.fake = true;
        this.onForumCategorySelect(node);
    },

    _catalogCategoryChange: function() {
        //console.log('Main._catalogCategoryChange', CatalogCategoryStore.getCategory());
        this.setState({
            catalogCategory: CatalogCategoryStore.getCategory()
        });
        // create a fake node to trigger the first node loading for the category
        let node = {};
        let props = {};
        props.category = CatalogCategoryStore.getCategory()[0];
        node.props = props;
        node.fake = true;
        this.onCatalogCategorySelect(node);
    },

    handleLeftNavToggle() {
        this.setState({leftNavOpen: !this.state.leftNavOpen});
    },

    onBlogCategorySelect(node) {
        // set the select state for the selected category
        if(node.fake) {

        } else {
            if (this.state.selected && this.state.selected.isMounted()) {
                this.state.selected.setState({selected: false});
            }
            this.setState({selected: node});
            node.setState({selected: true});
            if (this.props.onBlogCategorySelect) {
                this.props.onBlogCategorySelect(node);
            }
        }
        // route to Blog with a specific categoryId in the path
        let categoryId = node.props.category.categoryId;
        this.context.router.push('/blog/' + categoryId);
        // if the current location is blog/:categoryId and has different categoryId then the component won't
        // be mount again and there is no way for the component to reload the blogPost. Work around here.
        let secondPath = this.getSecondPath(this.props.location.pathname);
        //console.log('before workaround', this.props.location.pathname, secondPath, rid);
        if(secondPath != null && secondPath != categoryId) {
            //console.log('The main window has the same route, force to reload blogPost...');
            BlogActionCreators.getBlogPost(node.props.category['@rid'], defaultPageNo, defaultPageSize);
        }
    },

    onNewsCategorySelect(node) {
        // set the select state for the selected category
        if(node.fake) {

        } else {
            if (this.state.selected && this.state.selected.isMounted()) {
                this.state.selected.setState({selected: false});
            }
            this.setState({selected: node});
            node.setState({selected: true});
            if (this.props.onNewsCategorySelect) {
                this.props.onNewsCategorySelect(node);
            }
        }
        // route to News with a specific categoryId in the path
        let categoryId = node.props.category.categoryId;
        this.context.router.push('/news/' + categoryId);
        // if the current location is blog/:blogRid and has different blogRid then the component won't
        // be mount again and there is no way for the component to reload the blogPost. Work around here.
        let secondPath = this.getSecondPath(this.props.location.pathname);
        //console.log('before workaround', this.props.location.pathname, secondPath, rid);
        if(secondPath != null && secondPath != categoryId) {
            //console.log('The main window has the same route, force to reload blogPost...');
            NewsActionCreators.getNewsPost(node.props.category['@rid'], defaultPageNo, defaultPageSize);
        }
    },

    onForumCategorySelect(node) {
        // set the select state for the selected category
        if(node.fake) {

        } else {
            if (this.state.selected && this.state.selected.isMounted()) {
                this.state.selected.setState({selected: false});
            }
            this.setState({selected: node});
            node.setState({selected: true});
            if (this.props.onForumCategorySelect) {
                this.props.onForumCategorySelect(node);
            }
        }
        // route to Forum with a specific categoryId in the path
        let categoryId = node.props.category.categoryId;
        this.context.router.push('/forum/' + categoryId);
        // if the current location is forum/:forumRid and has different forumRid then the component won't
        // be mount again and there is no way for the component to reload the forumPost. Work around here.
        let secondPath = this.getSecondPath(this.props.location.pathname);
        //console.log('before workaround', this.props.location.pathname, secondPath, rid);
        if(secondPath != null && secondPath != categoryId) {
            //console.log('The main window has the same route, force to reload forumPost...');
            ForumActionCreators.getForumPost(node.props.category['@rid'], defaultPageNo, defaultPageSize);
        }
    },

    onCatalogCategorySelect(node) {
        // set the select state for the selected category
        if(node.fake) {

        } else {
            if (this.state.selected && this.state.selected.isMounted()) {
                this.state.selected.setState({selected: false});
            }
            this.setState({selected: node});
            node.setState({selected: true});
            if (this.props.onCatalogCategorySelect) {
                this.props.onCatalogCategorySelect(node);
            }
        }
        // route to Catalog with a specific categoryId in the path
        let categoryId = node.props.category.categoryId;
        this.context.router.push('/catalog/' + categoryId);
        // if the current location is blog/:blogRid and has different blogRid then the component won't
        // be mount again and there is no way for the component to reload the blogPost. Work around here.
        let secondPath = this.getSecondPath(this.props.location.pathname);
        //console.log('before workaround', this.props.location.pathname, secondPath, rid);
        if(secondPath != null && secondPath != categoryId) {
            //console.log('The main window has the same route, force to reload blogPost...');
            CatalogActionCreators.getCatalogProduct(node.props.category['@rid'], defaultPageNo, defaultPageSize);
        }
    },

    handleItemTouchTap(event, item) {
        // clear category as context has switched. waiting for the new category to be loaded.
        this.setState({leftNavOpen: false, category: []});
        this.context.router.push(item.props.value);
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

    handleSnackbarClose() {
        this.setState({snackbarOpen: false})
    },

    handleSnackbarTouchTap() {
        this.setState({snackbarOpen: false})
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
            loginMenuItems.push(<MenuItem key='logout' value='/logout' primaryText='Sign out' />)
        } else {
            loginMenuItems.push(<MenuItem key='login' value='/login' primaryText='Log in' />);
            loginMenuItems.push(<MenuItem key='signup' value='/form/com.networknt.light.user.signup' primaryText='Sign up' />);
        }

        let cartButton = '';
        if (CommonUtils.findMenuItem(this.state.menuItems, 'cart')) {
            cartButton = <CheckoutButton/>
        }
        let mainMenu = '';
        if (CommonUtils.findMenuItem(this.state.menuItems, 'main')) {
            let mainMenuItems = CommonUtils.findMenuItem(this.state.menuItems, 'main').out_Own;
            //console.log('mainMenuItems', mainMenuItems);
            mainMenu = mainMenuItems.map((item, index) => {
                if(CommonUtils.hasMenuAccess(item, AuthStore.getRoles())) {
                    return (
                        <MenuItem
                            key={index}
                            primaryText={item.text}
                            value={item.route}
                            />
                    );
                }
            });
        }

        var rightMenu = (
            <div>
                {cartButton}
                <IconMenu iconButtonElement={userButton}
                          openDirection="bottom-left"
                          onItemTouchTap={this.handleItemTouchTap}>
                    {loginMenuItems}
                </IconMenu>
                <IconMenu iconButtonElement={menuButton}
                          openDirection="bottom-left"
                          onItemTouchTap={this.handleItemTouchTap}>
                    {mainMenu}
                </IconMenu>
            </div>
        );

        var leftNavContent;
        //console.log('Main.render pahtnaeme', this.getFirstPath(this.props.location.pathname));
        switch(this.getFirstPath(this.props.location.pathname)) {
            case 'blog':
                leftNavContent = this.state.blogCategory.length > 0 ? (
                    <div>
                        <ul className="category-tree">
                            {this.state.blogCategory.map(function(item) {
                                return <TreeNode key={item.categoryId}
                                                 category={item}
                                                 onCategorySelect={this.onBlogCategorySelect}/>;
                            }.bind(this))}
                        </ul>
                    </div>
                ) : (<CircularProgress mode="indeterminate"/>);
                break;
            case 'news':
                leftNavContent = this.state.newsCategory.length > 0 ? (
                    <div>
                        <ul className="category-tree">
                            {this.state.newsCategory.map(function(item) {
                                return <TreeNode key={item.categoryId}
                                                 category={item}
                                                 onCategorySelect={this.onNewsCategorySelect}/>;
                            }.bind(this))}
                        </ul>
                    </div>
                ) : (<CircularProgress mode="indeterminate"/>);
                break;
            case 'forum':
                leftNavContent = this.state.forumCategory.length > 0 ? (
                    <div>
                        <ul className="category-tree">
                            {this.state.forumCategory.map(function(item) {
                                return <TreeNode key={item.categoryId}
                                                 category={item}
                                                 onCategorySelect={this.onForumCategorySelect}/>;
                            }.bind(this))}
                        </ul>
                    </div>
                ) : (<CircularProgress mode="indeterminate"/>);
                break;
            case 'catalog':
                leftNavContent = this.state.catalogCategory.length > 0 ? (
                    <div>
                        <ul className="category-tree">
                            {this.state.catalogCategory.map(function(item) {
                                return <TreeNode key={item.categoryId}
                                                 category={item}
                                                 onCategorySelect={this.onCatalogCategorySelect}/>;
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

        return (
            <div id="page_container">
                <LeftNav open={this.state.leftNavOpen} docked={false} onRequestChange={leftNavOpen => this.setState({leftNavOpen})}>
                    {leftNavContent}
                </LeftNav>
                <header>
                    <AppBar title={AppConstants.Site} onTitleTouchTap={this._onTitleTouchTap} onLeftIconButtonTouchTap={this.handleLeftNavToggle} iconElementRight={rightMenu} zDepth={0} className="mainAppBar"/>
                </header>
                <Snackbar open={this.state.snackbarOpen} message={this.state.snackbarMessage} action="Close" autoHideDuration={3000} onActionTouchTap={this.handleSnackbarTouchTap} onRequestClose={this.handleSnackbarClose} />
                <span className="mainRoot">
                    {this.props.children}
                </span>
                <footer><Footer /></footer>
            </div>
        );
    }
});

export default Main;
export default Main;