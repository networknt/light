/**
 * Created by steve on 18/08/15.
 */

var TreeNode = React.createClass({
    getInitialState: function() {
        return {children: []};
    },
    onCategorySelect: function (ev) {
        if (this.props.onCategorySelect) {
            this.props.onCategorySelect(this);
        }
        ev.preventDefault();
        ev.stopPropagation();
    },
    onChildDisplayToggle: function (ev) {
        if (this.props.data.children) {
            if (this.state.children && this.state.children.length) {
                this.setState({children: null});
            } else {
                this.setState({children: this.props.data.children});
            }
        }
        ev.preventDefault();
        ev.stopPropagation();
    },
    render: function () {
        if (!this.state.children) this.state.children = [];
        var classes = React.addons.classSet({
            'has-children': (this.props.data.children ? true : false),
            'open': (this.state.children.length ? true : false),
            'closed': (this.state.children ? false : true),
            'selected': (this.state.selected ? true : false)
        });
        return (
            React.DOM.li( {ref:"node", className:classes, onClick:this.onChildDisplayToggle},
                React.DOM.a( {onClick:this.onCategorySelect, 'data-id':this.props.data.id}, this.props.data.name),
                React.DOM.ul(null,
                    this.state.children.map(function(child) {
                        return TreeNode( {key:child.id, data:child, onCategorySelect:this.props.onCategorySelect});
                    }.bind(this))
                )
            )
        );
    }
});

module.exports = TreeNode;