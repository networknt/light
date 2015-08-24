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
var classNames = require('classnames');

var Catalog = React.createClass({

    getInitialState: function() {
        return {
            catalog: [],
            products: {},
            ancestors: [],
            allowUpdate: false
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
            catalog: ProductStore.getCatalog(),
            ancestors: ProductStore.getAncestors(),
            allowUpdate: ProductStore.getAllowUpdate(),
            products: ProductStore.getProducts()
        });
    },

    onSelect: function (node) {
        console.log('selected = ', node.props.catalog['@rid']);
        if (this.state.selected && this.state.selected.isMounted()) {
            this.state.selected.setState({selected: false});
        }
        this.setState({selected: node});
        node.setState({selected: true});
        if (this.props.onCategorySelect) {
            this.props.onCategorySelect(node);
        }
        ProductActionCreators.selectCatalog(node.props.catalog['@rid']);
    },
    render: function() {
        return (
            <div className="panel panel-default">
                <div className="panel-body">
                    <ul className="category-tree">
                        {this.state.catalog.map(function(category) {
                            return <TreeNode key={category.catalogId}
                                             catalog={category}
                                             onCategorySelect={this.onSelect}/>;
                        }.bind(this))}
                    </ul>
                </div>
                <ProductList products={this.state.products} ancestors={this.state.ancestors} allowUpdate={this.state.allowUpdate}/>
            </div>
        );
    }
});

var TreeNode = React.createClass({
    getInitialState: function() {
        return {out_Own: []};
    },
    onCategorySelect: function (ev) {
        if (this.props.onCategorySelect) {
            this.props.onCategorySelect(this);
        }
        ev.preventDefault();
        ev.stopPropagation();
    },
    onChildDisplayToggle: function (ev) {
        if (this.props.catalog.out_Own) {
            if (this.state.out_Own && this.state.out_Own.length) {
                this.setState({out_Own: null});
            } else {
                this.setState({out_Own: this.props.catalog.out_Own});
            }
        }
        ev.preventDefault();
        ev.stopPropagation();
    },
    render: function () {
        if (!this.state.out_Own) this.state.out_Own = [];
        var classes = classNames({
            'has-children': (this.props.catalog.out_Own ? true : false),
            'open': (this.state.out_Own.length ? true : false),
            'closed': (this.state.out_Own ? false : true),
            'selected': (this.state.selected ? true : false)
        });
        return (
            <li ref="node" className={classes}
                onClick={this.onChildDisplayToggle}>
                <a onClick={this.onCategorySelect}
                   data-id={this.props.catalog.catalogId}>
                    {this.props.catalog.catalogId}
                </a>
                <ul>
                    {this.state.out_Own.map(function(child) {
                        return <TreeNode key={child.catalogId}
                                         catalog={child}
                                         onCategorySelect={this.props.onCategorySelect}/>;
                    }.bind(this))}
                </ul>
            </li>
        );
    }
});

module.exports = Catalog;

