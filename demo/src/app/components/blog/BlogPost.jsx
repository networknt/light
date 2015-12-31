'use strict';

var React = require('react');
var ReactPropTypes = React.PropTypes;
var ProductActionCreator = require('../../actions/ProductActionCreators');
var CartActionCreator = require('../../actions/CartActionCreators');

import AppConstants from '../../constants/AppConstants';
import Paper from 'material-ui/lib/paper';

var BlogPost = React.createClass({
    displayName: 'BlogPost',

    propTypes: {
        blogPost: React.PropTypes.object.isRequired
    },

    _routeToPost: function(postRid) {
        //this.transitionTo("/light-cms/blogs/" + this.props.params.blogRid + "/" + postRid.substring(1));
    },

    render: function() {
        let date = new Date(this.props.blogPost.createDate);
        let boundClick = this._routeToPost.bind(this, this.props.blogPost.rid);
        return (
            <span>
                <Paper className="blogPostPaper">
                    <div className="blogPost">
                        <h2>
                            <strong className="strongDate">{AppConstants.monthNames[date.getMonth()]} {date.getDay()},</strong> <span className="year">{date.getFullYear()}</span>
                        </h2>
                        <h1 className="title"><a onClick={boundClick}>{this.props.blogPost.title}</a></h1>
                        <p className="content">
                            {this.props.blogPost.content}
                        </p>
                    </div>
                </Paper>
                <hr />
            </span>
        )

    }
});

module.exports = BlogPost;
