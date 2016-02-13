var React = require('react');
var WebAPIUtils = require('../utils/WebAPIUtils');
var TagStore = require('../stores/TagStore');
var TagActionCreators = require('../actions/TagActionCreators');
var classNames = require('classnames');
import Paper from 'material-ui/lib/paper';
import Markdown from './Markdown';
import RaisedButton from 'material-ui/lib/raised-button';
require('rc-pagination/assets/index.css');
import Pagination from 'rc-pagination';
import Locale from 'rc-pagination/lib/locale/en_US';
require('rc-select/assets/index.css');
import Select from 'rc-select';
import CommonUtils from '../utils/CommonUtils';
import Gravatar from './Gravatar';
import BlogSummary from './blog/BlogSummary';
import NewsSummary from './news/NewsSummary';
import ProductSummary from './catalog/ProductSummary';
import CartActionCreators from '../actions/CartActionCreators';


var Tag = React.createClass({
    displayName: 'Tag',

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: function() {
        return {
            entities: [],
            total: 0,
            pageSize: 10,
            pageNo: 1
        };
    },

    componentWillMount: function() {
        TagStore.addChangeListener(this._onTagChange);
        TagActionCreators.getTagEntity(this.props.params.tagId, this.state.pageNo, this.state.pageSize);
    },

    componentWillUnmount: function() {
        TagStore.removeChangeListener(this._onTagChange);
    },

    _onTagChange: function() {
        this.setState({
            entities: TagStore.getEntities(),
            total: TagStore.getTotal()
        });
    },

    _routeToEntity: function(parentType, categoryId, entityId) {
        console.log('Tag._routeToEntity', parentType, categoryId, entityId);
        this.context.router.push('/' + parentType.toLowerCase() + '/' + categoryId + '/' + entityId);
    },

    _onAddCart: function (product) {
        CartActionCreators.addToCart(product);
    },

    _onPageNoChange: function (key) {
        this.setState({
            pageNo: key
        });
        // use key instead of this.state.pageNo as setState is async.
        TagActionCreators.getTagEntity(this.props.params.tagId, key, this.state.pageSize);
    },

    _onPageSizeChange: function (current, pageSize) {
        //console.log("_onPageSizeChange is called", current, pageSize);
        this.setState({
            pageSize: pageSize
        });
        TagActionCreators.getTagEntity(this.props.params.tagId, this.state.pageNo, pageSize);
    },

    render: function() {
        let entities = this.state.entities.map((entity, index) => {
            let boundClick = this._routeToEntity.bind(this, entity.parentType, entity.parentId, entity.entityId);
            if(entity.parentType === 'Blog') {
                return (
                    <span key={index}>
                        <BlogSummary post={entity} onClick={boundClick} />
                    </span>
                );
            } else if(entity.parentType === 'News') {
                return (
                    <span key={index}>
                        <NewsSummary post={entity} onClick={boundClick} />
                    </span>
                );
            } else if(entity.parentType === 'Catalog') {
                let boundAddCart = this._onAddCart.bind(this, entity);
                return (
                    <span key={index}>
                        <ProductSummary index={index} product={entity} onClick={boundClick} onAddCart={boundAddCart} />
                    </span>
                );
            }
        });
        return (
            <div>
                <div className="blogHeader">
                    <h2>this.props.params.tagId</h2>
                </div>
                <div className="blogRoot">
                    <div className="leftColumn">
                        {entities}
                        <Pagination locale={Locale} selectComponentClass={Select} showSizeChanger={true} pageSizeOptions={['10', '25', '50', '100']} onShowSizeChange={this._onPageSizeChange} onChange={this._onPageNoChange} current={this.state.pageNo} pageSize={this.state.pageSize} total={this.state.total}/>
                    </div>
                </div>
            </div>
        );
    }
});

module.exports = Tag;
