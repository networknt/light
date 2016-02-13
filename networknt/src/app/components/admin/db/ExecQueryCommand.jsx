import React from 'react';
import CircularProgress from 'material-ui/lib/circular-progress';
import RaisedButton from 'material-ui/lib/raised-button';
import FormStore from '../../../stores/FormStore';
import DbStore from '../../../stores/DbStore';
import FormActionCreators from '../../../actions/FormActionCreators';
import DbActionCreators from '../../../actions/DbActionCreators';
import SchemaForm from 'react-schema-form/lib/SchemaForm';
import utils from 'react-schema-form/lib/utils';

const id = 'com.networknt.light.db.execQueryCommand';

var ExecQueryCommand = React.createClass({

    getInitialState: function() {
        return {
            schema: null,
            form: null,
            model: null,
            action: null,
            result: null
        };
    },

    componentWillMount: function() {
        FormStore.addChangeListener(this._onFormChange);
        DbStore.addChangeListener(this._onDbChange);
        FormActionCreators.getForm(id);
    },

    componentWillUnmount: function() {
        FormStore.removeChangeListener(this._onFormChange);
        DbStore.removeChangeListener(this._onDbChange);
    },


    _onFormChange: function() {
        let schema = FormStore.getForm(id).schema;
        let form = FormStore.getForm(id).form;
        let action = FormStore.getForm(id).action;

        this.setState({
            schema: schema,
            form: form,
            action: action,
            model: {}
        });
    },

    _onDbChange: function() {
        this.setState({
            result: JSON.stringify(DbStore.getQueryResult(), undefined, 2)
        });
    },

    _onModelChange: function(key, val) {
        utils.selectOrSet(key, this.state.model, val);
    },

    _onTouchTap: function(action) {
        //console.log('ExecQueryCommand._onTouchTap', action, this.state.model);
        action.data = this.state.model;
        DbActionCreators.execQueryCmd(action);
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
                    <SchemaForm schema={this.state.schema} form={this.state.form} onModelChange={this._onModelChange} />
                    {actions}
                    <div><textarea name="queryResult" value={this.state.result}></textarea></div>
                </div>
           )
        } else {
            return <CircularProgress mode="indeterminate"/>
        }
    }
});

module.exports = ExecQueryCommand;
