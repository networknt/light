/**
 * Created by steve on 08/07/15.
 */
var React = require('react');
var AuthActionCreators = require('../../actions/AuthActionCreators.js');
var AuthStore = require('../../stores/AuthStore.js');
var ErrorNotice = require('../../components/common/ErrorNotice.js');

import {Input, ButtonInput, FormControls} from 'react-bootstrap';

var Login = React.createClass({

    getInitialState: function() {
        return {errors: []};
    },

    _onSubmit: function(e) {
        e.preventDefault();
        this.setState({ errors: [] });
        console.log('userIdEmail', this);
        console.log('password', this.refs.password);
        console.log('rememberMe', this.refs.rememberMe);
        var userIdEmail = this.refs.userIdEmail.refs.input.getDOMNode().value;
        console.log('userIdEmail', userIdEmail);
        var password = this.refs.password.refs.input.getDOMNode().value;
        console.log('password', password);
        var rememberMe = this.refs.rememberMe.refs.input.getDOMNode().checked;
        console.log('rememberMe', rememberMe);
        AuthActionCreators.login(userIdEmail, password, rememberMe);
    },

    render: function() {
        var errors = (this.state.errors.length > 0) ? <ErrorNotice errors={this.state.errors}/> : <div></div>;
        return (
            <div className="login">
                <div className="loginErrors">
                    {errors}
                </div>
                <form onSubmit={this._onSubmit} className="loginForm form-horizontal">
                    <FormControls.Static className="col-xs-9 col-xs-offset-2 loginTitle" value="Login" />
                    <Input type="text" label="UserId or Email" id="userIdEmail" ref="userIdEmail" labelClassName="col-xs-2" wrapperClassName="col-xs-9"/>
                    <Input type="password" label="Password" id="password" ref="password" labelClassName="col-xs-2" wrapperClassName="col-xs-9"/>
                    <Input type="checkbox" label="Remember me" id="rememberMe" ref="rememberMe" className="rememberMe" wrapperClassName="col-xs-offset-2 col-xs-9"/>
                    <ButtonInput type="submit" bsStyle="primary" wrapperClassName="col-xs-offset-2 col-xs-9">Login</ButtonInput>
                </form>
            </div>
        );
    }
});

module.exports = Login;

