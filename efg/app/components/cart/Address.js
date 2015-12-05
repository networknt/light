/**
 * Created by steve on 06/09/15.
 *
 * Address info in readonly format if it exists in db or
 * address form will be displayed to collect the address.
 *
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
var Form = require('react-schema-form');


var Address = React.createClass({

    displayName: 'Address',

    getInitialState: function() {

        return {
            catalog: [],
            products: [],
            ancestors: [],
            allowUpdate: false,
            categoryTreeOpen: true
        };
    },

    componentWillMount: function() {
        ProductStore.addChangeListener(this._onChange);
        ProductActionCreators.loadCatalog();
    },

    componentWillUnmount: function() {
        ProductStore.removeChangeListener(this._onChange);
    },

    _onChange: function() {
        console.log('_onChange is called');
        if(ProductStore.getSelected() && ProductStore.getSelected().isMounted()) {
            this.state.selected.setState({selected: false});
        }
        if (ProductStore.getOnCategorySelect()) {
            ProductStore.getOnCategorySelect()(node);
        }
        this.setState({
            catalog: ProductStore.getCatalog(),
            ancestors: ProductStore.getAncestors(),
            allowUpdate: ProductStore.getAllowUpdate(),
            products: ProductStore.getProducts(),
            selected: ProductStore.getNode()
        });
        if(ProductStore.getNode()) {
            ProductStore.getNode().setState({selected: true});
        }
    },

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
