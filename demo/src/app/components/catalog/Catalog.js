/**
 * Created by steve on 11/08/15.
 */
var React = require('react');
var ProductList = require('./ProductList');
//var TreePath = require('./TreePath');
//var SearchForm = require('./SearchForm');
var WebAPIUtils = require('../../utils/WebAPIUtils');
var ProductStore = require('../../stores/ProductStore');
var ProductActionCreators = require('../../actions/ProductActionCreators');
var classNames = require('classnames');

var Catalog = React.createClass({
    displayName: 'Catalog',

    getInitialState: function() {
        return {
            products: [],
            ancestors: [],
            allowUpdate: false,
            categoryTreeOpen: true
        };
    },

    componentWillMount: function() {
        ProductStore.addChangeListener(this._onProductChange);
        ProductActionCreators.getCatalogTree();
    },

    componentWillUnmount: function() {
        ProductStore.removeChangeListener(this._onProductChange);
    },

    _onProductChange: function() {
        console.log('_onProductChange is called');
        this.setState({
            ancestors: ProductStore.getAncestors(),
            allowUpdate: ProductStore.getAllowUpdate(),
            products: ProductStore.getProducts(),
        });
    },

    render: function() {
        return (
            <div className="catalogView container">
                <div className="row">
                    <ProductList products={this.state.products} ancestors={this.state.ancestors} allowUpdate={this.state.allowUpdate}/>
                </div>
            </div>
        );
    }
});

module.exports = Catalog;
