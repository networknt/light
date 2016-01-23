import React from 'react';
import Table from 'material-ui/lib/table/table';
import TableBody from 'material-ui/lib/table/table-body';
import TableFooter from 'material-ui/lib/table/table-footer';
import TableHeader from 'material-ui/lib/table/table-header';
import TableHeaderColumn from 'material-ui/lib/table/table-header-column';
import TableRow from 'material-ui/lib/table/table-row';
import TableRowColumn from 'material-ui/lib/table/table-row-column';
import RaisedButton from 'material-ui/lib/raised-button';
import Dialog from 'material-ui/lib/dialog';
import CircularProgress from 'material-ui/lib/circular-progress';
import SchemaForm from 'react-schema-form/lib/SchemaForm';
import RcSelect from 'react-schema-form-rc-select/lib/RcSelect';
import BlogAdminStore from '../../../stores/BlogAdminStore';
import FormStore from '../../../stores/FormStore';
import BlogActionCreators from '../../../actions/BlogActionCreators';
import FormActionCreators from'../../../actions/FormActionCreators';

var BlogAdminHome = React.createClass({
    displayName: 'BlogAdminHome',

    getInitialState: function() {
        return {
            blogs: [],
            formOpen: false,
            formTitle: null,
            formId: null,
            model: {}
        };
    },

    componentWillMount: function() {
        BlogAdminStore.addChangeListener(this._onBlogChange);
        FormStore.addChangeListener(this._onFormChange);
        BlogActionCreators.getBlog();
    },

    componentWillUnmount: function() {
        BlogAdminStore.removeChangeListener(this._onBlogChange);
        FormStore.removeChangeListener(this._onFormChange);
    },

    _onFormChange: function() {
        let schema = FormStore.getForm(this.state.formId).schema;
        let form = FormStore.getForm(this.state.formId).form;
        let action = FormStore.getForm(this.state.formId).action;
        this.setState({
            schema: schema,
            form: form,
            action: action
        });
    },

    _onBlogChange: function() {
        this.setState({
            blogs: BlogAdminStore.getBlogs()
        });
    },

    _onDeleteBlog: function(blog) {
        console.log("_onDeleteBlog", blog);
        BlogActionCreators.delBlog(blog);
    },

    _onUpdateBlog: function(blog) {
        console.log("_onUpdateBlog", blog);
        let formId = 'com.networknt.light.blog.update';
        this.setState({
            formId: formId,
            formTitle: 'Update Blog',
            formOpen: true
        });
        FormActionCreators.getForm(formId);
    },

    _onAddBlog: function() {
        let formId = 'com.networknt.light.blog.add';
        this.setState({
            formId: formId,
            formTitle: 'Add Blog',
            formOpen: true
        });
        FormActionCreators.getForm(formId);
    },

    _onFormClose: function() {
        this.setState({
            formOpen: false
        })
    },

    _onModelChange: function() {

    },

    _onTouchTap: function (action) {
        console.log('BlogAdminHome._onTouchTap', action);

    },

    render: function() {
        var actions = [];
        var contents= (<CircularProgress mode="indeterminate"/>);
        if(this.state.schema) {
            {this.state.action.map((item, index) => {
                let boundTouchTap = this._onTouchTap.bind(this, item);
                actions.push(<RaisedButton key={index} label={item.title} primary={true} onTouchTap={boundTouchTap} />)
            })}
            actions.push(<RaisedButton key="Cancel" label="Cancel" secondary={true} onTouchTap={this._onFormClose} />)

            if(this.state.formId === 'com.networknt.light.blog.add') {
                contents = (<SchemaForm schema={this.state.schema} form={this.state.form} onModelChange={this._onModelChange} mapper= {{"rc-select": RcSelect}} />);
            } else {
                contents = (<SchemaForm schema={this.state.schema} form={this.state.form} model={this.state.model} onModelChange={this._onModelChange} mapper= {{"rc-select": RcSelect}} />);
            }
        }

        return (
            <span>
                <Table
                    height={'1080px'}
                    fixedHeader={true}
                    fixedFooter={true}
                    selectable={false}
                    multiSelectable={false}>
                    <TableHeader enableSelectAll={false}>
                        <TableRow>
                            <TableHeaderColumn colSpan="8" tooltip='Blogs' style={{textAlign: 'center'}}>
                                Blogs
                            </TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Host'>Host</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Category Id'>Category Id</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Description'>Description</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create Date'>Create Date</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Update Date'>Update Date</TableHeaderColumn>
                        </TableRow>
                    </TableHeader>
                    <TableBody
                        deselectOnClickaway={false}
                        showRowHover={true}
                        stripedRows={true}>

                        {this.state.blogs.map((blog, index) => {
                            let boundDelete = this._onDeleteBlog.bind(this, blog);
                            let boundUpdate = this._onUpdateBlog.bind(this, blog);
                            return (
                                <TableRow key={index}>
                                    <TableRowColumn><a onClick={boundDelete}>Delete</a></TableRowColumn>
                                    <TableRowColumn>{blog.host}</TableRowColumn>
                                    <TableRowColumn><a onClick={boundUpdate}>{blog.categoryId}</a></TableRowColumn>
                                    <TableRowColumn>{blog.description}</TableRowColumn>
                                    <TableRowColumn>{blog.createDate}</TableRowColumn>
                                    <TableRowColumn>{blog.updateDate}</TableRowColumn>
                                </TableRow>
                            );
                        })}

                    </TableBody>

                    <TableFooter>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Host'>Host</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Category Id'>Category Id</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Description'>Description</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create Date'>Create Date</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Update Date'>Update Date</TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableRowColumn colSpan="6" style={{textAlign: 'left'}}>
                                <RaisedButton label="Add Blog" primary={true} onTouchTap={this._onAddBlog} />
                            </TableRowColumn>
                        </TableRow>
                    </TableFooter>
                </Table>
                <Dialog title={this.state.formTitle} actions={actions} modal={true} open={this.state.formOpen}>
                    {contents}
                </Dialog>
            </span>
        );
    }
});

module.exports = BlogAdminHome;
