import React from 'react';
import Table from 'material-ui/lib/table/table';
import TableBody from 'material-ui/lib/table/table-body';
import TableFooter from 'material-ui/lib/table/table-footer';
import TableHeader from 'material-ui/lib/table/table-header';
import TableHeaderColumn from 'material-ui/lib/table/table-header-column';
import TableRow from 'material-ui/lib/table/table-row';
import TableRowColumn from 'material-ui/lib/table/table-row-column';
import RaisedButton from 'material-ui/lib/raised-button';
import ForumAdminStore from '../../../stores/ForumAdminStore';
import FormStore from '../../../stores/FormStore';
import ForumActionCreators from '../../../actions/ForumActionCreators';
import FormActionCreators from'../../../actions/FormActionCreators';

var ForumAdminHome = React.createClass({
    displayName: 'ForumAdminHome',

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: function() {
        return {
            forums: []
        };
    },

    componentWillMount: function() {
        ForumAdminStore.addChangeListener(this._onForumChange);
        ForumActionCreators.getForum();
    },

    componentWillUnmount: function() {
        ForumAdminStore.removeChangeListener(this._onForumChange);
    },

    _onForumChange: function() {
        this.setState({
            forums: ForumAdminStore.getForums()
        });
    },

    _onDeleteForum: function(forum) {
        ForumActionCreators.delForum(forum['@rid']);
    },

    _onUpdateForum: function(forum) {
        let formId = 'com.networknt.light.forum.update';
        FormActionCreators.setFormModel(formId, forum);
        this.context.router.push('/form/' + formId);
    },

    _onAddForum: function() {
        let formId = 'com.networknt.light.forum.add';
        this.context.router.push('/form/' + formId);
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
                            <TableHeaderColumn colSpan="4" tooltip='Forums' style={{textAlign: 'center'}}>
                                Forums
                            </TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Host'>Host</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Category Id'>Category Id</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Description'>Description</TableHeaderColumn>
                        </TableRow>
                    </TableHeader>
                    <TableBody
                        deselectOnClickaway={false}
                        showRowHover={true}
                        stripedRows={true}>

                        {this.state.forums.map((forum, index) => {
                            let boundDelete = this._onDeleteForum.bind(this, forum);
                            let boundUpdate = this._onUpdateForum.bind(this, forum);
                            return (
                                <TableRow key={index}>
                                    <TableRowColumn><a onClick={boundDelete}>Delete</a></TableRowColumn>
                                    <TableRowColumn>{forum.host}</TableRowColumn>
                                    <TableRowColumn><a onClick={boundUpdate}>{forum.categoryId}</a></TableRowColumn>
                                    <TableRowColumn>{forum.description}</TableRowColumn>
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
                        </TableRow>
                        <TableRow>
                            <TableRowColumn colSpan="4" style={{textAlign: 'left'}}>
                                <RaisedButton label="Add Forum" primary={true} onTouchTap={this._onAddForum} />
                            </TableRowColumn>
                        </TableRow>
                    </TableFooter>
                </Table>
            </span>
        );
    }
});

module.exports = ForumAdminHome;
