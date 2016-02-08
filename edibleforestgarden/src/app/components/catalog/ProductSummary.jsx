import React from 'react';
import Paper from 'material-ui/lib/paper';
import RaisedButton from 'material-ui/lib/raised-button';
import Gravatar from '../Gravatar';
import Markdown from '../Markdown';
import VariantSelect from './VariantSelect';
import CartActionCreators from '../../actions/CartActionCreators';
import CartStore from '../../stores/CartStore';
import _ from 'lodash';

var ProductSummary = React.createClass ({
    displayName: 'ProductSummary',
    propTypes: {
        product: React.PropTypes.object.isRequired,
        onClick: React.PropTypes.func.isRequired
    },

    getInitialState: function() {
        return {
            product: _.extend(this.props.product, {variantIndex: 0})
        }
    },

    componentWillMount: function() {
        CartStore.addChangeListener(this._onCartStoreChange);
    },

    componentWillUnmount: function() {
        CartStore.removeChangeListener(this._onCartStoreChange);
    },

    _onCartStoreChange: function() {
        // synch inventory with cart
        let inventory = CartStore.getInventory(this.state.product.entityId);
        if(inventory) {
            let variants = this.state.product.variants;
            for (var i = 0; i < variants.length; i++) {
                variants[i].inventory = inventory[variants[i].sku]
            }
        }
    },

    _onAddCart: function() {
        //console.log('ProductSummary._onAddCart', this.state.product);
        CartActionCreators.addToCart(this.state.product);
        // inventory will be updated by CartStore.
    },

    _onVariantSelect: function(event, variantIndex, value) {
        //console.log('ProductSummary._onVariantSelect', event, variantIndex, value);
        this.state.product.variantIndex = variantIndex;
        this.forceUpdate();
    },

    render: function() {
        //console.log('ProductSummary.render this.state.product', this.state.product);
        var variants = this.state.product.variants;
        var i = this.state.product.variantIndex;
        var inventory = variants[i].inventory;
        var price = variants[i].price.toFixed(2);

        return (
            <Paper className="blogPostPaper">
                <div className="blogPost">

                    <img src={'/assets/images/' + this.state.product.variants[i].image} className="img-responsive productImage" />
                    <h3><a onClick={this.props.onClick}>{this.state.product.name}</a></h3>
                    <h4>{ '$' + price}</h4>
                    <div className="cbp-vm-details">
                        <Markdown text={this.state.product.description} />
                    </div>
                    <div className="cbp-vm-variants">
                        {(_.size(this.state.product.variants) > 1) ?
                        <VariantSelect variants={this.state.product.variants} onVariantSelect={this._onVariantSelect} /> :
                        this.state.product.variants[i].type + ' $' + price}
                    </div>
                    <RaisedButton label={inventory > 0 ? 'Add to cart' : 'Sold Out!'}
                                  primary = {true}
                                  onTouchTap={this._onAddCart}
                                  disabled={inventory === 0} />
                </div>
            </Paper>
        );
    }
});

module.exports = ProductSummary;
