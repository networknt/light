/**
 * Created by steve on 12/04/15.
 */
'use strict';

var React = require('react');
var Product = require('./Product');
var ProductStore = require('../../stores/ProductStore');

function getStateFromStores() {
    return {
        'products': ProductStore.getAll()
    };
}

function getProduct(product, index) {
    return (
        <Product product={product} key={index} />
    );
}

var ProductList = React.createClass({

    getInitialState: () => getStateFromStores(),

    componentDidMount: function() {
        ProductStore.addChangeListener(this._onChange);
    },

    componentWillUnmount: function() {
        ProductStore.removeChangeListener(this._onChange);
    },

    render: function() {
        console.log('products', this.state.products);
        var products = this.state.products.map(getProduct, this);
        return (
            <div className="row">
                {products}
            </div>
        )
    },

    _onChange: function() {
        this.setState(getStateFromStores());
    }

})

module.exports = ProductList;
