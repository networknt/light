import React from 'react';
import CircularProgress from 'material-ui/lib/circular-progress';
import RaisedButton from 'material-ui/lib/raised-button';
import FormStore from '../../stores/FormStore';
import PostStore from '../../stores/PostStore';
import NewsStore from '../../stores/NewsStore';
import FormActionCreators from '../../actions/FormActionCreators';
import NewsActionCreators from '../../actions/NewsActionCreators';
import SchemaForm from 'react-schema-form/lib/SchemaForm';
import utils from 'react-schema-form/lib/utils';

const id = 'com.networknt.light.blog.post.update';

var NewsPostUpdate = React.createClass({

    getInitialState: function() {
        return {
            schema: null,
            form: null,
            model: null,
            action: null
        };
    },

    componentWillMount: function() {
        FormStore.addChangeListener(this._onFormChange);
        PostStore.addChangeListener(this._onPostChange);
        FormActionCreators.getForm(id);
    },

    componentWillUnmount: function() {
        FormStore.removeChangeListener(this._onFormChange);
        PostStore.removeChangeListener(this._onPostChange);
    },


    _onFormChange: function() {
        let schema = FormStore.getForm(id).schema;
        let form = FormStore.getForm(id).form;
        let action = FormStore.getForm(id).action;
        console.log('onFormChange', this.props.params.index, NewsStore.getNewsPosts()[this.props.params.index]);
        this.setState({
            schema: schema,
            form: form,
            action: action,
            model: NewsStore.getNewsPosts()[this.props.params.index]
        });
    },

    _onPostChange: function() {
        console.log('NewsPostUpdate._onPostChange', PostStore.getResult(), PostStore.getErrors());
        // TODO display toaster

    },

    _onModelChange: function(key, val) {
        this.setState({model: utils.selectOrSet(key, this.state.model, val)});
    },

    _onTouchTap: function(action) {
        action.data = this.state.model;
        NewsActionCreators.updPost(action);
    },

    render: function() {
        console.log('model', this.state.model);
        if(this.state.schema) {
            var actions = [];
            {this.state.action.map((item, index) => {
                let boundTouchTap = this._onTouchTap.bind(this, item);
                actions.push(<RaisedButton key={index} label={item.title} primary={true} onTouchTap={boundTouchTap} />)
            })}
            return (
                <div>
                    <SchemaForm schema={this.state.schema} model={this.state.model} form={this.state.form} onModelChange={this._onModelChange} />
                    {actions}
                </div>
            )
        } else {
            return <CircularProgress mode="indeterminate"/>
        }
    }
});

module.exports = NewsPostUpdate;
