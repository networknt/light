'use strict';

var React = require('react');
var ReactPropTypes = React.PropTypes;

import AppConstants from '../../constants/AppConstants';
import Paper from 'material-ui/lib/paper';
import Markdown from '../Markdown';
import BlogActionCreators from '../../actions/BlogActionCreators';
import BlogStore from '../../stores/BlogStore';
import PostStore from '../../stores/PostStore';
import RaisedButton from 'material-ui/lib/raised-button';

var BlogPost = React.createClass({
    displayName: 'BlogPost',

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
        //console.log('BlogPost blogPosts', BlogStore.getBlogPosts());
        //console.log('BlogPost index ', this.props.params.index);
        this.setState({
            post: BlogStore.getBlogPosts()[this.props.params.index],
            allowPost: BlogStore.getAllowPost()
        })
    },

    _onPostChange: function() {
        console.log('BlogPostUpdate._onPostChange', PostStore.getResult(), PostStore.getErrors());
        // TODO display toaster

    },

    _onUpdatePost: function () {
        console.log("_onUpdatePost is called");
        this.props.history.push('/blogPostUpdate/' + this.props.params.index);
    },

    _onDeletePost: function () {
        console.log("_onDeletePost is called");
        BlogActionCreators.delPost(this.state.post.rid);
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

module.exports = BlogPost;
