/**
 * Created by steve on 18/08/15.
 */

var CategoryTree = React.createClass({
    getInitialState: function() {
        return {data: data};
    },
    componentWillMount: function() {
        this.setState({data: data});
    },
    onSelect: function (node) {
        if (this.state.selected && this.state.selected.isMounted()) {
            this.state.selected.setState({selected: false});
        }
        this.setState({selected: node});
        node.setState({selected: true});
        if (this.props.onCategorySelect) {
            this.props.onCategorySelect(node);
        }
    },
    render: function() {
        return (
            React.DOM.div( {className:"panel panel-default"},
                React.DOM.div( {className:"panel-body"},
                    React.DOM.ul( {className:"category-tree"},
                        TreeNode({key:this.state.data.id, data:this.state.data, onCategorySelect:this.onSelect})
                    )
                )
            )
        );
    }
});

module.exports = CategoryTree;