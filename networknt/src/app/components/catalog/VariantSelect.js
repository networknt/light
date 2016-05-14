/**
 * Created by steve on 12/04/15.
 */
var React = require('react');
var ProductActionCreator = require('../../actions/ProductActionCreators');
var ReactPropTypes = React.PropTypes;

import DropDownMenu from 'material-ui/lib/DropDownMenu';
import MenuItem from 'material-ui/lib/menus/menu-item';

var VariantSelect = React.createClass({

    propTypes: {
        variants: React.PropTypes.array.isRequired,
        onVariantSelect: React.PropTypes.func.isRequired
    },

    getInitialState: function() {
        return {
            value: 0
        };
    },

    render: function() {
        //console.log('VariantSelect variants', this.props.variants);
        var menuItems = this.props.variants.map(function(variant, index) {
            return <MenuItem key={index} value={index} primaryText={variant.type + ' ' + variant.currency + ' ' + variant.price.toFixed(2)}/>;
        });
        return (
            <DropDownMenu value={this.state.value} onChange={this._setProductVariant}>
                {menuItems}
            </DropDownMenu>
        );
    },

    _setProductVariant: function(e, variantIndex, value) {
        //console.log('VariantSelect._setProductVariant', variantIndex, value);
        this.setState({
            value: variantIndex
        });
        this.props.onVariantSelect(e, variantIndex, value);
    }

});

module.exports = VariantSelect;
