import React from 'react';
import FormStore from '../stores/FormStore';
import FormActionCreators from '../actions/FormActionCreators';
import SchemaForm from 'react-schema-form/lib/SchemaForm';
import RaisedButton from 'material-ui/lib/raised-button';
import WebAPIUtils from '../utils/WebAPIUtils';

/*
class Form extends React.Component {
    render() {
        console.log('Form: props', this.props);
        return (
            <button onClick={() => {this.props.history.push('user')}}>clickme</button>
        );
    }
}

export default Form;
 */



let Form = React.createClass({

    displayName: 'Form',

    propTypes: {
        /**
         * This is the form id that used to load form from database
         */
        id: React.PropTypes.string,
        /**
         * Whoever is calling schema form should have model available. It is update most of the cases.
         */
        model: React.PropTypes.object,
        /**
         * This router object is used to route another page once form submission is done according to actions
         */
        history: React.PropTypes.object,
    },

    getInitialState: function() {
        return {
            schema: null,
            form: null,
            action: null
        };
    },

    componentWillMount: function() {
        FormStore.addChangeListener(this._onFormChange);
        FormActionCreators.getForm(this.props.id);
    },

    componentWillUnmount: function() {
        FormStore.removeChangeListener(this._onFormChange);
    },

    _onModelChange: function(key, val) {
        console.log('Form._onModelChange:', key);
        console.log('Form._onModelChange:', val);
        //this.setState({shippingAddress: utils.selectOrSet(key, this.state.shippingAddress, val)});
    },


    _onFormChange: function() {
        let schema = FormStore.getForm(this.props.id) ? FormStore.getForm(this.props.id).schema : null;
        if(schema) {
            let form = FormStore.getForm(this.props.id).form;
            let action = FormStore.getForm(this.props.id).action;
            console.log('schema = ', schema);
            console.log('form = ', form);
            console.log('action = ', action);
            this.setState({
                schema: schema,
                form: form,
                action: action
            });
        }
    },

    render: function() {
        console.log('Form: props', this.props);
        if(this.state.schema) {
            const buttons = this.state.action.map((item, idx) => (
               <RaisedButton label={item.title} primary={true} onTouchTap = {(e) => (WebAPIUtils.submitForm(this.state.action))} />
            ));

            return (
                <div>
                    <SchemaForm schema={this.state.schema} form={this.state.form} model={this.props.model} onModelChange={this._onModelChange} />
                    {buttons}
                </div>
            )
        } else {
            return <div>Loading...</div>
        }
    }
});

module.exports = Form;

/*
                   "action" : [{
                   "category" : "user",
                   "name" : "signInUser",
                   "readOnly": false,
                   "title" : "Sign In",
                   "success" : "/page/com-networknt-light-v-user-home"
               }],
*/