import React from 'react';
import Paper from 'material-ui/lib/paper';
import Card from 'material-ui/lib/card/card';
import CardActions from 'material-ui/lib/card/card-actions';
import CardHeader from 'material-ui/lib/card/card-header';
import CardTitle from 'material-ui/lib/card/card-title';
import CardText from 'material-ui/lib/card/card-text';
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
        var variants = this.state.product.variants;
        var i = this.state.product.variantIndex;
        var inventory = variants[i].inventory;
        var price = variants[i].price.toFixed(2);

        return (
            <Paper className="summaryPaper">
                <Card>
                    <a onClick={this.props.onClick}><CardTitle title={this.state.product.name} /></a>
                    <CardText>
                        <div className="productLeft">
                            <a onClick={this.props.onClick}><img src={'/images/' + this.state.product.variants[i].image} className="img-responsive productImage" /></a>
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
                        <div className="productRight">
                            <Markdown text={this.state.product.description} />
                        </div>
                    </CardText>
                </Card>
            </Paper>
        );
    }
});

module.exports = ProductSummary;

