/**
 * Created by steve on 12/04/15.
 */
'use strict';

var React = require('react');
var Product = require('./Product');


function getProduct(product, index) {
    return (
        <Product product={product} key={index} />
    );
}

var ProductList = React.createClass({

    render: function() {
        console.log('products', this.props.products);
        var products = Object.keys(this.props.products).length ===0 ? '' : this.props.products.map(getProduct, this);
        return (
            <div className="row">
                {products}
            </div>
        )
    }

})

module.exports = ProductList;
