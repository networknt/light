var React = require('react');
var {ListItem, Styles, Avatar} = require('material-ui');
var BlogActions = require('../../actions/BlogActions');

var Navigation = require('react-router').Navigation;

var BlogRow = React.createClass({
    mixins: [Navigation],

    render: function () {
        return (
            this._createItems(this.props.blog)
        );
    },

    _onTouchTap: function () {
        this.transitionTo('/blog/' + this.props.blog["@rid"].substring(1));
    },

    _createItems: function (blogs) {
        var children;
        if (blogs.out_Own) {
            children = blogs.out_Own.map(function (child) {
                return this._createItems(child);
            }.bind(this));
        }
        return (
            <ListItem
                leftAvatar={this._getLeftAvatar(blogs)}
                primaryText={this._getPrimaryText(blogs)}
                secondaryText={blogs.description}
                onTouchTap={this._onTouchTap}>{children}</ListItem>
        );
    },

    _getLeftAvatar: function(blogs) {
        var count = "0";
        if (blogs.out_HasPost != null && blogs.out_HasPost.length > 0) {
            count = blogs.out_HasPost.length.toString();
        }
        return (
            <div className="blogLeftAvatar">{count}</div>
        );
    },

    _getPrimaryText: function(blogs) {
        return (
            <p>
                {blogs.blogId}
            </p>
        );
    }

});

module.exports = BlogRow;