import React from 'react';
import CircularProgress from 'material-ui/lib/circular-progress';
import RaisedButton from 'material-ui/lib/raised-button';
import Tabs from 'material-ui/lib/tabs/tabs';
import Tab from 'material-ui/lib/tabs/tab';
import FormStore from '../../stores/FormStore';
import PostStore from '../../stores/PostStore.jsx';
import FormActionCreators from '../../actions/FormActionCreators';
import BlogActionCreators from '../../actions/BlogActionCreators';
import BlogCategoryStore from '../../stores/BlogCategoryStore';
import Markdown from '../Markdown';
import SchemaForm from 'react-schema-form/lib/SchemaForm';
import utils from 'react-schema-form/lib/utils';
import CommonUtils from '../../utils/CommonUtils';

const id = 'com.networknt.light.blog.post.add';

var BlogPostAdd = React.createClass({

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
        let category = CommonUtils.findCategory(BlogCategoryStore.getCategory(), this.props.params.categoryId);
        this.setState({
            schema: schema,
            form: form,
            action: action,
            model: {parentRid: category['@rid']}
        });
    },

    _onPostChange: function() {
        console.log('BlogPostAdd._onPostChange', PostStore.getResult(), PostStore.getErrors());
        // TODO display toaster

    },

    _onModelChange: function(key, val) {
        this.setState({model: utils.selectOrSet(key, this.state.model, val)});
    },

    _onTouchTap: function(action) {
        console.log('ExecQueryCommand._onTouchTap', action, this.state.model);
        action.data = this.state.model;
        BlogActionCreators.addPost(action);
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
                    <SchemaForm schema={this.state.schema} model={this.state.model} form={this.state.form} onModelChange={this._onModelChange} />
                    {actions}
                    <Tabs initialSelectedIndex={1}>
                        <Tab label="Summary">
                            <Markdown text={this.state.model.summary} />
                        </Tab>
                        <Tab label="Content">
                            <Markdown text={this.state.model.content} />
                        </Tab>
                    </Tabs>
                </div>
            )
        } else {
            return <CircularProgress mode="indeterminate"/>
        }
    }
});

module.exports = BlogPostAdd;