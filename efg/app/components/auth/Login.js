/**
 * Created by steve on 08/07/15.
 */
var React = require('react');
var AuthActionCreators = require('../../actions/AuthActionCreators.js');
var AuthStore = require('../../stores/AuthStore.js');
var ErrorNotice = require('../../components/common/ErrorNotice.js');

var Login = React.createClass({

    getInitialState: function() {
        return { errors: [] };
    },

    componentDidMount: function() {
        AuthStore.addChangeListener(this._onChange);
    },

    componentWillUnmount: function() {
        AuthStore.removeChangeListener(this._onChange);
    },

    _onChange: function() {
        this.setState({ errors: AuthStore.getErrors() });
    },

    _onSubmit: function(e) {
        e.preventDefault();
        this.setState({ errors: [] });
        var email = this.refs.email.getDOMNode().value;
        var password = this.refs.password.getDOMNode().value;
        AuthActionCreators.login(email, password);
    },

    render: function() {
        var errors = (this.state.errors.length > 0) ? <ErrorNotice errors={this.state.errors}/> : <div></div>;
        return (
            <div>
                {errors}
                <div className="row">
                    <div className="card card--login small-10 medium-6 large-4 columns small-centered">
                        <form onSubmit={this._onSubmit}>
                            <div className="card--login__field">
                                <label name="email">Email</label>
                                <input type="text" name="email" ref="email" />
                            </div>
                            <div className="card--login__field">
                                <label name="password">Password</label>
                                <input type="password" name="password" ref="password" />
                            </div>
                            <button type="submit" className="card--login__submit">Login</button>
                        </form>
                    </div>
                </div>
            </div>
        );
    }
});

module.exports = Login;

