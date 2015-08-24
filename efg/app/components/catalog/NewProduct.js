/**
 * Created by steve on 21/08/15.
 */
var React = require('react');
var ProductForm = require('./ProductForm');

class NewProduct extends React.Component {

    constructor(props) {
        super(props);
        this.handleSuccess = this.handleSuccess.bind(this);
    }

    handleSuccess() {
        this.context.router.transitionTo('catalog');
    }

    render() {
        return (
            <div>
                <ProductForm
                    resource={{}}
                    onSuccess={this.handleSuccess} />
            </div>
        )
    }

}

NewProduct.contextTypes = {
    router: React.PropTypes.func
};

module.exports = NewProduct;