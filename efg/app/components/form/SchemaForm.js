/**
 * Created by steve on 06/09/15.
 */
/*
 <form name="ngform"  sf-model="modelData" sf-form="form" sf-schema="schema" ng-submit="submitForm(ngform,modelData)"></form>
 */

var React = require('react');
var should = require('chai').should();
var _ = require('lodash');
var ObjectPath = require('objectpath');
var tv4 = require('tv4');




var SchemaForm = React.createClass({
    render: function() {
        //getDefaultsTest();
        mergeTest();
        return (
            <div>SchemaForm</div>
        );
    }
});


module.exports = SchemaForm;


