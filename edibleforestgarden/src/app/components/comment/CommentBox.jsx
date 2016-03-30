import React from 'react';
import CommonUtils from '../../utils/CommonUtils';
import CommentActionCreators from '../../actions/CommentActionCreators';
import CommentStore from '../../stores/CommentStore';
import CommentForm from './CommentForm';
import CommentThread from './CommentThread';
require('rc-pagination/assets/index.css');
import Pagination from 'rc-pagination';
import Locale from 'rc-pagination/lib/locale/en_US';
require('rc-select/assets/index.css');
import Select, {Option} from 'rc-select';
import CircularProgress from 'material-ui/lib/circular-progress';


var CommentBox = React.createClass({

    propTypes: {
        entityRid: React.PropTypes.string
    },

    getInitialState: function() {
        return {
            comments: [],
            total: 0,
            allowUpdate: false,
            pageSize: 10,
            pageNo: 1,
            sortedBy: 'rank',
            sortDir: 'desc'
        };
    },

    componentDidMount: function() {
        //console.log("CommentBox.componentDidMount", this.props.entityRid);
        CommentStore.addChangeListener(this._onCommentChange);
        CommentActionCreators.getCommentTree(this.props.entityRid, this.state.pageNo, this.state.pageSize, this.state.sortedBy, this.state.sortDir);
    },

    componentWillUnmount: function() {
        CommentStore.removeChangeListener(this._onCommentChange);
    },

    _onCommentChange: function() {
        console.log('onCommentChange', JSON.stringify(CommentStore.getComments()));
        this.setState({
            comments: CommentStore.getComments(),
            total: CommentStore.getTotal(),
            allowUpdate: CommentStore.getAllowUpdate()
        });
    },

    _onAddComment: function(parentRid, content) {
        let data = {
            entityRid: this.props.entityRid,
            '@rid': parentRid,
            content: content
        };
        CommentActionCreators.addComment(data);
    },

    _onDelComment: function(rid) {
        let data = {
            entityRid: this.props.entityRid,
            '@rid': rid
        };
        CommentActionCreators.delComment(data);
    },

    _onUpdComment: function(rid, content) {
        let data = {
            entityRid: this.props.entityRid,
            '@rid': rid,
            content: content
        };
        CommentActionCreators.updComment(data);
    },

    _onUpVote: function(rid) {
        let data = {
            entityRid: this.props.entityRid,
            '@rid': rid
        };
        CommentActionCreators.upComment(data);
    },

    _onDownVote: function(rid) {
        let data = {
            entityRid: this.props.entityRid,
            '@rid': rid
        };
        CommentActionCreators.downComment(data);
    },

    _onSpam: function(rid) {
        let data = {
            entityRid: this.props.entityRid,
            '@rid': rid
        };
        CommentActionCreators.spmComment(data);

    },

    _onPageNoChange: function (key) {
        this.setState({
            pageNo: key
        });
        // use key instead of this.state.pageNo as setState is async.
        CommentActionCreators.getCommentTree(this.props.entityRid, key, this.state.pageSize, this.state.sortedBy, this.state.sortDir);
    },

    _onPageSizeChange: function (current, pageSize) {
        this.setState({
            pageSize: pageSize
        });
        CommentActionCreators.getCommentTree(this.props.entityRid, this.state.pageNo, pageSize, this.state.sortedBy, this.state.sortDir);
    },

    _onSortSelect: function(value, option) {
        let sortedBy = 'rank';
        let sortDir = 'desc';

        switch(value) {
            case 'Rank':
                break;
            case 'Newest':
                sortedBy = 'createDate';
                sortDir = 'desc';
                break;
            case 'Oldest':
                sortedBy = 'createDate';
                sortDir = 'asc';
                break;
        }
        this.setState({
            sortedBy: sortedBy,
            sortDir: sortDir
        });

        CommentActionCreators.getCommentTree(this.props.entityRid, this.state.pageNo, this.state.pageSize, sortedBy, sortDir);

    },

    render: function() {
        var CommentThreadProps = {
            comments: this.state.comments,
            onAddComment: this._onAddComment,
            onDelComment: this._onDelComment,
            onUpdComment: this._onUpdComment,
            onUpVote: this._onUpVote,
            onDownVote: this._onDownVote,
            onSpam: this._onSpam,
            allowUpdate: this.state.allowUpdate
        };
        let commentThread = (<div></div>);
        if(this.state.comments && this.state.comments.length > 0) {
            commentThread = (
                <div className="comment-thread-app">
                    <CommentThread {...CommentThreadProps} />
                </div>
            )
        }
        return (
            <div>
                <h3>{this.state.total} comments</h3><Select defaultValue="Rank" onSelect={this._onSortSelect}><Option value="Rank">Rank</Option><Option value="Newest">Newest</Option><Option value="Oldest">Oldest</Option></Select>
                <CommentForm onAddComment={this._onAddComment} parentRid={this.props.entityRid} />
                {commentThread}
                <Pagination locale={Locale} selectComponentClass={Select} showSizeChanger={true} pageSizeOptions={['10', '25', '50', '100']} onShowSizeChange={this._onPageSizeChange} onChange={this._onPageNoChange} current={this.state.pageNo} pageSize={this.state.pageSize} total={this.state.total}/>
            </div>
        );
    }
});

module.exports = CommentBox;
