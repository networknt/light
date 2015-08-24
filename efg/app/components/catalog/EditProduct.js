/**
 * Created by steve on 21/08/15.
 */
var React = require('react');

class EditProduct extends React.Component {

    constructor(props) {
        super(props);
        this.state = { loaded: false };
        this.handleSuccess = this.handleSuccess.bind(this);
    }

    componentDidMount() {
        ProductStore.getResource(this.props.params.rid).then(data => {
            this.setState({
                loaded: true,
                product:   data.product
            });
        });
    }

    handleSuccess() {
        this.context.router.transitionTo('catalog');
    }

    render() {
        if (!this.state.loaded) { return <div>Loading...</div>; }

        return (
            <div>
                <ProductForm
                    resource={this.state.product}
                    onSuccess={this.handleSuccess}
                    onDestroy={this.handleSuccess} />
            </div>
        )
    }

}

EditProduct.contextTypes = {
    router: React.PropTypes.func
};

module.exports = EditProduct;