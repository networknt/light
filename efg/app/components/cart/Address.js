/**
 * Created by steve on 06/09/15.
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
var Cart = require('./Cart')

var Address = React.createClass({

    render: function() {
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
                    <Cart cartItems={ this.props.cartItems } totalPrice={ this.props.totalPrice } />
                </Modal.Body>
                <Modal.Footer>
                    {buyButton}
                    <Button className="btn btn-default" onClick={this.props.close}>
                        <span className="glyphicon glyphicon-shopping-cart"></span> Continue Shopping
                    </Button>
                </Modal.Footer>
            </div>
        )
    }
});

module.exports = Address;
