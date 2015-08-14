/**
 * Created by steve on 12/04/15.
 */
var React = require('react');
var ReactPropTypes = React.PropTypes;
var CheckoutModal = require('./CheckoutModal');
var Modal = require('react-bootstrap').Modal;
var ModalHeader = require('react-bootstrap').ModalHeader;
var ModalBody = require('react-bootstrap').ModalBody;
var ModalFooter = require('react-bootstrap').ModalFooter;
var Button = require('react-bootstrap').Button;
var CartStore = require('../../stores/CartStore');
var CartActionCreators = require('../../actions/CartActionCreators');

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
    },

    componentDidMount: function() {
        CartStore.addChangeListener(this._onChange);
    },

    componentWillUnmount: function() {
        CartStore.removeChangeListener(this._onChange);
    },

    render: function() {
        return (
            <div>
                <div onClick={this.open}>
                    <span className="glyphicon glyphicon-shopping-cart"></span>
                    <span className="badge">{this.state.cartItemsCount}</span>
                    <span>Checkout</span>
                </div>
                <Modal show={this.state.showModal} onHide={this.close}>
                    <Modal.Header closeButton>
                        <Modal.Title>Modal heading</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <h4>Text in a modal</h4>
                        <h4>Overflowing text to show scroll behavior</h4>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button onClick={this.close}>Close</Button>
                    </Modal.Footer>
                </Modal>
            </div>
        )
    },

    _onChange: function() {
        this.setState(getStateFromStores());
    },

    _toggleCart: function (e) {
        e.preventDefault();
        CartActionCreators.toggleCart();
    }


});

module.exports = CheckoutButton;
