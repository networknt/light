/**
 * Created by steve on 12/04/15.
 */
var React = require('react');
var Modal = require('react-bootstrap').Modal;
var Button = require('react-bootstrap').Button;
var OrderStore = require('../../stores/OrderStore');

var CheckoutDone = React.createClass({

    render: function() {

        return (
            <div>
                <Modal.Header>
                    <Modal.Title>{this.props.title}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    Thanks for ordering from Edible Forest Garden. Your order number is {OrderStore.getOrderId()}.
                </Modal.Body>
                <Modal.Footer>
                    <button type="button" className="btn btn-success" onClick={ this.props.close}>
                        Close<span className="glyphicon glyphicon-play"></span>
                    </button>
                </Modal.Footer>
            </div>
        )
    }
});

module.exports = CheckoutDone;
