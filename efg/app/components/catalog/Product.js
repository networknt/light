/**
 * Created by steve on 12/04/15.
 */
'use strict';

var React = require('react');
var ReactPropTypes = React.PropTypes;
var ProductActionCreator = require('../../actions/ProductActionCreators');
var CartActionCreator = require('../../actions/CartActionCreators');
var VariantSelect = require('./VariantSelect');
var _ = require('lodash');
var {Button, Panel, Well} = require('react-bootstrap');

var Product = React.createClass({
    displayName: 'Product',

    propTypes: {
        product: React.PropTypes.object.isRequired
    },

    render: function() {

        var product = this.props.product;
        product.index = this.props.productIndex;
        //console.log('Product product = ', product);
        var variantIndex, i = product.variantIndex;
        var variants = product.variants;
        var inventory = variants[i].inventory;
        var price = variants[i].price.toFixed(2);
        var variantProps = {
            variants: variants,
            index: product.index
        };

        return (
            <Well className="productPanel">
                <img src={'/assets/images/' + product.variants[i].image} className="img-responsive productImage" />
                <h3>{product.title}</h3>
                <h4>{ '$' + price }</h4>
                <div className="cbp-vm-details">
                    {product.description}
                </div>
                <div className="cbp-vm-variants">
                    {(_.size(variants) > 1) ?
                        <VariantSelect {...variantProps} /> : product.variants[i].type + ' $' + price}
                </div>
                <Button className="cbp-vm-icon cbp-vm-add productAddButton"
                        onClick={this._addToCart}
                        disabled={inventory === 0}>
                    {inventory > 0 ? 'Add to cart' : 'Sold Out!'}
                </Button>
            </Well>
        )
    },

    _addToCart: function(e) {
        e.preventDefault();
        var product = this.props.product;
        CartActionCreator.addToCart(product);
        //ProductActionCreator.removeOneFromInventory(product);
    }
});

module.exports = Product;