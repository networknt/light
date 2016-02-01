import React from 'react';
import Paper from 'material-ui/lib/paper';
import RaisedButton from 'material-ui/lib/raised-button';
import Gravatar from '../Gravatar';
import Markdown from '../Markdown';
import VariantSelect from './VariantSelect';

class ProductSummary extends React.Component {

    render() {
        var variants = this.props.product.variants;
        var i = this.props.product.variantIndex;
        var inventory = variants[i].inventory;
        var price = variants[i].price.toFixed(2);
        var variantProps = {
            variants: this.props.product.variants,
            index: this.props.index
        };

        return (
            <Paper className="blogPostPaper">
                <div className="blogPost">

                    <img src={'/assets/images/' + this.props.product.variants[i].image} className="img-responsive productImage" />
                    <h3><a onClick={this.props.onClick}>{this.props.product.name}</a></h3>
                    <h4>{ '$' + price}</h4>
                    <div className="cbp-vm-details">
                        <Markdown text={this.props.product.description} />
                    </div>
                    <div className="cbp-vm-variants">
                        {(_.size(variants) > 1) ?
                            <VariantSelect {...variantProps} /> : this.props.product.variants[i].type + ' $' + price}
                    </div>
                    <RaisedButton label={inventory > 0 ? 'Add to cart' : 'Sold Out!'}
                                  primary = {true}
                                  onTouchTap={this.props.onAddCart}
                                  disabled={inventory === 0} />
                </div>
            </Paper>
        );
    }
}

ProductSummary.propTypes = {
    product: React.PropTypes.object.isRequired,
    index: React.PropTypes.number.isRequired,
    onClick: React.PropTypes.func.isRequired,
    onAddCart: React.PropTypes.func.isRequired
};

export default ProductSummary;
