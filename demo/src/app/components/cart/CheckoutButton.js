/**
 * Created by steve on 12/04/15.
 */
var React = require('react');
var ReactPropTypes = React.PropTypes;
var CartStore = require('../../stores/CartStore');
var CartActionCreators = require('../../actions/CartActionCreators');
var AddressActionCreators = require('../../actions/AddressActionCreators');
var OrderActionCreators = require('../../actions/OrderActionCreators');
var Cart = require('./Cart');
var CheckoutCart = require('./CheckoutCart');
var ShippingAddress = require('./ShippingAddress');
var ShippingTax = require('./ShippingTax');
var CheckoutDone = require('./CheckoutDone');
var Payment = require('./Payment');
var AuthStore = require('../../stores/AuthStore');
var classNames = require('classnames');
var utils = require('react-schema-form/lib/utils.js');

import RaisedButton from 'material-ui/lib/raised-button';
import IconButton from 'material-ui/lib/icon-button';
import Dialog from 'material-ui/lib/dialog';


function getStateFromStores() {
    return {
        cartItems: CartStore.getAll(),
        cartItemsCount: CartStore.getCartItemsCount(),
        cartTotal: CartStore.getCartTotal(),
        cartOpen: CartStore.getCartStatus(),
        shippingAddress: AuthStore.getShippingAddress() || {},
        screen: 'cart',
        title: 'Cart'
    };
}

var CheckoutButton = React.createClass({

    getInitialState: () => getStateFromStores(),

    handleCartClose: function() {
        this.setState({ cartOpen: false, screen: 'cart', title: 'Cart'});
    },

    handleCartTouchTap(event) {
        if(AuthStore.isLoggedIn()) {
            this.setState({cartOpen: true});
        } else {
            this.props.history.push('login');
        }

    },

    open: function() {
        this.setState({cartOpen: true});
    },

    onShipping: function() {
        this.setState({
            screen: 'shippingAddress',
            title: 'Shipping Address'
        })
    },

    onConfirmShippingAddress: function() {
        var data = {};

        data.shippingAddress = this.state.shippingAddress;
        data.cartTotal = this.state.cartTotal;
        data.cartItems = this.state.cartItems;

        AddressActionCreators.confirmShippingAddress(data);

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

    _onCartStoreChange: function() {
        //console.log('onChange is called', this.state.cartItems);
        this.setState({
            cartItems: CartStore.getAll(),
            cartItemsCount: CartStore.getCartItemsCount(),
            cartTotal: CartStore.getCartTotal()
        })
    },

    _onAuthStoreChange: function() {
        this.setState({
            shippingAddress: AuthStore.getShippingAddress() || {}
        })
    },

    componentDidMount: function() {
        CartStore.addChangeListener(this._onCartStoreChange);
        AuthStore.addChangeListener(this._onAuthStoreChange);
    },

    componentWillUnmount: function() {
        CartStore.removeChangeListener(this._onCartStoreChange);
        AuthStore.removeChangeListener(this._onAuthStoreChange);
    },


    render: function() {
        var actions = [];
        var contents;

        if(this.state.screen === 'cart') {
            contents =  <CheckoutCart cartItems = {this.state.cartItems} totalPrice= {this.state.cartTotal} />
            actions.push(<RaisedButton label="Buy now" primary={true} disabled={this.state.cartItems.length > 0? false : true} onTouchTap={this.onShipping} />)
            actions.push(<RaisedButton label="Cancel" secondary={true} onTouchTap={this.handleCartClose} />)
        } else if (this.state.screen === 'shippingAddress') {
            contents =  <ShippingAddress shippingAddress={this.state.shippingAddress} onShippingAddressChange={this.onShippingAddressChange} />
            actions.length = 0;
            actions.push(<RaisedButton label="Update" primary={true} onTouchTap={this.onUpdateShippingAddress} />);
            if(AuthStore.getShippingAddress()) {
                actions.push(<RaisedButton label="Confirm" primary={true} onTouchTap={this.onConfirmShippingAddress}/>);
            }
            actions.push(<RaisedButton label="Cancel" secondary={true} onTouchTap={this.handleCartClose} />);
        } else if (this.state.screen === 'shippingTax') {
            contents =  <ShippingTax cartItems = {this.state.cartItems} totalPrice= {this.state.cartTotal} />;
            actions.length = 0;
            actions.push(<RaisedButton label="Check out" primary={true} onTouchTap={this.onPayment} />);
            actions.push(<RaisedButton label="Cancel" secondary={true} onTouchTap={this.handleCartClose} />);
        } else if (this.state.screen === 'payment') {
            contents =  <Payment onPlaceOrder = {this.onPlaceOrder} />
        } else {
            contents =  <CheckoutDone />
            actions.length = 0;
            actions.push(<RaisedButton label="Close" primary={true} onTouchTap={this.handleCartClose} />);
        }

        var cartHeaderIconClasses = classNames({
            'cart-badge': true
        });

        return (
            <span>
                <IconButton iconClassName="material-icons" onTouchTap={this.handleCartTouchTap}>shopping_cart</IconButton>
                <span className={cartHeaderIconClasses}>{this.state.cartItemsCount}</span>
                <Dialog
                    title={this.state.title}
                    actions={actions}
                    modal={true}
                    open={this.state.cartOpen}>
                    {contents}
                </Dialog>
            </span>
        );

    }


});

module.exports = CheckoutButton;
