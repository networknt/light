import React from 'react';
import Paper from 'material-ui/lib/paper';
import RaisedButton from 'material-ui/lib/raised-button';
import Gravatar from '../Gravatar';
import Markdown from '../Markdown';
import VariantSelect from './VariantSelect';
import CartActionCreators from '../../actions/CartActionCreators';
import _ from 'lodash';

class ProductSummary extends React.Component {

    constructor(props) {
        super(props);
        this._onVariantSelect = this._onVariantSelect.bind(this);
        this._onAddCart = this._onAddCart.bind(this);
        this.state = {
            product: _.extend(this.props.product, {variantIndex: 0})
        };
    }

    _onAddCart() {
        console.log('ProductSummary._onAddCart', this.state.product);
        CartActionCreators.addToCart(this.state.product);
        // update product inventory
        let variant = this.state.product.variants[this.state.product.variantIndex];
        variant.inventory = variant.inventory - 1;
        this.forceUpdate();
    }

    _onVariantSelect(event, variantIndex, value) {
        console.log('ProductSummary._onVariantSelect', event, variantIndex, value);
        this.state.product.variantIndex = variantIndex;
        this.forceUpdate();
    }

    render() {
        console.log('ProductSummary.render this.state.product', this.state.product);
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
}

ProductSummary.propTypes = {
    product: React.PropTypes.object.isRequired,
    onClick: React.PropTypes.func.isRequired
};

export default ProductSummary;
