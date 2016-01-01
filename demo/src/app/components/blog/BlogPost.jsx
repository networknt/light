'use strict';

var React = require('react');
var ReactPropTypes = React.PropTypes;

import AppConstants from '../../constants/AppConstants';
import Paper from 'material-ui/lib/paper';
import Markdown from '../Markdown';
import BlogActionCreators from '../../actions/BlogActionCreators';
import BlogStore from '../../stores/BlogStore';

var BlogPost = React.createClass({
    displayName: 'BlogPost',

    getInitialState: function() {
        return {
            post: {}
        };
    },

    componentDidMount: function() {
        //console.log('BlogPost blogPosts', BlogStore.getBlogPosts());
        //console.log('BlogPost index ', this.props.params.index);
        this.setState({
            post: BlogStore.getBlogPosts()[this.props.params.index]
        })
    },

    render: function() {
        return (
            <span>
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
