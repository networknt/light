/**
 * Created by steve on 12/04/15.
 */
var React = require('react');

var TreePath = React.createClass({
    render: function() {
        return (
            <div>
                <a href="#">First Catagory</a>&nbsp;&gt;&nbsp;<a href="#">Second Category</a>&nbsp;&gt;&nbsp;<a href="#">Third Category</a>
            </div>
        );
    }
});

module.exports = TreePath;
