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
        var tax;
        if(this.props.tax) {
            tax = (
                <tr>
                    <td></td>
                    <td>Tax</td>
                    <td className="text-right">
                        <strong>{ this.props.tax.toFixed(2)}</strong>
                    </td>
                </tr>
            )
        }
        var orderTotal;
        if(this.props.shipping && this.props.tax) {
            orderTotal = (
                <tr>
                    <td></td>
                    <td>Total</td>
                    <td className="text-right">
                        <strong>{ (this.props.totalPrice + this.props.shipping + this.props.tax).toFixed(2)}</strong>
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
                    {tax}
                    {orderTotal}
                </tbody>
            </table>
        )
    }

});

module.exports = Cart;
