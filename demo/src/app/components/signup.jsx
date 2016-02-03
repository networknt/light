import React from 'react';
import FormStore from '../stores/FormStore';
import AuthStore from '../stores/AuthStore';
import FormActionCreators from '../actions/FormActionCreators';
import AuthActionCreators from '../actions/AuthActionCreators';
import SchemaForm from 'react-schema-form/lib/SchemaForm';
import RaisedButton from 'material-ui/lib/raised-button';
import utils from 'react-schema-form/lib/utils';

const id = 'com.networknt.light.user.signup';

let Signup = React.createClass({

    getInitialState: function() {
        return {
            schema: null,
            form: null,
            action: null,
            user: {}
        };
    },

    componentWillMount: function() {
        FormStore.addChangeListener(this._onFormChange);
        AuthStore.addChangeListener(this._onAuthChange);
        FormActionCreators.getForm(id);
    },

    componentWillUnmount: function() {
        FormStore.removeChangeListener(this._onFormChange);
        AuthStore.removeChangeListener(this._onAuthChange);
    },

    _onModelChange: function(key, val) {
        utils.selectOrSet(key, this.state.user, val);
    },

    _onFormChange: function() {
        let schema = FormStore.getForm(id) ? FormStore.getForm(id).schema : null;
        if(schema) {
            let form = FormStore.getForm(id).form;
            let action = FormStore.getForm(id).action;
            this.setState({
                schema: schema,
                form: form,
                action: action
            });
        }
    },

    _onAuthChange: function() {
        // route to user page once it is logged in.
        if(AuthStore.isLoggedIn()) {
            this.props.history.push('profile');
        }
    },

    render: function() {
        if(this.state.schema) {
            const buttons = this.state.action.map((item, idx) => (
                <RaisedButton key={idx} label={item.title} primary={true}
                              onTouchTap = {(e) => (AuthActionCreators.login(this.state.user.email, this.state.user.userId,
                               this.state.user.password, this.state.user.passwordConfirm, this.state.user.firstName,
                               this.state.user.lastName))} />
            ));

            return (
                <div>
                    <SchemaForm schema={this.state.schema} form={this.state.form} model={this.state.user} onModelChange={this._onModelChange} />
                    {buttons}
                </div>
            )
        } else {
            return <div>Loading...</div>
        }
    }
});

module.exports = Signup;
