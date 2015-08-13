/**
 * Created by steve on 12/04/15.
 */
var React = require('react');
var ReactPropTypes = React.PropTypes;
var CheckoutModal = require('./CheckoutModal');
var ModalTrigger = require('react-bootstrap').ModalTrigger;
var CartStore = require('../../stores/CartStore');
var CartActionCreators = require('../../actions/CartActionCreators');

function getStateFromStores() {
    return {
        cartItems: CartStore.getAll(),
        cartItemsCount: CartStore.getCartItemsCount(),
        cartTotal: CartStore.getCartTotal(),
        isOpen: CartStore.getCartStatus()
    };
}

var CheckoutButton = React.createClass({
    propTypes: {
        cartItems: React.PropTypes.array,
        cartItemsCount: React.PropTypes.number,
        cartTotal: React.PropTypes.number,
        isOpen: React.PropTypes.bool
    },

    getInitialState: () => getStateFromStores(),

    componentDidMount: function() {
        CartStore.addChangeListener(this._onChange);
    },

    componentWillUnmount: function() {
        CartStore.removeChangeListener(this._onChange);
    },

    render: function() {
        return (
            <ModalTrigger modal={<CheckoutModal cartItems={this.state.cartItems} cartTotal={this.state.cartTotal} />}>
                <a href="#cart">
                    <span className="glyphicon glyphicon-shopping-cart"></span>
                    <span className="badge">{this.state.cartItemsCount}</span>
                    <span>Checkout</span>
                </a>
            </ModalTrigger>
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
