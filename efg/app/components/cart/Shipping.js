/**
 * Created by steve on 04/09/15.
 */
var React = require('react');
var ReactPropTypes = React.PropTypes;
var Modal = require('react-bootstrap').Modal;
var ModalHeader = require('react-bootstrap').ModalHeader;
var ModalBody = require('react-bootstrap').ModalBody;
var ModalFooter = require('react-bootstrap').ModalFooter;
var Button = require('react-bootstrap').Button;
var CartStore = require('../../stores/CartStore');
var AuthStore = require('../../stores/AuthStore');
var CartActionCreators = require('../../actions/CartActionCreators');
var Cart = require('./Cart');
var Address = require('./Address');
var Shipping = React.createClass({


    render: function() {
        //console.log('Shipping total price = ', this.props.totalPrice);
        var button;
        if(AuthStore.getShippingAddress()) {
            button = (
                <span>
                <Button className="btn btn-success" onClick={this.props.onUpdateShippingAddress}>
                    Update<span className="glyphicon glyphicon-play"></span>
                </Button>
                <Button className="btn btn-success" onClick={this.props.onConfirmShippingAddress}>
                    Confirm<span className="glyphicon glyphicon-play"></span>
                </Button>
                </span>

            );
        } else {
            button = (
                <Button className="btn btn-success" onClick={this.props.onUpdateShippingAddress}>
                    Update<span className="glyphicon glyphicon-play"></span>
                </Button>
            )
        }

        return (
            <div>
                <Modal.Header>
                    <Modal.Title>{this.props.title}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Address model={this.props.shippingAddress} onModelChange={this.props.onShippingAddressChange} />
                </Modal.Body>
                <Modal.Footer>
                    {button}
                    <Button className="btn btn-default" onClick={this.props.close}>
                        <span className="glyphicon glyphicon-shopping-cart"></span> Continue Shopping
                    </Button>
                </Modal.Footer>
            </div>
        )
    }
});

module.exports = Shipping;
