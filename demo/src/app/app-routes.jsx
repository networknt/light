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
import Login from './components/login';
import Logout from './components/logout';
import Form from './components/Form';

import BlogCategory from './components/blog/BlogCategory';
import Blog from './components/blog/Blog';
import BlogPost from './components/blog/BlogPost';
import BlogPostAdd from './components/blog/BlogPostAdd';
import BlogPostUpdate from './components/blog/BlogPostUpdate';

import NewsCategory from './components/news/NewsCategory';
import News from './components/news/News';
import NewsPost from './components/news/NewsPost';
import NewsPostAdd from './components/news/NewsPostAdd';
import NewsPostUpdate from './components/news/NewsPostUpdate';

import CatalogCategory from './components/catalog/CatalogCategory';
import CatalogProductAdd from './components/catalog/CatalogProductAdd';
import CatalogProductUpdate from './components/catalog/CatalogProductUpdate';
import Catalog from './components/catalog/Catalog';
import CatalogProduct from './components/catalog/CatalogProduct';

import Forum from './components/forum';
import Admin from './components/admin/AdminMenu';
import BlogAdminHome from './components/admin/blog/BlogAdminHome';
import DbAdminHome from './components/admin/db/DbAdminHome';
import ExecQueryCommand from './components/admin/db/ExecQueryCommand';
import ExportDatabase from './components/admin/db/ExportDatabase';
import DownloadEvent from './components/admin/db/DownloadEvent';
import User from './components/user';


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
    <Route path='/:root/form/:formId' component={Form} />

    <Route path='/blog' component={BlogCategory} />
    <Route path='/blog/postAdd/:categoryRid' component={BlogPostAdd} />
    <Route path='/blog/postUpdate/:index' component={BlogPostUpdate} />
    <Route path='/blog/:categoryRid' component={Blog} />
    <Route path='/blog/:categoryRid/:index' component={BlogPost} />

    <Route path='/news' component={NewsCategory} />
    <Route path='/news/postAdd/:categoryRid' component={NewsPostAdd} />
    <Route path='/news/postUpdate/:index' component={NewsPostUpdate} />
    <Route path='/news/:categoryRid' component={News} />
    <Route path='/news/:categoryRid/:index' component={NewsPost} />

    <Route path='/forum' component={Forum} />

    <Route path='/catalog' component={CatalogCategory} />
    <Route path='/catalog/productAdd/:categoryRid' component={CatalogProductAdd} />
    <Route path='/catalog/productUpdate/:index' component={CatalogProductUpdate} />
    <Route path='/catalog/:categoryRid' component={Catalog} />
    <Route path='/catalog/:categoryRid/:index' component={CatalogProduct} />

    <Route path='/admin' component={Admin} />
    <Route path='/admin/blogAdmin' component={BlogAdminHome} />
    <Route path='/admin/dbAdmin' component={DbAdminHome} />
    <Route path='/admin/dbAdmin/exportDatabase' component={ExportDatabase} />
    <Route path='/admin/dbAdmin/execQueryCommand' component={ExecQueryCommand} />
    <Route path='/admin/dbAdmin/downloadEvent' component={DownloadEvent} />
    <Route path='/user' component={User} />
  </Route>
);

export default AppRoutes;
