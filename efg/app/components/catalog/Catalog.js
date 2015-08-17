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

var Catalog = React.createClass({

    getInitialState: function() {
        return {
            catalog: [],
            products: {},
            offset: 0
        };
    },

    componentDidMount: function() {
        ProductStore.addChangeListener(this._onChange);
        ProductActionCreators.loadCatalog();
        ProductActionCreators.loadProducts();
    },

    componentWillUnmount: function() {
        ProductStore.removeChangeListener(this._onChange);
    },

    _onChange: function() {
        this.setState({
            catalog: ProductStore.getCatalog(),
            products: ProductStore.getProducts(),
            offset: ProductStore.getOffset
        });
    },

    render: function() {
        return (
            <div>
                <div className="row">
                    <div className="col-md-12">
                        <div className="pull-left">
                            <TreePath/>
                        </div>
                        <div className="pull-left">
                            <SearchForm/>
                        </div>
                    </div>
                </div>
                <ProductList/>
                <ReactPaginate previousLabel={"previous"}
                               nextLabel={"next"}
                               breakLabel={<li className="break"><a href="">...</a></li>}
                               pageNum={this.state.pageNum}
                               marginPagesDisplayed={1}
                               pageRangeDisplayed={2}
                               clickCallback={this.handlePageClick}
                               containerClassName={"pagination"}
                               subContainerClassName={"pages pagination"}
                               activeClass={"active"} />

            </div>
        );
    }
});

module.exports = Catalog;

