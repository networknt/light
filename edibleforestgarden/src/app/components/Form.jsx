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

    getInitialState: function() {
        return {
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
        // TODO display error or success with toaster.
    },


    _onFormChange: function() {
        let schema = FormStore.getForm(this.props.params.formId) ? FormStore.getForm(this.props.params.formId).schema : null;
        if(schema) {
            let form = FormStore.getForm(this.props.params.formId).form;
            let action = FormStore.getForm(this.props.params.formId).action;
            let model = FormStore.getModel(this.props.params.formId);
            console.log('Form._onFormChange: model', model, this.state.model);
            this.setState({
                schema: schema,
                form: form,
                action: action,
                model: model || {}
            });
        }
    },

    _onModelChange: function(key, val) {
        console.log('_onModelChange', key, val, this.state.model);
        let newModel = _.cloneDeep(this.state.model);
        utils.selectOrSet(key, newModel, val);
        console.log('_onModelChange', newModel);
        this.setState({model: newModel});
    },

    _onTouchTap: function(action) {
        console.log('Form._onTouchTap', action, this.state.model);
        action.data = this.state.model;
        FormActionCreators.submitForm(action);
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
                    {actions}
                </div>
            )
        } else {
            return (<CircularProgress mode="indeterminate"/>);
        }
    }
});

module.exports = Form;
