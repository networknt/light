/**
 * Created by steve on 12/04/15.
 */
var Cart = require('./Cart')
var Modal = require('react-bootstrap').Modal
var React = require('react')

var CheckoutCart = React.createClass({
    render: function() {
        var buyButton
        if (this.props.cartItems.length > 0) {
            buyButton = (
                <button type="button" className="btn btn-success" onClick={this.props.onCheckout}>
                    Buy now <span className="glyphicon glyphicon-play"></span>
                </button>
            )
        }

        return (
            <div>
                <div className="modal-body">
                    <Cart cartItems={ this.props.cartItems } totalPrice={ this.props.totalPrice } />
                </div>
                <div className="modal-footer">
                    <button type="button" className="btn btn-default" onClick={this.props.onRequestHide}>
                        <span className="glyphicon glyphicon-shopping-cart"></span> Continue Shopping
                    </button>
                    {buyButton}
                </div>
            </div>
        )
    }
})

module.exports = CheckoutCart
