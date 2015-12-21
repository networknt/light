/**
 * Created by steve on 12/04/15.
 */
'use strict';

var React = require('react');
var ReactPropTypes = React.PropTypes;
var CartItem = require('./CartItem');
var CartStore = require('../../stores/CartStore');
var CartActionCreators = require('../../actions/CartActionCreators.js');

var Cart = React.createClass({


    render: function() {
        //console.log('cartItems = ', this.props.cartItems);
        var cartItems = this.props.cartItems.map(function(cartItem) {
            return <CartItem key={cartItem.index} cartItem={cartItem} />
        });

        var shipping;
        if(this.props.shipping) {
            shipping = (
                <tr>
                    <td></td>
                    <td>Shipping</td>
                    <td className="text-right">
                        <strong>{ this.props.shipping.toFixed(2)}</strong>
                    </td>
                </tr>
            );
        }
        var taxes = [];
        let tax = 0.0;
        console.log('Cart this.props.taxes = ', this.props.taxes);
        if(this.props.taxes) {
            console.log('Cart taxes is not null');
            for(var key in this.props.taxes) {
                taxes.push(<tr><td></td><td>{key}</td><td className="text-right"><strong>{ this.props.taxes[key].toFixed(2)}</strong></td></tr>)
                tax += this.props.taxes[key];
            }
            console.log('Cart taxes = ', taxes);
        }
        var orderTotal;
        if(this.props.shipping && this.props.taxes) {
            orderTotal = (
                <tr>
                    <td></td>
                    <td>Total</td>
                    <td className="text-right">
                        <strong>{ (this.props.totalPrice + this.props.shipping + tax).toFixed(2)}</strong>
                    </td>
                </tr>
            )
        }

        return (
            <table className="table table-hover">
                <thead>
                    <tr>
                        <th>Product</th>
                        <th className="text-center">Quantity</th>
                        <th className="text-center">Total</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    {cartItems}
                    <tr>
                        <td></td>
                        <td>Subtotal</td>
                        <td className="text-right">
                            <strong>{ this.props.totalPrice.toFixed(2)}</strong>
                        </td>
                    </tr>
                    {shipping}
                    {taxes}
                    {orderTotal}
                </tbody>
            </table>
        )
    }

});

module.exports = Cart;
