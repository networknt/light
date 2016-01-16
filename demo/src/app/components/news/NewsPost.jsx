'use strict';

var React = require('react');
var ReactPropTypes = React.PropTypes;

import AppConstants from '../../constants/AppConstants';
import Paper from 'material-ui/lib/paper';
import Markdown from '../Markdown';
import NewsActionCreators from '../../actions/NewsActionCreators';
import NewsStore from '../../stores/NewsStore';
import PostStore from '../../stores/PostStore';
import CommonUtils from '../../utils/CommonUtils';
import RaisedButton from 'material-ui/lib/raised-button';

var NewsPost = React.createClass({
    displayName: 'NewsPost',

    getInitialState: function() {
        return {
            post: {},
            allowPost: false
        };
    },

    componentWillMount: function() {
        PostStore.addChangeListener(this._onPostChange);
    },

    componentWillUnmount: function() {
        PostStore.removeChangeListener(this._onPostChange);
    },

    componentDidMount: function() {
        //console.log('NewsPost blogPosts', NewsStore.getNewsPosts());
        //console.log('NewsPost index ', this.props.params.index);
        this.setState({
            post: CommonUtils.findPost(NewsStore.getPosts(), this.props.params.postId),
            allowPost: NewsStore.getAllowPost()
        })
    },

    _onPostChange: function() {
        console.log('NewsPost._onPostChange', PostStore.getResult(), PostStore.getErrors());
        // TODO display toaster

    },

    _onUpdatePost: function () {
        console.log("_onUpdatePost is called");
        this.props.history.push('/news/postUpdate/' + this.props.params.postId);
    },

    _onDeletePost: function () {
        console.log("_onDeletePost is called");
        NewsActionCreators.delPost(this.state.post.rid);
    },

    render: function() {
        let updateButton = this.state.allowPost? <RaisedButton label="Update Post" primary={true} onTouchTap={this._onUpdatePost} /> : '';
        let deleteButton = this.state.allowPost? <RaisedButton label="Delete Post" primary={true} onTouchTap={this._onDeletePost} /> : '';
        return (
            <span>
                {updateButton}
                {deleteButton}
                <Paper className="blogPostPaper">
                    <div className="blogPost">
                        <h2 className="title">{this.state.post.title}</h2>
                        <span>Submitted by {this.state.post.createUserId} on {this.state.post.createDate}</span>
                        <Markdown text={this.state.post.content} />
                    </div>
                </Paper>
                <hr />
            </span>
        )

    }
});

module.exports = NewsPost;
