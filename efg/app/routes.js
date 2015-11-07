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
    <Route path="/" component={App}>
        <Route path="about" component={About} />
        <Route path="schemaForm" component={SchemaForm} />
        <Route path="cart" component={Cart} />
        <Route path="contact" component={Contact} />
        <Route path="blog" component={BlogList} />
        <Route path="blog/:blogRid" component={Blog} />
        <Route path="blog/:blogRid/:postId" component={BlogPost} />
        <Route path="news" component={News} />
        <Route path="forum" component={Forum} />
        <Route path="catalog" component={Catalog}/>
        <Route path="admin"  component={Admin} />
        <Route path="admin/roleAdmin" component={RoleAdmin} />
        <Route path="admin/userAdmin" component={UserAdmin} />
        <Route path="admin/blogAdmin" component={BlogAdmin} />
        <Route path="admin/forumAdmin" component={ForumAdmin} />
        <Route path="admin/newsAdmin" component={NewsAdmin} />
        <Route path="admin/productAdmin" component={ProductAdmin} />
        <Route path="admin/catalogAdmin" component={ProductAdmin} />
        <Route path="admin/accessAdmin" component={AccessAdmin} />
        <Route path="admin/dbAdmin" component={DbAdmin} />
        <Route path="admin/menuAdmin" component={MenuAdmin} />
        <Route path="admin/ruleAdmin" component={RuleAdmin} />
        <Route path="admin/formAdmin" component={FormAdmin} />
        <Route path="admin/pageAdmin" component={PageAdmin} />
        <Route path="admin/hostAdmin" component={HostAdmin} />
        <Route path="logIn" component={LogIn}/>
        <Route path="logOut" component={LogOut}/>
        <Route path="signUp" component={SignUp}/>
    </Route>
);
