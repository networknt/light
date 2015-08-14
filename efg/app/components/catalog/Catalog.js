/**
 * Created by steve on 11/08/15.
 */
var React = require('react');
var ProductList = require('./ProductList');
var TreePath = require('./TreePath');
var SearchForm = require('./SearchForm');
var WebAPIUtils = require('../../utils/WebAPIUtils.js');
WebAPIUtils.getAllProducts();

var Catalog = React.createClass({
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
            </div>
        );
    }
});

module.exports = Catalog;

