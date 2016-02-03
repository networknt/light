/**
 * Created by steve on 12/04/15.
 */
'use strict';

var React = require('react');
var ReactPropTypes = React.PropTypes;
var CartItem = require('./CartItem');

var Cart = React.createClass({

    render: function() {
        var cartItems = this.props.cartItems.map((cartItem, index) => {
            console.log(index, cartItem);
            return <CartItem key={index} cartItem={cartItem} />
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
        if(this.props.taxes) {
            for(var key in this.props.taxes) {
                taxes.push(<tr key={key}><td></td><td>{key}</td><td className="text-right"><strong>{ this.props.taxes[key].toFixed(2)}</strong></td></tr>)
                tax += this.props.taxes[key];
            }
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
