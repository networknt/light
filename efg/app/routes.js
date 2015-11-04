/**
 * Created by steve on 09/07/15.
 */
var React = require('react');
var Router = require('react-router');
var Route = Router.Route;
var DefaultRoute = Router.DefaultRoute;

var App = require('./components/App.js');
var LogIn = require('./components/auth/Login.js');
var LogOut = require('./components/auth/Logout.js');
//var Stories = require('./components/stories/StoriesPage.react.jsx');
//var Story = require('./components/stories/StoryPage.react.jsx');
//var StoryNew = require('./components/stories/StoryNew.react.jsx');
var SignUp = require('./components/auth/Signup.js');
var About = require('./components/common/About.js');
var Contact = require('./components/common/Contact.js');
var BlogList = require('./components/blog/Blogs.js');
var Blog = require('./components/blog/Blog.js');
var BlogPost = require('./components/blog/BlogPost.js');
var News = require('./components/common/News.js');
var Forum = require('./components/common/Forum.js');
var Catalog = require('./components/catalog/Catalog.js');
var Cart = require('./components/cart/Cart.js');
var SchemaForm = require('./components/form/SchemaForm');
var Admin = React.createClass({
    render: function() {
        return <div>Admin</div>;
    }
});

var RoleAdmin = React.createClass({
    render: function() {
        return <div>RoleAdmin</div>;
    }
});
var UserAdmin = React.createClass({
    render: function() {
        return <div>UserAdmin</div>;
    }
});
var BlogAdmin = React.createClass({
    render: function() {
        return <div>BlogAdmin</div>;
    }
});
var AccessAdmin = React.createClass({
    render: function() {
        return <div>AccessAdmin</div>;
    }
});

var DbAdmin = React.createClass({
    render: function() {
        return <div>DbAdmin</div>;
    }
});

var MenuAdmin = React.createClass({
    render: function() {
        return <div>MenuAdmin</div>;
    }
});

var RuleAdmin = React.createClass({
    render: function() {
        return <div>RuleAdmin</div>;
    }
});

var FormAdmin = React.createClass({
    render: function() {
        return <div>FormAdmin</div>;
    }
});

var PageAdmin = React.createClass({
    render: function() {
        return <div>PageAdmin</div>;
    }
});

var HostAdmin = React.createClass({
    render: function() {
        return <div>HostAdmin</div>;
    }
});

var ForumAdmin = React.createClass({
    render: function() {
        return <div>ForumAdmin</div>;
    }
});
var NewsAdmin = React.createClass({
    render: function() {
        return <div>NewsAdmin</div>;
    }
});

var ProductAdmin = React.createClass({
    render: function() {
        return <div>ProductAdmin</div>;
    }
});

var CatalogAdmin = React.createClass({
    render: function() {
        return <div>CatalogAdmin</div>;
    }
});

module.exports = (
    <Route name="app" path="/" component={App}>
        <Route name="about" path="/about" component={About} />
        <Route name="schemaForm" path="/schemaForm" component={SchemaForm} />
        <Route name="cart" path="/cart" component={Cart} />
        <Route name="contact" path="/contact" component={Contact} />
        <Route name="blog" path="/blog" component={BlogList} />
        <Route name="blog/:blogRid" component={Blog} />
        <Route name="blog/:blogRid/:postId" component={BlogPost} />
        <Route name="news" path="/news" component={News} />
        <Route name="forum" path="/forum" component={Forum} />
        <Route name="catalog" path="/catalog" component={Catalog}/>
        <Route name="admin" path="/admin" component={Admin} />
        <Route name="roleAdmin" path="/admin/roleAdmin" component={RoleAdmin} />
        <Route name="userAdmin" path="/admin/userAdmin" component={UserAdmin} />
        <Route name="blogAdmin" path="/admin/blogAdmin" component={BlogAdmin} />
        <Route name="forumAdmin" path="/admin/forumAdmin" component={ForumAdmin} />
        <Route name="newsAdmin" path="/admin/newsAdmin" component={NewsAdmin} />
        <Route name="productAdmin" path="/admin/productAdmin" component={ProductAdmin} />
        <Route name="catalogAdmin" path="/admin/catalogAdmin" component={ProductAdmin} />
        <Route name="accessAdmin" path="/admin/accessAdmin" component={AccessAdmin} />
        <Route name="dbAdmin" path="/admin/dbAdmin" component={DbAdmin} />
        <Route name="menuAdmin" path="/admin/menuAdmin" component={MenuAdmin} />
        <Route name="ruleAdmin" path="/admin/ruleAdmin" component={RuleAdmin} />
        <Route name="formAdmin" path="/admin/formAdmin" component={FormAdmin} />
        <Route name="pageAdmin" path="/admin/pageAdmin" component={PageAdmin} />
        <Route name="hostAdmin" path="/admin/hostAdmin" component={HostAdmin} />
        <Route name="logIn" path="/logIn" component={LogIn}/>
        <Route name="logOut" path="/logOut" component={LogOut}/>
        <Route name="signUp" path="/signUp" component={SignUp}/>
    </Route>
);
