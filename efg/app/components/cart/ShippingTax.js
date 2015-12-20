/**
 * Created by steve on 04/09/15.
 */
var React = require('react');
var ReactPropTypes = React.PropTypes;
var Modal = require('react-bootstrap').Modal;
var Button = require('react-bootstrap').Button;
var CartStore = require('../../stores/CartStore');
var CartActionCreators = require('../../actions/CartActionCreators');
var Cart = require('./Cart')

var ShippingTax = React.createClass({


    render: function() {
        //console.log('ShippingTax totalPrice = ', this.props.totalPrice);
        var buyButton
        if (this.props.cartItems.length > 0) {
            buyButton = (
                <Button className="btn btn-success" onClick={this.props.onPayment}>
                    CheckOut <span className="glyphicon glyphicon-play"></span>
                </Button>
            )
        }

        return (
            <div>
                <Modal.Header>
                    <Modal.Title>{this.props.title}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Cart cartItems={ this.props.cartItems } totalPrice={ this.props.totalPrice } shipping={CartStore.getShipping()} tax={CartStore.getTax()}/>
                </Modal.Body>
                <Modal.Footer>
                    {buyButton}
                    <Button className="btn btn-default" onClick={this.props.close}>
                        <span className="glyphicon glyphicon-shopping-cart"></span> Continue Shopping
                    </Button>
                </Modal.Footer>
            </div>
        )
    }
});

module.exports = ShippingTax;
