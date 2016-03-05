/**
 * Created by steve on 04/09/15.
 */
var React = require('react');
import SchemaForm from 'react-schema-form/lib/SchemaForm';


var Delivery = React.createClass({
    render: function() {
        return <SchemaForm schema={this.props.schema} form={this.props.form} model={this.props.shippingAddress} onModelChange={this.props.onShippingAddressChange} />
    }
});

module.exports = Delivery;
