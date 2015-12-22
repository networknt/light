/**
 * Created by steve on 12/04/15.
 */
var React = require('react');
var ReactPropTypes = React.PropTypes;
var Modal = require('react-bootstrap').Modal;
var Button = require('react-bootstrap').Button;
var CartStore = require('../../stores/CartStore');
var CartActionCreators = require('../../actions/CartActionCreators');
var AddressActionCreators = require('../../actions/AddressActionCreators');
var OrderActionCreators = require('../../actions/OrderActionCreators');
var Cart = require('./Cart');
var Login = require('../auth/Login');
var CheckoutCart = require('./CheckoutCart');
var Shipping = require('./Shipping');
var ShippingTax = require('./ShippingTax');
var CheckoutDone = require('./CheckoutDone');
var Payment = require('./Payment');
var AuthStore = require('../../stores/AuthStore');
var classNames = require('classnames');
var utils = require('react-schema-form/lib/utils.js');


function getStateFromStores() {
    return {
        cartItems: CartStore.getAll(),
        cartItemsCount: CartStore.getCartItemsCount(),
        cartTotal: CartStore.getCartTotal(),
        showModal: CartStore.getCartStatus(),
        shippingAddress: AuthStore.getShippingAddress() || {},
        screen: 'cart',
        title: 'Cart'
    };
}

var CheckoutButton = React.createClass({

    getInitialState: () => getStateFromStores(),

    close: function() {
        //console.log('CheckoutButton close is called');
        this.setState({ showModal: false, screen: 'cart', title: 'Cart'});
    },

    open: function() {
        this.setState({showModal: true});
        //console.log('cartItems = ', this.state.cartItems);
    },

    onShipping: function() {
        this.setState({
            screen: 'shipping',
            title: 'Shipping Address'
        })
    },

    onConfirmShippingAddress: function() {

        this.setState({
            screen: 'shippingTax',
            title: 'Shipping and Tax'
        })
    },

    onUpdateShippingAddress: function() {
        // call API to update
        //console.log('onUpdateShippingAddress', this.state.shippingAddress);
        //console.log('onUpdateShippingAddress cartItems', this.state.cartItems);
        var data = {};

        data.shippingAddress = this.state.shippingAddress;
        data.cartTotal = this.state.cartTotal;
        data.cartItems = this.state.cartItems;

        //console.log('onUpdateShippingAddres data = ', data);
        AddressActionCreators.updateShippingAddress(data);
        this.setState({
            screen: 'shippingTax',
            title: 'Shipping and Tax'
        })
    },

    onShippingAddressChange: function(key, val) {
        //console.log('ExamplePage.onModelChange:', key);
        //console.log('ExamplePage.onModelChange:', val);
        this.setState({shippingAddress: utils.selectOrSet(key, this.state.shippingAddress, val)});
    },

    onPayment: function() {
        // before switching to payment gateway, save the order here.
        var order = {};
        // all the numbers should be calculated on the server and only items should be passed here
        // need at least @rid/productId, sku, quantity in order to calculate all the numbers.
        //console.log('cartItems', this.state.cartItems);
        var items = [];
        this.state.cartItems.forEach(function(cartItem) {
            var item = {};
            item.rid = cartItem.rid;
            item.sku = cartItem.sku;
            item.qty = cartItem.qty;
            items.push(item);
        });
        order.items = items;
        //console.log('order', order);

        OrderActionCreators.addOrder(order);
        this.setState({
            screen: 'payment',
            title: 'BrainTree Payment Gateway'
        })
    },

    onPlaceOrder: function() {
        this.setState({
            screen: 'checkoutDone',
            title: 'Order Summary'
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
            //console.log('is logged in');
            if(this.state.screen === 'cart') {
                contents =  <CheckoutCart cartItems = {this.state.cartItems} totalPrice= {this.state.cartTotal}
                                          title = {this.state.title} close = {this.close} onShipping = {this.onShipping}/>
            } else if (this.state.screen === 'shipping') {
                contents =  <Shipping cartItems = {this.state.cartItems} totalPrice= {this.state.cartTotal}
                                      title = {this.state.title} close = {this.close}
                                      onConfirmShippingAddress = {this.onConfirmShippingAddress}
                                      onUpdateShippingAddress = {this.onUpdateShippingAddress}
                                      shippingAddress={this.state.shippingAddress}
                                      onShippingAddressChange={this.onShippingAddressChange}

                    />
            } else if (this.state.screen === 'shippingTax') {
                contents =  <ShippingTax cartItems = {this.state.cartItems} totalPrice= {this.state.cartTotal}
                                     title = {this.state.title} close = {this.close} onPayment = {this.onPayment} />
            } else if (this.state.screen === 'payment') {
                contents =  <Payment cartItems = {this.state.cartItems} totalPrice= {this.state.cartTotal}
                                     title = {this.state.title} close = {this.close} onPlaceOrder = {this.onPlaceOrder} />
            } else {
                contents =  <CheckoutDone title = {this.state.title} close = {this.close} />
            }
        } else {
            contents =  <div className="checkoutButtonLogin">
                            <Modal.Title>Login required before checkout...</Modal.Title>
                            <Login/>
                        </div>
        }
        return (
            <div>
                <div onClick={this.open}>
                    <span className="glyphicon glyphicon-shopping-cart"></span>
                    <span className={cartHeaderIconClasses}>{this.state.cartItemsCount}</span>
                    <span className="header-cart-text">Checkout</span>
                </div>
                <Modal show={this.state.showModal} onHide={this.close} className="checkoutButtonModal">
                    <Modal.Body>
                        {contents}
                    </Modal.Body>
                </Modal>
            </div>
        );


        var cartHeaderIconClasses = classNames({
           'badge': true,
            'header-cart-num-items': true,
            'hidden': this.state.cartItemsCount <= 0
        });
    },

    _onChange: function() {
        //console.log('onChange is called', this.state.cartItems);
        this.setState({
            cartItems: CartStore.getAll(),
            cartItemsCount: CartStore.getCartItemsCount(),
            cartTotal: CartStore.getCartTotal()
        })
    }

});

module.exports = CheckoutButton;
