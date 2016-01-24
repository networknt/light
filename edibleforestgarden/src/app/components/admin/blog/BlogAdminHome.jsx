import React from 'react';
import Table from 'material-ui/lib/table/table';
import TableBody from 'material-ui/lib/table/table-body';
import TableFooter from 'material-ui/lib/table/table-footer';
import TableHeader from 'material-ui/lib/table/table-header';
import TableHeaderColumn from 'material-ui/lib/table/table-header-column';
import TableRow from 'material-ui/lib/table/table-row';
import TableRowColumn from 'material-ui/lib/table/table-row-column';
import RaisedButton from 'material-ui/lib/raised-button';
import BlogAdminStore from '../../../stores/BlogAdminStore';
import FormStore from '../../../stores/FormStore';
import BlogActionCreators from '../../../actions/BlogActionCreators';
import FormActionCreators from'../../../actions/FormActionCreators';

var BlogAdminHome = React.createClass({
    displayName: 'BlogAdminHome',

    getInitialState: function() {
        return {
            blogs: [],
        };
    },

    componentWillMount: function() {
        BlogAdminStore.addChangeListener(this._onBlogChange);
        BlogActionCreators.getBlog();
    },

    componentWillUnmount: function() {
        BlogAdminStore.removeChangeListener(this._onBlogChange);
    },

    _onBlogChange: function() {
        this.setState({
            blogs: BlogAdminStore.getBlogs()
        });
    },

    _onDeleteBlog: function(blog) {
        //console.log("_onDeleteBlog", blog);
        BlogActionCreators.delBlog(blog['@rid']);
    },

    _onUpdateBlog: function(blog) {
        //console.log("_onUpdateBlog", blog);
        let formId = 'com.networknt.light.blog.update';
        FormActionCreators.setFormModel(formId, blog);
        this.props.history.push('/form/' + formId);
    },

    _onAddBlog: function() {
        let formId = 'com.networknt.light.blog.add';
        this.props.history.push('/form/' + formId);
    },

    render: function() {
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
            </span>
        );
    }
});

module.exports = BlogAdminHome;
