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
import Badge from 'material-ui/lib/badge';
import NotificationsIcon from 'material-ui/lib/svg-icons/social/notifications';
import FormActionCreators from '../../actions/FormActionCreators';
import FormStore from '../../stores/FormStore';
import UserStore from '../../stores/UserStore';
import UserActionCreators from '../../actions/UserActionCreators';
import CircularProgress from 'material-ui/lib/circular-progress';


function getStateFromStores() {
    return {
        cartItems: CartStore.getAll(),
        cartItemsCount: CartStore.getCartItemsCount(),
        cartTotal: CartStore.getCartTotal(),
        cartOpen: CartStore.getCartStatus(),
        screen: 'cart',
        title: 'Cart'
    };
}

const id = 'com.networknt.light.user.address';
var CheckoutButton = React.createClass({

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: () => getStateFromStores(),

    handleCartClose: function() {
        this.setState({ cartOpen: false, screen: 'cart', title: 'Cart'});
    },

    handleCartReset: function() {
        CartActionCreators.reset();
        this.setState({ cartOpen: false, screen: 'cart', title: 'Cart'});
    },

    handleCartTouchTap(event) {
        if(AuthStore.isLoggedIn()) {
            this.setState({cartOpen: true});
            console.log('CheckoutButton.handleCartTouchTap state is set to open');
        } else {
            this.context.router.push('login');
        }

    },

    open: function() {
        this.setState({cartOpen: true});
    },

    onDelivery: function() {
        // calculate what delivery options available based on the items in the cart.


        this.setState({
            screen: 'shippingPickup',
            title: 'Shipping or Pickup'
        });

        this.setState({
            screen: 'pickupAddress',
            title: 'Pickup Address'
        });

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
        var data = {};
        data.shippingAddress = this.state.shippingAddress;
        data.cartTotal = this.state.cartTotal;
        data.cartItems = this.state.cartItems;
        // validate the address here.
        let validationResult = utils.validateBySchema(this.state.schema, this.state.shippingAddress);
        if(!validationResult.valid) {
            console.log('error = ', validationResult.error.message);
            this.setState({error: validationResult.error.message});
        } else {
            AddressActionCreators.updateShippingAddress(data);
            this.setState({
                screen: 'shippingTax',
                title: 'Shipping and Tax'
            })
        }
    },

    onShippingAddressChange: function(key, val) {
        utils.selectOrSet(key, this.state.shippingAddress, val);
    },

    onPayment: function() {
        // before switching to payment gateway, save the order here.
        var order = {};
        // all the numbers should be calculated on the server and only items should be passed here
        // need at least @rid/entityId, sku, quantity in order to calculate all the numbers.
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

    _onUserStoreChange: function() {
        this.setState({
            shippingAddress: UserStore.getUser().shippingAddress || {}
        })
    },

    _onFormStoreChange: function() {
        this.setState({
            schema: FormStore.getForm(id).schema,
            form: FormStore.getForm(id).form
        });
    },

    componentDidMount: function() {
        CartStore.addChangeListener(this._onCartStoreChange);
        UserStore.addChangeListener(this._onUserStoreChange);
        FormStore.addChangeListener(this._onFormStoreChange);
        FormActionCreators.getForm(id);
        if(UserStore.getUser()) {
            this.setState({
                shippingAddress: UserStore.getUser().shippingAddress || {}
            })
        } else {
            UserActionCreators.getUser(AuthStore.getUserId());
        }
    },

    componentWillUnmount: function() {
        CartStore.removeChangeListener(this._onCartStoreChange);
        UserStore.removeChangeListener(this._onUserStoreChange);
        FormStore.removeChangeListener(this._onFormStoreChange);
    },


    render: function() {
        var actions = [];
        var contents;

        if(this.state.screen === 'cart') {
            contents =  <CheckoutCart cartItems = {this.state.cartItems} totalPrice= {this.state.cartTotal} />;
            actions.push(<RaisedButton label="Buy now" primary={true} disabled={this.state.cartItems.length > 0? false : true} onTouchTap={this.onDelivery} />);
            actions.push(<RaisedButton label="Cancel" secondary={true} onTouchTap={this.handleCartClose} />)
        } else if (this.state.screen === 'shippingAddress') {
            if(this.state.schema) {
                contents =
                    (<div>
                        <pre>{this.state.error}</pre>
                        <ShippingAddress shippingAddress={this.state.shippingAddress} schema={this.state.schema} form={this.state.form} onShippingAddressChange={this.onShippingAddressChange} />
                        <pre>{this.state.error}</pre>
                    </div>);
            } else {
                contents = <CircularProgress mode="indeterminate"/>;
            }
            actions.length = 0;
            actions.push(<RaisedButton label="Update" primary={true} onTouchTap={this.onUpdateShippingAddress} />);
            if(UserStore.getUser() && UserStore.getUser().shippingAddress) {
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
            actions.push(<RaisedButton label="Close" primary={true} onTouchTap={this.handleCartReset} />);
        }

        var cartHeaderIconClasses = classNames({
            'cart-badge': true
        });

        return (
            <span>
                <Badge badgeContent={this.state.cartItemsCount} onTouchTap={this.handleCartTouchTap} primary={true} style={{margin: 0, padding: 0}} badgeStyle={{width:'22px', height: '22px'}}>
                    <IconButton iconClassName="material-icons" onTouchTap={this.handleCartTouchTap} iconStyle={{margin: 0}}>shopping_cart</IconButton>
                </Badge>
                <Dialog
                    autoScrollBodyContent={true}
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
