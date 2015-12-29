import React from 'react';
import {
  Route,
  Redirect,
  IndexRoute,
} from 'react-router';

// Here we define all our material-ui ReactComponents.
import Main from './components/main';
import Home from './components/home';
import About from './components/about';
import Contact from './components/contact';
import Signup from './components/signup';
import Login from './components/login';
import Logout from './components/logout';
import Blog from './components/blog';
import News from './components/news';
import Forum from './components/forum';
import Catalog from './components/catalog/Catalog';
import Admin from './components/admin';
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
    <Route path="home" component={Home} />
    <Route path="about" component={About} />
    <Route path="contact" component={Contact} />
    <Route path='signup' component={Signup} />
    <Route path='login' component={Login} />
    <Route path='logout' component={Logout} />
    <Route path='blog' component={Blog} />
    <Route path='news' component={News} />
    <Route path='forum' component={Forum} />
    <Route path='catalog' component={Catalog} />
    <Route path='admin' component={Admin} />
    <Route path='user' component={User} />
  </Route>
);

export default AppRoutes;
