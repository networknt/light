/**
 * Created by steve on 04/09/15.
 */
var React = require('react');
var ReactPropTypes = React.PropTypes;
var PaymentStore = require('../../stores/PaymentStore');
var OrderStore = require('../../stores/OrderStore');
import ConfigStore from '../../stores/ConfigStore';
var PaymentActionCreators = require('../../actions/PaymentActionCreators');
var DropIn = require('braintree-react').DropIn;
var braintree = require('braintree-web');

const configId = 'delivery';

var Payment = React.createClass({

    getInitialState: function(){
        return {
            loading: true
        };
    },

    onReady: function () {
        //console.log('Drop-In ready');
    },

    onError: function (err) {
        console.error(err);
    },

    onPaymentMethodReceived: function (payload) {
        //console.log(payload);
        let delivery = ConfigStore.getConfig(configId);
        console.log('Payment delivery', delivery);
        switch(delivery.method) {
            case "RS":
                console.log('RS subscription');
                PaymentActionCreators.addSubscription(payload, OrderStore.getOrderId());
                break;
            default:
                console.log('One time payment');
                PaymentActionCreators.addTransaction(payload, OrderStore.getOrderId());
        }
    },

    componentDidMount: function() {
        //console.log('Payment:componentDidMount is called');
        PaymentStore.addChangeListener(this._onChangePaymentStore);
        OrderStore.addChangeListener(this._onChangeOrderStore);
        PaymentActionCreators.getClientToken();
    },

    componentWillUnmount: function() {
        PaymentStore.removeChangeListener(this._onChangePaymentStore);
        OrderStore.addChangeListener(this._onChangeOrderStore);
    },

    _onChangePaymentStore: function() {
        this.setState({
            loading: false
        })
    },

    _onChangeOrderStore: function() {
        if(OrderStore.getOrderCompleted()) {
            this.props.onPlaceOrder();
        }
    },

    render: function() {
        var body;
        if(this.state.loading) {
            body = "";
        } else {
            let submitValue = 'Buy for $' + OrderStore.getTotal().toFixed(2);
            body = (
                <form action="/transactions" method="POST">
                    <DropIn
                        braintree={braintree}
                        clientToken={PaymentStore.getClientToken()}
                        onReady={this.onReady}
                        onError={this.onError}
                        onPaymentMethodReceived={this.onPaymentMethodReceived}
                        />
                    <input type="submit" value={submitValue} />
                </form>
            )
        }

        return (
            <div>
                <div>The following is embedded braintree sandbox. User credit card 4111111111111111 or paypal to test.</div>
                {body}
            </div>
        )
    }
});

module.exports = Payment;
