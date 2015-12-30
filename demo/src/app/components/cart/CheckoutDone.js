/**
 * Created by steve on 12/04/15.
 */
var React = require('react');
var OrderStore = require('../../stores/OrderStore');

var CheckoutDone = React.createClass({

    render: function() {

        return (
            <div>
                Thanks for ordering from Edible Forest Garden. Your order number is {OrderStore.getOrderId()}.
            </div>
        )
    }
});

module.exports = CheckoutDone;
