'use strict';

var React = require('react');
var ReactPropTypes = React.PropTypes;

import AppConstants from '../../constants/AppConstants';
import Paper from 'material-ui/lib/paper';
import Markdown from '../Markdown';
import CatalogActionCreators from '../../actions/CatalogActionCreators';
import ProductActionCreators from '../../actions/ProductActionCreators';
import CatalogStore from '../../stores/CatalogStore';
import ProductStore from '../../stores/ProductStore';
import EntityStore from '../../stores/EntityStore';
import CommonUtils from '../../utils/CommonUtils';
import RaisedButton from 'material-ui/lib/raised-button';

var CatalogProduct = React.createClass({
    displayName: 'CatalogProduct',

    getInitialState: function() {
        return {
            product: {},
            allowUpdate: false
        };
    },

    componentWillMount: function() {
        ProductStore.addChangeListener(this._onProductChange);
        EntityStore.addChangeListener(this._onEntityChange);
    },

    componentWillUnmount: function() {
        ProductStore.removeChangeListener(this._onProductChange);
        EntityStore.removeChangeListener(this._onEntityChange);
    },

    componentDidMount: function() {
        //console.log('CatalogProduct.componentDidMount', CatalogStore.getProducts(), this.props.params.index);
        let product = CommonUtils.findProduct(CatalogStore.getProducts(), this.props.params.entityId);
        if(!product) {
            ProductActionCreators.getProduct(this.props.params.entityId);
        }

        this.setState({
            product: product? product : {},
            allowUpdate: CatalogStore.getAllowUpdate()
        });
    },

    _onProductChange: function() {
        console.log('CatalogProduct._onProductChange', ProductStore.getResult(), ProductStore.getErrors());
        // TODO display toaster

    },

    _onEntityChange: function() {
        this.setState({
            product: EntityStore.getEntity()
        })
    },

    _onUpdateProduct: function () {
        //console.log("_onUpdateProduct is called");
        this.props.history.push('/catalog/productUpdate/' + this.props.params.entityId);
    },

    _onDeleteProduct: function () {
        CatalogActionCreators.delProduct(this.state.product.rid);
    },

    _routeToTag: function(tagId) {
        this.props.history.push('/tag/' + encodeURIComponent(tagId));
    },

    render: function() {
        console.log('CatalogProduct.render', this.state.product);
        let tags = '';
        if(this.state.product.tags) {
            tags = this.state.product.tags.map((tag, index) => {
                let boundTagClick = this._routeToTag.bind(this, tag);
                return <span key={index}><a href='#' onClick={boundTagClick}>{tag}</a>&nbsp;&nbsp;&nbsp;</span>
            });
        }
        let updateButton = this.state.allowUpdate? <RaisedButton label="Update Product" primary={true} onTouchTap={this._onUpdateProduct} /> : '';
        let deleteButton = this.state.allowUpdate? <RaisedButton label="Delete Product" primary={true} onTouchTap={this._onDeleteProduct} /> : '';
        let content = this.state.product.content? <Markdown text={this.state.product.content} /> : '';
        return (
            <span>
                {updateButton}
                {deleteButton}
                <Paper className="blogPostPaper">
                    <div className="blogPost">
                        <h2 className="title">{this.state.product.name}</h2>
                        <div>{tags}</div>
                        {content}
                    </div>
                </Paper>
                <hr />
            </span>
        )

    }
});

module.exports = CatalogProduct;
