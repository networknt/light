/**
 * Created by steve on 04/09/15.
 */
var React = require('react');
var ReactPropTypes = React.PropTypes;
var Modal = require('react-bootstrap').Modal;
var Button = require('react-bootstrap').Button;
var PaymentStore = require('../../stores/PaymentStore');
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
        console.log('Drop-In ready');
    },

    onError: function (err) {
        console.error(err);
    },

    onPaymentMethodReceived: function (payload) {
        console.log(payload);

        // Now that you have a nonce, send it to your
        // server to create a payment method or a transaction
    },

    componentDidMount: function() {
        console.log('Payment:componentDidMount is called');
        PaymentStore.addChangeListener(this._onChange);
        PaymentActionCreators.getClientToken();
    },

    componentWillUnmount: function() {
        PaymentStore.removeChangeListener(this._onChange);
    },

    _onChange: function() {
        this.setState({
            loading: false
        })
    },

    render: function() {

        var body;
        if(this.state.loading) {
            body = "";
        } else {
            body = (
                <form action="/transactions" method="POST">
                    <DropIn
                        braintree={braintree}
                        clientToken={PaymentStore.getClientToken()}
                        onReady={this.onReady}
                        onError={this.onError}
                        onPaymentMethodReceived={this.onPaymentMethodReceived}
                        />
                    <input type="submit" value="Buy for $14" />
                </form>
            )
        }

        var buyButton
        if (this.props.cartItems.length > 0) {
            buyButton = (
                <Button className="btn btn-success" onClick={this.props.onShipping}>
                    Buy now <span className="glyphicon glyphicon-play"></span>
                </Button>
            )
        }

        return (
            <div>
                <Modal.Header>
                    <Modal.Title>{this.props.title}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {body}
                </Modal.Body>
                <Modal.Footer>
                </Modal.Footer>
            </div>
        )
    }
});

module.exports = Payment;
