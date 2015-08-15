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
        console.log('cartItems = ', this.props.cartItems);
        var cartItems = this.props.cartItems.map(function(cartItem) {
            return <CartItem key={cartItem.id} cartItem={cartItem} />
        });

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
                        <td>Total</td>
                        <td className="text-right">
                            <strong>{ this.props.totalPrice.toFixed(2)}</strong>
                        </td>
                    </tr>
                </tbody>
            </table>
        )
    }

})

module.exports = Cart
