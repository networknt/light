import React from 'react';
import classNames from 'classnames';

var CommentNode = React.createClass({

    getInitialState: function() {
        return {out_HasComment: []};
    },

    onCategorySelect: function (ev) {
        if (this.props.onCategorySelect) {
            this.props.onCategorySelect(this);
        }
        ev.preventDefault();
        ev.stopPropagation();
    },

    onChildDisplayToggle: function (ev) {
        if (this.props.category.out_HasComment) {
            if (this.state.out_HasComment && this.state.out_HasComment.length) {
                this.setState({out_HasComment: null});
            } else {
                this.setState({out_HasComment: this.props.category.out_HasComment});
            }
        }
        ev.preventDefault();
        ev.stopPropagation();
    },

    render: function () {
        if (!this.state.out_HasComment) this.state.out_HasComment = [];
        var classes = classNames({
            'has-children': (this.props.category.out_HasComment ? true : false),
            'open': (this.state.out_HasComment.length ? true : false),
            'closed': (this.state.out_HasComment ? false : true),
            'selected': (this.state.selected ? true : false)
        });
        return (
            <li ref="node" className={classes}
                onClick={this.onChildDisplayToggle}>
                <a onClick={this.onCategorySelect}
                   data-id={this.props.category.categoryId}>
                    {this.props.category.categoryId}
                </a>
                <ul>
                    {this.state.out_HasComment.map(function(child) {
                        return <TreeNode key={child.categoryId}
                                         category={child}
                                         onCategorySelect={this.props.onCategorySelect}/>;
                    }.bind(this))}
                </ul>
            </li>
        );
    }
});

module.exports = CommentNode;
