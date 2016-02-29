import React from 'react';
import FormStore from '../stores/FormStore';
import SubmissionStore from '../stores/SubmissionStore';
import FormActionCreators from '../actions/FormActionCreators';
import SchemaForm from 'react-schema-form/lib/SchemaForm';
import RcSelect from 'react-schema-form-rc-select/lib/RcSelect';
import RaisedButton from 'material-ui/lib/raised-button';
import CircularProgress from 'material-ui/lib/circular-progress';
import WebAPIUtils from '../utils/WebAPIUtils';
import utils from 'react-schema-form/lib/utils';
import _ from 'lodash';

let Form = React.createClass({

    displayName: 'Form',

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: function() {
        return {
            error: null,
            schema: null,
            form: null,
            action: null,
            model: {}
        };
    },

    componentWillMount: function() {
        FormStore.addChangeListener(this._onFormChange);
        SubmissionStore.addChangeListener(this._onSubmissionChange);
        FormActionCreators.getForm(this.props.params.formId);
    },

    componentWillUnmount: function() {
        FormStore.removeChangeListener(this._onFormChange);
        SubmissionStore.removeChangeListener(this._onSubmissionChange);
    },

    _onSubmissionChange: function() {
        // It is 200 status code. route to the right uri. The same message is sent to Main to display snackbar.
        console.log('Form._onSubmissionChange', this.state.success);
        this.context.router.push(this.state.success);
    },


    _onFormChange: function() {
        let schema = FormStore.getForm(this.props.params.formId) ? FormStore.getForm(this.props.params.formId).schema : null;
        if(schema) {
            let form = FormStore.getForm(this.props.params.formId).form;
            let action = FormStore.getForm(this.props.params.formId).action;
            let model = FormStore.getModel(this.props.params.formId);
            this.setState({
                schema: schema,
                form: form,
                action: action,
                model: model || {}
            });
        }
    },

    _onModelChange: function(key, val) {
        utils.selectOrSet(key, this.state.model, val);
    },

    _onTouchTap: function(action) {
        //console.log('Form._onTouchTap', action, this.state.model);
        let validationResult = utils.validateBySchema(this.state.schema, this.state.model);
        if(!validationResult.valid) {
            this.setState({error: validationResult.error.message});
        } else {
            if(action.category && action.name) {
                action.data = this.state.model;
                this.setState({success: action.success});
            } else {
                // no category or name defined in the action, this must be command form
                action = this.state.model;
            }
            FormActionCreators.submitForm(action);
        }
    },

    render: function() {
        if(this.state.schema) {
            var actions = [];
            {this.state.action.map((item, index) => {
                let boundTouchTap = this._onTouchTap.bind(this, item);
                actions.push(<RaisedButton key={index} label={item.title} primary={true} onTouchTap={boundTouchTap} />)
            })}
            return (
                <div>
                    <SchemaForm schema={this.state.schema} form={this.state.form} model={this.state.model} onModelChange={this._onModelChange} mapper= {{"rc-select": RcSelect}} />
                    <pre>{this.state.error}</pre>
                    {actions}
                </div>
            )
        } else {
            return (<CircularProgress mode="indeterminate"/>);
        }
    }
});

module.exports = Form;
