/**
 * Created by steve on 11/08/15.
 */

var React = require('react');
var WebAPIUtils = require('../../utils/WebAPIUtils');
var CatalogStore = require('../../stores/CatalogStore');
var CatalogActionCreators = require('../../actions/CatalogActionCreators');
var CartActionCreators = require('../../actions/CartActionCreators');

var classNames = require('classnames');
var _ = require('lodash');
import RaisedButton from 'material-ui/lib/raised-button';
require('rc-pagination/assets/index.css');
import Pagination from 'rc-pagination';
import Locale from 'rc-pagination/lib/locale/en_US';
require('rc-select/assets/index.css');
import Select from 'rc-select';
import CatalogCategoryStore from '../../stores/CatalogCategoryStore';
import CommonUtils from '../../utils/CommonUtils';
import ProductSummary from './ProductSummary';


var Catalog = React.createClass({
    displayName: 'Catalog',

    getInitialState: function() {
        return {
            products: [],
            ancestors: [],
            allowUpdate: false,
            total: 0,
            pageSize: 10,
            pageNo: 1
        };
    },

    componentWillMount: function() {
        CatalogStore.addChangeListener(this._onCatalogChange);
        CatalogCategoryStore.addChangeListener(this._catalogCategoryChange);

        if(CatalogCategoryStore.getCategory().length === 0) {
            CatalogActionCreators.getCatalogTree();
        } else {
            // lookup categoryRid from categoryId in params.
            let category = CommonUtils.findCategory(CatalogCategoryStore.getCategory(), this.props.params.categoryId);
            CatalogActionCreators.getCatalogProduct(category['@rid'], this.state.pageNo, this.state.pageSize);
        }
    },

    componentWillUnmount: function() {
        CatalogStore.removeChangeListener(this._onCatalogChange);
        CatalogCategoryStore.removeChangeListener(this._catalogCategoryChange);
    },

    _onCatalogChange: function() {
        this.setState({
            ancestors: CatalogStore.getAncestors(),
            allowUpdate: CatalogStore.getAllowUpdate(),
            products: CatalogStore.getProducts(),
            total: CatalogStore.getTotal()
        });
    },

    _catalogCategoryChange: function() {
        // The Main doesn't care about the post loading anymore. the loading action always starts here.
        let rid = CatalogCategoryStore.getCategory()[0]['@rid'];
        if(this.props.params.categoryId) {
            let category = CommonUtils.findCategory(CatalogCategoryStore.getCategory(), this.props.params.categoryId);
            rid = category['@rid'];
        }
        this.setState({rid: rid});
        CatalogActionCreators.getCatalogProduct(rid, this.state.pageNo, this.state.pageSize);
    },

    _routeToProduct: function(entityId) {
        this.props.history.push('/catalog/' + this.props.params.categoryId + '/' + entityId);
    },

    _onAddCart: function(index) {
        //console.log('_onAddCart', index);
        var product = this.state.products[index];
        CartActionCreators.addToCart(product);
    },

    _onAddProduct: function () {
        this.props.history.push('/catalog/productAdd/' + this.props.params.categoryId);
    },

    _onPageNoChange: function (key) {
        this.setState({
            pageNo: key
        });
        // use key instead of this.state.pageNo as setState is async.
        CatalogActionCreators.getCatalogProduct(this.state.rid, key, this.state.pageSize);
    },

    _onPageSizeChange: function (current, pageSize) {
        this.setState({
            pageSize: pageSize
        });
        CatalogActionCreators.getCatalogProduct(this.state.rid, this.state.pageNo, pageSize);
    },

    render: function() {
        //console.log('total', this.state.total);
        let addButton = this.state.allowUpdate? <RaisedButton label="Add Product" primary={true} onTouchTap={this._onAddProduct} /> : '';

        return (
            <div>

                <div className="header">
                    <h2 className="headerContent">Catalog{addButton}</h2>
                </div>
                <div className="columnRoot">
                    <div className="leftColumn">
                        {
                            this.state.products.map(function(product, index) {
                                var boundClick = this._routeToProduct.bind(this, product.entityId);
                                var boundAddCart = this._onAddCart.bind(this, index);
                                return (
                                    <span key={index}>
                                        <ProductSummary index={index} product={product} onClick={boundClick} onAddCart={boundAddCart} />
                                    </span>
                                );
                            }, this)
                        }
                        <Pagination locale={Locale} selectComponentClass={Select} showSizeChanger={true} pageSizeOptions={['10', '25', '50', '100']} onShowSizeChange={this._onPageSizeChange} onChange={this._onPageNoChange} current={this.state.pageNo} pageSize={this.state.pageSize} total={this.state.total}/>
                    </div>
                    <div className="rightColumn">
                        <div className="blogInfo">
                            <h1>Blog Information</h1>
                            <p>In this section, you will see some information and references pertaining to the opened blog.</p>
                            <p>Also, having the screen width be less then 64em will hide it, leaving reading room for mobile users only concerned with reading post content on the go.</p>
                            <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Ad adipisci alias cum, cumque cupiditate ea eum itaque, minus molestias necessitatibus nihil pariatur perspiciatis quam quas quod rem repellat, sint voluptate.</p>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
});

module.exports = Catalog;
