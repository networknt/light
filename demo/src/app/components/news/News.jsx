var React = require('react');
var WebAPIUtils = require('../../utils/WebAPIUtils');
var NewsStore = require('../../stores/NewsStore');
var NewsActionCreators = require('../../actions/NewsActionCreators');
var classNames = require('classnames');
import Paper from 'material-ui/lib/paper';
import Markdown from '../Markdown';
import RaisedButton from 'material-ui/lib/raised-button';
require('rc-pagination/assets/index.css');
import Pagination from 'rc-pagination';
import Locale from 'rc-pagination/lib/locale/en_US';
require('rc-select/assets/index.css');
import Select from 'rc-select';


var News = React.createClass({
    displayName: 'News',

    getInitialState: function() {
        return {
            posts: [],
            ancestors: [],
            allowPost: false,
            total: 0,
            pageSize: 10,
            pageNo: 1
        };
    },

    componentWillMount: function() {
        NewsStore.addChangeListener(this._onNewsChange);
        NewsActionCreators.getNewsPost("#" + this.props.params.categoryRid, this.state.pageNo, this.state.pageSize);
    },

    componentWillUnmount: function() {
        NewsStore.removeChangeListener(this._onNewsChange);
    },

    _onNewsChange: function() {
        this.setState({
            ancestors: NewsStore.getAncestors(),
            allowPost: NewsStore.getAllowPost(),
            posts: NewsStore.getPosts(),
            total: NewsStore.getTotal()
        });
    },

    _routeToPost: function(index) {
        this.props.history.push('/news/post' + this.props.params.categoryRid + '/' + index);
    },

    _onAddPost: function () {
        console.log("_onAddPost is called");
        this.props.history.push('/news/postAdd/' + this.props.params.categoryRid);
    },

    _onPageNoChange: function (key) {
        console.log("_onPageNoChange is called", key);
        this.setState({
            pageNo: key
        });
        // use key instead of this.state.pageNo as setState is async.
        NewsActionCreators.getNewsPost("#" + this.props.params.categoryRid, key, this.state.pageSize);
    },

    _onPageSizeChange: function (current, pageSize) {
        console.log("_onPageSizeChange is called", current, pageSize);
        this.setState({
            pageSize: pageSize
        });
        NewsActionCreators.getNewsPost("#" + this.props.params.categoryRid, this.state.pageNo, pageSize);
    },

    render: function() {
        //console.log('total', this.state.total);
        let addButton = this.state.allowPost? <RaisedButton label="Add Post" primary={true} onTouchTap={this._onAddPost} /> : '';
        return (
            <div>
                <div className="blogHeader">
                    <h2>News{addButton}</h2>
                </div>
                <div className="blogRoot">
                    <div className="leftColumn">
                        {
                            this.state.posts.map(function(post, index) {
                                var boundClick = this._routeToPost.bind(this, index);
                                return (
                                    <span key={index}>
                                        <Paper className="blogPostPaper">
                                            <div className="blogPost">
                                                <h2 className="title"><a onClick={boundClick}>{post.title}</a></h2>
                                                <span>Submitted by {post.createUserId} on {post.createDate}</span>
                                                <Markdown text={post.summary} />
                                            </div>
                                        </Paper>
                                        <hr />
                                    </span>
                                );
                            }, this)
                        }
                        <Pagination locale={Locale} selectComponentClass={Select} showSizeChanger={true} pageSizeOptions={['10', '25', '50', '100']} onShowSizeChange={this._onPageSizeChange} onChange={this._onPageNoChange} current={this.state.pageNo} pageSize={this.state.pageSize} total={this.state.total}/>
                    </div>
                    <div className="rightColumn">
                        <div className="blogInfo">
                            <h1>News Information</h1>
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

module.exports = News;
