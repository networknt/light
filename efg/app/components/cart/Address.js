/**
 * Created by steve on 06/09/15.
 *
 * Address info in readonly format if it exists in db or
 * address form will be displayed to collect the address.
 *
 */
var React = require('react');
var FormStore = require('../../stores/FormStore');
var FormActionCreators = require('../../actions/FormActionCreators');
var { SchemaForm } = require('react-schema-form');
require('react-select/less/default.less');


var Address = React.createClass({

    displayName: 'Address',

    getInitialState: function() {
        return {
            schema: null,
            form: null
        };
    },

    componentWillMount: function() {
        FormStore.addChangeListener(this._onChange);
        FormActionCreators.getForm('com.networknt.light.user.address');
    },

    componentWillUnmount: function() {
        FormStore.removeChangeListener(this._onChange);
    },


    _onChange: function() {

        let schema = FormStore.getForm('com.networknt.light.user.address').schema;
        let form = FormStore.getForm('com.networknt.light.user.address').form;
        //console.log('schema = ', schema);
        //console.log('form = ', form);

        this.setState({
            schema: schema,
            form: form
        });
    },

    render: function() {
        if(this.state.schema) {
            //console.log('now trying to render the schema form');
            return <SchemaForm schema={this.state.schema} form={this.state.form} model={this.props.model} onModelChange={this.props.onModelChange} />
        } else {
            return <div>Loading...</div>
        }
    }
});

/*


 */
module.exports = Address;
