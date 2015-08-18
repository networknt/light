/**
 * Created by steve on 11/08/15.
 */
var React = require('react');
var ProductList = require('./ProductList');
var TreePath = require('./TreePath');
var SearchForm = require('./SearchForm');
var WebAPIUtils = require('../../utils/WebAPIUtils');
var ProductStore = require('../../stores/ProductStore');
var ProductActionCreators = require('../../actions/ProductActionCreators');
var ReactPaginate = require('react-paginate');

var Catalog = React.createClass({

    getInitialState: function() {
        return {
            catalogTree: [],
            selectedCatalog: null
        };
    },

    componentWillMount: function() {
        ProductStore.addChangeListener(this._onChange);
        ProductActionCreators.loadCatalog();
    },

    componentWillUnmount: function() {
        ProductStore.removeChangeListener(this._onChange);
    },

    _onChange: function() {
        this.setState({
            catalogTree: ProductStore.getCatalog(),
            selectedCatalog: ProductStore.getSelectedCatalog()
        });
    },

    render: function() {
        return (
            <div className="panel panel-default">
                <div className="panel-body">
                    <ul className="category-tree">
                    </ul>
                </div>
            </div>
        );
    }
});

module.exports = Catalog;

