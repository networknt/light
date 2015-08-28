/**
 * Created by steve on 12/04/15.
 */
'use strict';

var React = require('react');
var Router = require('react-router')
    , RouteHandler = Router.RouteHandler
    , Route = Router.Route;

var ReactBootstrap = require('react-bootstrap');
var Button = require('react-bootstrap').Button;

var ReactRouterBootstrap = require('react-router-bootstrap')
    , ButtonLink = ReactRouterBootstrap.ButtonLink

var Product = require('./Product');
var Ancestor = require('./Ancestor');

function getProduct(product, index) {
    return (
        <Product product={product} key={index} />
    );
}

function getAncestor(ancestor, index) {
    return (
        <Ancestor ancestor={ancestor} key={index} />
    );
}

var ProductList = React.createClass({
    render: function() {
        console.log('products', this.props.products);
        var products = Object.keys(this.props.products).length ===0 ? '' : this.props.products.map(getProduct, this);
        var ancestors = Object.keys(this.props.ancestors).length ===0 ? '' : this.props.ancestors.map(getAncestor, this);
        return (
            <div>
                {ancestors}
                <div className="row">
                    {products}
                </div>
            </div>
        )
    },
    contextTypes: {
        router: React.PropTypes.func.isRequired
    }

})

module.exports = ProductList;
