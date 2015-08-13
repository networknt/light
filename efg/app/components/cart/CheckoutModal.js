/**
 * Created by steve on 12/04/15.
 */
var React = require('react')
var Modal = require('react-bootstrap').Modal
var CheckoutCart = require('./CheckoutCart')
var CheckoutDone = require('./CheckoutDone')

var CheckoutModal = React.createClass({

    render: function() {

        var contents = <CheckoutCart
            cartItems={this.props.cartItems}
            onCheckout={this.onCheckout}
            totalPrice={ this.props.cartTotal.toFixed(2) }
            onRequestHide={this.props.onRequestHide}
             />
        return <Modal {...this.props} title="Cart">{ contents }</Modal>;
    }
});

module.exports = CheckoutModal;
