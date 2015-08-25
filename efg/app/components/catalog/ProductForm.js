/**
 * Created by steve on 22/08/15.
 */
var React = require('react');
var BaseForm = require('../form/BaseForm');
var ProductStore = require('../../stores/ProductStore');

class ProductForm extends BaseForm {

    constructor(props) {
        super(props);
        this.store = ProductStore;
    }

    componentDidMount() {
        this.setState({
            loaded: true,
            selectedCatalog: this.store.getSelectedCatalog()
        });
    }

    render() {
        if (!this.state.loaded) { return <div>Loading...</div>; }

        let destroyButton;
        if (this.props.resource.id) {
            destroyButton = <button className="btn btn-danger pull-right" onClick={this.handleDestroy}>Delete</button>;
        }

        return (
            <form onSubmit={this.handleSubmit}>
                <SelectInput {...this.getInputProps('category_id')} options={this.state.categories} />
                <TextInput {...this.getInputProps('title')} autoFocus={true} />
                <TextareaInput {...this.getInputProps('body')} />
                <Link to='posts' className="btn btn-default">Cancel</Link>
                <button type="submit" className="btn btn-primary pull-right">Save</button>
                {destroyButton}
            </form>
        )
    }

}

module.exports = ProductForm;