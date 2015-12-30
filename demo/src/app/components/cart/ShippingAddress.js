/**
 * Created by steve on 04/09/15.
 */
var React = require('react');
var ReactPropTypes = React.PropTypes;
var CartStore = require('../../stores/CartStore');
var AuthStore = require('../../stores/AuthStore');
var FormStore = require('../../stores/FormStore');
var CartActionCreators = require('../../actions/CartActionCreators');
var Cart = require('./Cart');
var FormActionCreators = require('../../actions/FormActionCreators');
import SchemaForm from 'react-schema-form/lib/SchemaForm';

const id = 'com.networknt.light.user.address';
var ShippingAddress = React.createClass({

    getInitialState: function() {
        return {
            schema: null,
            form: null
        };
    },

    componentWillMount: function() {
        FormStore.addChangeListener(this._onChange);
        FormActionCreators.getForm(id);
    },

    componentWillUnmount: function() {
        FormStore.removeChangeListener(this._onChange);
    },


    _onChange: function() {

        let schema = FormStore.getForm(id).schema;
        let form = FormStore.getForm(id).form;
        this.setState({
            schema: schema,
            form: form
        });
    },

    render: function() {
        if(this.state.schema) {
            //console.log('now trying to render the schema form');
            return <SchemaForm schema={this.state.schema} form={this.state.form} model={this.props.shippingAddress} onModelChange={this.props.onShippingAddressChange} />
        } else {
            return <div>Loading...</div>
        }
    }
});

module.exports = ShippingAddress;
