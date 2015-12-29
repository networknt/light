/**
 * Created by steve on 12/04/15.
 */
var React = require('react');

var SearchForm = React.createClass({
    render: function() {
        return (
            <div>
                <input type="text" name="q" className="search_box" placeholder="Search" value=""/>
                <a href="#search"><span className="glyphicon glyphicon-search"></span></a>
            </div>
        );
    }
});

module.exports = SearchForm;
