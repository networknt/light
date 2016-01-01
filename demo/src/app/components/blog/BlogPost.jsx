'use strict';

var React = require('react');
var ReactPropTypes = React.PropTypes;
var ProductActionCreator = require('../../actions/ProductActionCreators');
var CartActionCreator = require('../../actions/CartActionCreators');

import AppConstants from '../../constants/AppConstants';
import Paper from 'material-ui/lib/paper';
import Markdown from '../Markdown';

var BlogPost = React.createClass({
    displayName: 'BlogPost',

    propTypes: {
        blogPost: React.PropTypes.object.isRequired
    },

    _routeToPost: function(postRid) {
        //this.transitionTo("/light-cms/blogs/" + this.props.params.blogRid + "/" + postRid.substring(1));
    },

    render: function() {
        let boundClick = this._routeToPost.bind(this, this.props.blogPost.rid);
        return (
            <span>
                <Paper className="blogPostPaper">
                    <div className="blogPost">
                        <h2 className="title"><a onClick={boundClick}>{this.props.blogPost.title}</a></h2>
                        <span>Submitted by {this.props.blogPost.createUserId} on {this.props.blogPost.createDate}</span>
                        <Markdown text={this.props.blogPost.summary} />
                    </div>
                </Paper>
                <hr />
            </span>
        )

    }
});

module.exports = BlogPost;
