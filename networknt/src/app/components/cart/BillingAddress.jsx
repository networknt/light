/**
 * Created by steve on 04/09/15.
 *
 * For DL/EM/OS/RS delivery methods, the billing address needs to be collected.
 *
 */
import React from 'react';
import SchemaForm from 'react-schema-form/lib/SchemaForm';

var BillingAddress = React.createClass({
    render: function() {
        return <SchemaForm schema={this.props.schema} form={this.props.form} model={this.props.billingAddress} onModelChange={this.props.onBillingAddressChange} />
    }
});

module.exports = BillingAddress;
