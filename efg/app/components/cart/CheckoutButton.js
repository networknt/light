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
var Cart = require('./Cart');
var Login = require('../auth/Login');
var CheckoutCart = require('./CheckoutCart');
var Shipping = require('./Shipping');
var AuthStore = require('../../stores/AuthStore');
var classNames = require('classnames');

function getStateFromStores() {
    return {
        cartItems: CartStore.getAll(),
        cartItemsCount: CartStore.getCartItemsCount(),
        cartTotal: CartStore.getCartTotal(),
        showModal: CartStore.getCartStatus(),
        screen: 'cart',
        title: 'Cart'
    };
}

var CheckoutButton = React.createClass({

    getInitialState: () => getStateFromStores(),

    close: function() {
        this.setState({ showModal: false, screen: 'cart', title: 'Cart'});
    },

    open: function() {
        this.setState({showModal: true});
        //console.log('cartItems = ', this.state.cartItems);
    },

    onShipping: function() {
        this.setState({
            screen: 'shipping',
            title: 'Shipping'
        })
    },

    componentDidMount: function() {
        CartStore.addChangeListener(this._onChange);
    },

    componentWillUnmount: function() {
        CartStore.removeChangeListener(this._onChange);
    },


    render: function() {
        var buyButton;
        if (this.state.cartItems.length > 0) {
            buyButton = (
                <Button className="btn btn-success" onClick={this.onShipping}>
                    Buy now <span className="glyphicon glyphicon-play"></span>
                </Button>
            )
        }

        var contents;
        if(AuthStore.isLoggedIn()) {
            console.log('is logged in');
            if(this.state.screen === 'cart') {
                contents =  <CheckoutCart cartItems = {this.state.cartItems} totalPrice= {this.state.cartTotal}
                                          title = {this.state.title} close = {this.close} onShipping = {this.onShipping}/>
            } else if (this.state.screen === 'shipping') {
                contents =  <Shipping cartItems = {this.state.cartItems} totalPrice= {this.state.cartTotal}
                                      title = {this.state.title} close = {this.close} onPayment = {this.onPayment} />
            } else if (this.state.screen === 'payment') {
                contents =  <Payment cartItems = {this.state.cartItems} totalPrice= {this.state.cartTotal}
                                     title = {this.state.title} close = {this.close} onPayment = {this.onCheckoutDone} />
            } else {
                contents =  <CheckoutDone cartItems = {this.state.cartItems} totalPrice= {this.state.cartTotal}
                                          title = {this.state.title} close = {this.close} />
            }
        } else {
            console.log('is not logged in');
            contents =  <div className="checkoutButtonLogin">
                            <h3>Login is required before you checkout</h3>
                            <Login/>
                        </div>
        }

        var cartHeaderIconClasses = classNames({
           'badge': true,
            'header-cart-num-items': true,
            'hidden': this.state.cartItemsCount <= 0
        });

        return (
            <div>
                <div onClick={this.open}>
                    <span className="glyphicon glyphicon-shopping-cart"></span>
                    <span className={cartHeaderIconClasses}>{this.state.cartItemsCount}</span>
                    <span className="header-cart-text">Checkout</span>
                </div>
                <Modal show={this.state.showModal} onHide={this.close} className="checkoutButtonModal">
                    {contents}
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
