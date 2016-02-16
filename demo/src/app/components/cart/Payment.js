/**
 * Created by steve on 04/09/15.
 */
var React = require('react');
var ReactPropTypes = React.PropTypes;
var PaymentStore = require('../../stores/PaymentStore');
var OrderStore = require('../../stores/OrderStore');
var PaymentActionCreators = require('../../actions/PaymentActionCreators');
var DropIn = require('braintree-react').DropIn;
var braintree = require('braintree-web');

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
        PaymentActionCreators.addTransaction(payload, OrderStore.getOrderId());
        //this.props.onPlaceOrder();
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
        //console.log('OrderStore is changed', OrderStore.getOrder());
        if(OrderStore.getOrder()) {
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
                <div>The following is connecting braintree sandbox. User credit card 4111111111111111 or paypal to test.</div>
                {body}
            </div>
        )
    }
});

module.exports = Payment;
