/**
 * Created by steve on 12/04/15.
 */
'use strict';

var React = require('react');
var Product = require('./Product');
var Ancestor = require('../Ancestor');

function getProduct(product, index) {
    return (
        <Product product={product} productIndex={index} key={product.entityId} />
    );
}

function getAncestor(ancestor, index) {
    return (
        <Ancestor ancestor={ancestor} key={index} />
    );
}

var ProductList = React.createClass({
    displayName: 'ProductList',

    render: function() {
        //console.log('products', this.props.products);
        var products = Object.keys(this.props.products).length ===0 ? '' : this.props.products.map(getProduct, this);
        var ancestors = Object.keys(this.props.ancestors).length ===0 ? '' : this.props.ancestors.map(getAncestor, this);
        return (
            <div>
                {ancestors}
                <div className="productList">
                    {products}
                </div>
            </div>
        )
    }

});

module.exports = ProductList;
