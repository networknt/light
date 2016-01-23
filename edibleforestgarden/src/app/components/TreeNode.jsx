var React = require('react');
var classNames = require('classnames');

var TreeNode = React.createClass({
    getInitialState: function() {
        return {out_Own: []};
    },
    onCategorySelect: function (ev) {
        if (this.props.onCategorySelect) {
            this.props.onCategorySelect(this);
        }
        ev.preventDefault();
        ev.stopPropagation();
    },
    onChildDisplayToggle: function (ev) {
        if (this.props.category.out_Own) {
            if (this.state.out_Own && this.state.out_Own.length) {
                this.setState({out_Own: null});
            } else {
                this.setState({out_Own: this.props.category.out_Own});
            }
        }
        ev.preventDefault();
        ev.stopPropagation();
    },
    render: function () {
        if (!this.state.out_Own) this.state.out_Own = [];
        var classes = classNames({
            'has-children': (this.props.category.out_Own ? true : false),
            'open': (this.state.out_Own.length ? true : false),
            'closed': (this.state.out_Own ? false : true),
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
                    {this.state.out_Own.map(function(child) {
                        return <TreeNode key={child.categoryId}
                                         category={child}
                                         onCategorySelect={this.props.onCategorySelect}/>;
                    }.bind(this))}
                </ul>
            </li>
        );
    }
});

module.exports = TreeNode;
