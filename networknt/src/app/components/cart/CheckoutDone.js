/**
 * Created by steve on 12/04/15.
 */
var React = require('react');
var OrderStore = require('../../stores/OrderStore');
import AppConstants from '../../constants/AppConstants';

var CheckoutDone = React.createClass({

    render: function() {

        return (
            <div>
                Thanks for ordering from {AppConstants.Site}. Your order number is {OrderStore.getOrderId()}.
            </div>
        )
    }
});

module.exports = CheckoutDone;
