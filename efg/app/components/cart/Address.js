/**
 * Created by steve on 06/09/15.
 *
 * Address info in readonly format if it exists in db or
 * address form will be displayed to collect the address.
 *
 */
var React = require('react');
var FormStore = require('../../stores/FormStore');
var AuthStore = require('../../stores/AuthStore');
var FormActionCreators = require('../../actions/FormActionCreators');
var { SchemaForm } = require('react-schema-form');
require('react-select/less/default.less');


var Address = React.createClass({

    displayName: 'Address',

    getInitialState: function() {

        return {
            schema: null,
            form: null,
            model: null
        };
    },

    componentWillMount: function() {
        FormStore.addChangeListener(this._onChange);
        FormActionCreators.getForm('com.networknt.light.user.address');
    },

    componentWillUnmount: function() {
        FormStore.removeChangeListener(this._onChange);
    },

    onModelChange: function(key, val) {
        //console.log('ExamplePage.onModelChange:', key);
        //console.log('ExamplePage.onModelChange:', val);
        //this.setState({model: utils.selectOrSet(key, this.state.model, val)});
    },

    _onChange: function() {
        console.log('Address._onChange is called', this.props.addressType);
        let model = {};
        if('shippingAddress' === this.props.addressType && AuthStore.getShippingAddress()) {
            model = AuthStore.getShippingAddress();
        }
        if('paymentAddress' === this.props.addressType && AuthStore.getPaymentAddress()) {
            model = AuthStore.getPaymentAddress();
        }
        console.log('model = ', model);
        let schema = FormStore.getForm('com.networknt.light.user.address').schema;
        let form = FormStore.getForm('com.networknt.light.user.address').form;
        console.log('schema = ', schema);
        console.log('form = ', form);

        this.setState({
            schema: schema,
            form: form,
            model: model
        });
    },

    render: function() {
        if(this.state.schema) {
            console.log('now trying to render the schema form');
            return <SchemaForm schema={this.state.schema} form={this.state.form} model={this.state.model} onModelChange={this.onModelChange} />
        } else {
            return <div>Loading...</div>
        }
    }
});

/*


 */
module.exports = Address;
