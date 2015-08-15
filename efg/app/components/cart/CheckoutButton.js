/**
 * Created by steve on 12/04/15.
 */
var React = require('react');
var ReactPropTypes = React.PropTypes;
var Modal = require('react-bootstrap').Modal;
var ModalHeader = require('react-bootstrap').ModalHeader;
var ModalBody = require('react-bootstrap').ModalBody;
var ModalFooter = require('react-bootstrap').ModalFooter;
var Button = require('react-bootstrap').Button;
var CartStore = require('../../stores/CartStore');
var CartActionCreators = require('../../actions/CartActionCreators');
var Cart = require('./Cart')

function getStateFromStores() {
    return {
        cartItems: CartStore.getAll(),
        cartItemsCount: CartStore.getCartItemsCount(),
        cartTotal: CartStore.getCartTotal(),
        showModal: CartStore.getCartStatus()
    };
}

var CheckoutButton = React.createClass({
    propTypes: {
        cartItems: React.PropTypes.array,
        cartItemsCount: React.PropTypes.number,
        cartTotal: React.PropTypes.number,
        showModal: React.PropTypes.bool
    },

    getInitialState: () => getStateFromStores(),

    close: function() {
        this.setState({ showModal: false});
    },

    open: function() {
        this.setState({showModal: true});
        //console.log('cartItems = ', this.state.cartItems);
    },

    componentDidMount: function() {
        CartStore.addChangeListener(this._onChange);
    },

    componentWillUnmount: function() {
        CartStore.removeChangeListener(this._onChange);
    },

    render: function() {
        var buyButton
        if (this.state.cartItems.length > 0) {
            buyButton = (
                <Button className="btn btn-success" onClick={this.state.onCheckout}>
                    Buy now <span className="glyphicon glyphicon-play"></span>
                </Button>
            )
        }

        return (
            <div>
                <div onClick={this.open}>
                    <span className="glyphicon glyphicon-shopping-cart"></span>
                    <span className="badge">{this.state.cartItemsCount}</span>
                    <span>Checkout</span>
                </div>
                <Modal show={this.state.showModal} onHide={this.close}>
                    <Modal.Header closeButton>
                        <Modal.Title>Cart</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <Cart cartItems={ this.state.cartItems } totalPrice={ this.state.cartTotal } />
                    </Modal.Body>
                    <Modal.Footer>
                        {buyButton}
                        <Button className="btn btn-default" onClick={this.close}>
                            <span className="glyphicon glyphicon-shopping-cart"></span> Continue Shopping
                        </Button>
                    </Modal.Footer>
                </Modal>
            </div>
        )
    },

    _onChange: function() {
        this.setState(getStateFromStores());
        console.log('onChange is called', this.state.cartItems);
    }

});

module.exports = CheckoutButton;
