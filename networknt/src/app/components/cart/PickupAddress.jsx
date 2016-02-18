/**
 * Created by steve on 04/09/15.
 *
 * This component will display a list of pick up addresses defined for the host.
 * The list can be overwrite if certain product can be picked only a certain location.
 *
 * TODO How to handle one cart that has two or more product located at two or more different locations?
 *
 */
import React from 'react';

var PickupAddress = React.createClass({
    render: function() {

        return <SchemaForm schema={this.props.schema} form={this.props.form} model={this.props.shippingAddress} onModelChange={this.props.onShippingAddressChange} />
    }
});

module.exports = PickupAddress;
