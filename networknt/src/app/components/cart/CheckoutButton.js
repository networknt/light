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
var DeliveryTax = require('./DeliveryTax');
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
import ConfigStore from '../../stores/ConfigStore';
import UserActionCreators from '../../actions/UserActionCreators';
import ConfigActionCreators from '../../actions/ConfigActionCreators';
import CircularProgress from 'material-ui/lib/circular-progress';
import BillingAddress from './BillingAddress';

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
const configId = 'default.delivery';

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
            //console.log('CheckoutButton.handleCartTouchTap state is set to open');
        } else {
            this.context.router.push('login');
        }

    },

    open: function() {
        //console.log('open is called');
        this.setState({cartOpen: true});
    },

    onDelivery: function() {
        // first get delivery method from host config, if the host config can be overwritten
        // check each item in the cart for delivery method and details.
        let screen;
        let title;
        switch(this.state.delivery.method) {
            case "SP":
                screen = 'shippingPickup';
                title = 'Shipping or Pickup';
                break;
            case "SO":
                screen = 'shippingAddress';
                title = 'Shipping Address';
                break;
            case "PO":
                screen = 'pickupAddress';
                title = 'Pickup Address';
                break;
            default:
                screen = 'billingAddress';
                title = 'Billing Address';
        }
        if(this.state.delivery.overwrite) {
            // TODO iterate all items in the cart to figure out the delivery method

        }
        this.setState({
            screen: screen,
            title: title
        });
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

    onBillingAddressChange: function(key, val) {
        utils.selectOrSet(key, this.state.billingAddress, val);
    },

    onConfirmBillingAddress: function() {
        var data = {};

        data.billingAddress = this.state.billingAddress;
        data.cartTotal = this.state.cartTotal;
        data.cartItems = this.state.cartItems;

        AddressActionCreators.confirmBillingAddress(data);
        this.setState({
            screen: 'deliveryTax',
            title: 'Delivery and Tax'
        })
    },

    onUpdateBillingAddress: function() {
        var data = {};
        data.billingAddress = this.state.billingAddress;
        data.cartTotal = this.state.cartTotal;
        data.cartItems = this.state.cartItems;
        // validate the address here.
        let validationResult = utils.validateBySchema(this.state.schema, this.state.billingAddress);
        if(!validationResult.valid) {
            console.log('error = ', validationResult.error.message);
            this.setState({error: validationResult.error.message});
        } else {
            AddressActionCreators.updateBillingAddress(data);
            this.setState({
                screen: 'deliveryTax',
                title: 'delivery and Tax'
            })
        }
    },

    onBillingAddressChange: function(key, val) {
        utils.selectOrSet(key, this.state.billingAddress, val);
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
        order.delivery = this.state.delivery;
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
            shippingAddress: UserStore.getUser().shippingAddress || {},
            billingAddress: UserStore.getUser().billingAddress || {}
        })
    },

    _onFormStoreChange: function() {
        this.setState({
            schema: FormStore.getForm(id).schema,
            form: FormStore.getForm(id).form
        });
    },

    _onConfigStoreChange: function() {
        console.log('_onConfigStoreChange, default.delivery', ConfigStore.getConfig(configId));
        this.setState({
            delivery: ConfigStore.getConfig(configId)
        });
    },

    componentDidMount: function() {
        CartStore.addChangeListener(this._onCartStoreChange);
        UserStore.addChangeListener(this._onUserStoreChange);
        FormStore.addChangeListener(this._onFormStoreChange);
        FormActionCreators.getForm(id);
        ConfigStore.addChangeListener(this._onConfigStoreChange);
        ConfigActionCreators.getConfig(configId);
        if(UserStore.getUser()) {
            this.setState({
                shippingAddress: UserStore.getUser().shippingAddress || {},
                billingAddress: UserStore.getUser().billingAddress || {}
            })
        } else {
            UserActionCreators.getUser(AuthStore.getUserId());
        }
    },

    componentWillUnmount: function() {
        CartStore.removeChangeListener(this._onCartStoreChange);
        UserStore.removeChangeListener(this._onUserStoreChange);
        FormStore.removeChangeListener(this._onFormStoreChange);
        ConfigStore.removeChangeListener(this._onConfigStoreChange);
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
        } else if (this.state.screen === 'billingAddress') {
            if(this.state.schema) {
                contents =
                    (<div>
                        <pre>{this.state.error}</pre>
                        <BillingAddress billingAddress={this.state.billingAddress} schema={this.state.schema} form={this.state.form} onBillingAddressChange={this.onBillingAddressChange} />
                        <pre>{this.state.error}</pre>
                    </div>);
            } else {
                contents = <CircularProgress mode="indeterminate"/>;
            }
            actions.length = 0;
            actions.push(<RaisedButton label="Update" primary={true} onTouchTap={this.onUpdateBillingAddress} />);
            if(UserStore.getUser() && UserStore.getUser().billingAddress) {
                actions.push(<RaisedButton label="Confirm" primary={true} onTouchTap={this.onConfirmBillingAddress}/>);
            }
            actions.push(<RaisedButton label="Cancel" secondary={true} onTouchTap={this.handleCartClose} />);
        } else if (this.state.screen === 'deliveryTax') {
            contents =  <DeliveryTax cartItems = {this.state.cartItems} totalPrice= {this.state.cartTotal} />;
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
