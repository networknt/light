import React from 'react';
import {
  Route,
  Redirect,
  IndexRoute,
} from 'react-router';

// Here we define all our material-ui ReactComponents.
import Main from './components/Main';
import Home from './components/Home';
import About from './components/about';
import Contact from './components/contact';
import Signup from './components/signup';
import Login from './components/Login';
import Logout from './components/logout';
import Form from './components/Form';
import Page from './components/Page';
import Profile from './components/Profile';

import BlogCategory from './components/blog/BlogCategory';
import Blog from './components/blog/Blog';
import BlogPost from './components/blog/BlogPost';
import BlogPostAdd from './components/blog/BlogPostAdd';
import BlogPostUpdate from './components/blog/BlogPostUpdate';
import BlogRecentPost from './components/blog/BlogRecentPost';

import NewsCategory from './components/news/NewsCategory';
import News from './components/news/News';
import NewsPost from './components/news/NewsPost';
import NewsPostAdd from './components/news/NewsPostAdd';
import NewsPostUpdate from './components/news/NewsPostUpdate';
import NewsRecentPost from './components/news/NewsRecentPost';

import CatalogCategory from './components/catalog/CatalogCategory';
import CatalogProductAdd from './components/catalog/CatalogProductAdd';
import CatalogProductUpdate from './components/catalog/CatalogProductUpdate';
import Catalog from './components/catalog/Catalog';
import CatalogProduct from './components/catalog/CatalogProduct';

import Forum from './components/forum';
import Admin from './components/admin/AdminMenu';
import BlogAdminHome from './components/admin/blog/BlogAdminHome';
import NewsAdminHome from './components/admin/news/NewsAdminHome';
import CatalogAdminHome from './components/admin/catalog/CatalogAdminHome';
import DbAdminHome from './components/admin/db/DbAdminHome';
import ExecQueryCommand from './components/admin/db/ExecQueryCommand';
import ExportDatabase from './components/admin/db/ExportDatabase';
import DownloadEvent from './components/admin/db/DownloadEvent';
import AccessAdminHome from './components/admin/access/AccessAdminHome';
import RoleAdminHome from './components/admin/role/RoleAdminHome';

/**
 * Routes: https://github.com/rackt/react-router/blob/master/docs/api/components/Route.md
 *
 * Routes are used to declare your view hierarchy.
 *
 * Say you go to http://material-ui.com/#/components/paper
 * The react router will search for a route named 'paper' and will recursively render its
 * handler and its parent handler like so: Paper > Components > Master
 */
const AppRoutes = (
  <Route path="/" component={Main}>
    <Route path="/home" component={Home} />
    <Route path="/about" component={About} />
    <Route path="/contact" component={Contact} />
    <Route path='/signup' component={Signup} />
    <Route path='/login' component={Login} />
    <Route path='/logout' component={Logout} />
    <Route path='/profile' component={Profile} />

    <Route path='/form/:formId' component={Form} />
    <Route path='/page/:pageId' component={Page} />


    <Route path='/blog' component={BlogCategory} />
    <Route path='/blog/postAdd/:categoryId' component={BlogPostAdd} />
    <Route path='/blog/postUpdate/:postId' component={BlogPostUpdate} />
    <Route path='/blog/:categoryId' component={Blog} />
    <Route path='/blog/:categoryId/:postId' component={BlogPost} />
    <Route path='/recentBlogPost' component={BlogRecentPost} />

    <Route path='/news' component={NewsCategory} />
    <Route path='/news/postAdd/:categoryId' component={NewsPostAdd} />
    <Route path='/news/postUpdate/:postId' component={NewsPostUpdate} />
    <Route path='/news/:categoryId' component={News} />
    <Route path='/news/:categoryId/:postId' component={NewsPost} />
    <Route path='/recentNewsPost' component={NewsRecentPost} />

    <Route path='/forum' component={Forum} />


    <Route path='/catalog' component={CatalogCategory} />
    <Route path='/catalog/productAdd/:categoryId' component={CatalogProductAdd} />
    <Route path='/catalog/productUpdate/:productId' component={CatalogProductUpdate} />
    <Route path='/catalog/:categoryId' component={Catalog} />
    <Route path='/catalog/:categoryId/:productId' component={CatalogProduct} />


    <Route path='/admin' component={Admin} />
    <Route path='/admin/blogAdmin' component={BlogAdminHome} />
    <Route path='/admin/newsAdmin' component={NewsAdminHome} />
    <Route path='/admin/catalogAdmin' component={CatalogAdminHome} />
    <Route path='/admin/dbAdmin' component={DbAdminHome} />
    <Route path='/admin/dbAdmin/exportDatabase' component={ExportDatabase} />
    <Route path='/admin/dbAdmin/execQueryCommand' component={ExecQueryCommand} />
    <Route path='/admin/dbAdmin/downloadEvent' component={DownloadEvent} />
    <Route path='/admin/accessAdmin' component={AccessAdminHome} />
    <Route path='/admin/roleAdmin' component={RoleAdminHome} />

    <IndexRoute component={Home}/>

  </Route>
);

export default AppRoutes;
