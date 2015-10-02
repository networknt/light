/**
 * Created by steve on 08/07/15.
 */
var React = require('react');
var AuthActionCreators = require('../../actions/AuthActionCreators.js');
var AuthStore = require('../../stores/AuthStore.js');
var ErrorNotice = require('../../components/common/ErrorNotice.js');

import {Grid, Row, Col} from 'react-bootstrap';

var Login = React.createClass({

    getInitialState: function() {
        return { errors: []};
    },

    _onSubmit: function(e) {
        e.preventDefault();
        this.setState({ errors: [] });
        var userIdEmail = this.refs.userIdEmail.getDOMNode().value;
        var password = this.refs.password.getDOMNode().value;
        var rememberMe = this.refs.rememberMe.getDOMNode().checked;
        console.log('userIdEmail', userIdEmail);
        console.log('password', password);
        console.log('rememberMe', rememberMe);
        AuthActionCreators.login(userIdEmail, password, rememberMe);
    },

    render: function() {
        var errors = (this.state.errors.length > 0) ? <ErrorNotice errors={this.state.errors}/> : <div></div>;
        return (
            <div className="login">
                {errors}
                <Grid>
                    <form onSubmit={this._onSubmit} className="userLoginForm">
                        <Row className="userLoginFormRow">
                            <Col xs={4} md={4}>
                                <label name="userIdEmail">UserId or Email:</label>
                            </Col>
                            <Col xs={5} md={5}>
                                <input type="text" name="userIdEmail" ref="userIdEmail" />
                            </Col>
                        </Row>
                        <Row className="userLoginFormRow">
                            <Col xs={4} md={4}>
                                <label name="password">Password:</label>
                            </Col>
                            <Col xs={5} md={5}>
                                <input type="password" name="password" ref="password"/>
                            </Col>
                        </Row>
                        <Row className="userLoginFormRow">
                            <Col xs={4} md={4}>
                                <label name="rememberMe">Remember me:</label>
                            </Col>
                            <Col xs={5} md={5}>
                                <input type="checkbox" name="rememberMe" ref="rememberMe" className="rememberMe"/>
                            </Col>
                        </Row>
                        <Row className="userLoginFormRow">
                            <Col xs={2} xsOffset={4} md={2} mdOffset={4}>
                                <button type="submit" className="card--login__submit">Login</button>
                            </Col>
                        </Row>
                    </form>
                </Grid>
            </div>
        );
    }
});

module.exports = Login;

