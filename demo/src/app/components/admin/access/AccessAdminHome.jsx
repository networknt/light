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
import AccessAdminStore from '../../../stores/AccessAdminStore';
import AccessActionCreators from '../../../actions/AccessActionCreators';
import FormActionCreators from '../../../actions/FormActionCreators';

var AccessAdminHome = React.createClass({
    displayName: 'AccessAdminHome',

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: function() {
        return {
            accesses: []
        };
    },

    componentWillMount: function() {
        AccessAdminStore.addChangeListener(this._onAccessChange);
        AccessActionCreators.getAllAccess();
    },

    componentWillUnmount: function() {
        AccessAdminStore.removeChangeListener(this._onAccessChange);
    },

    _onAccessChange: function() {
        this.setState({
            accesses: AccessAdminStore.getAllAccess()
        });
    },

    _onDeleteAccess: function(access) {
        AccessActionCreators.delAccess(access['@rid']);
    },

    _onUpdateAccess: function(access) {
        let formId = 'com.networknt.light.access.upd_d';
        FormActionCreators.setFormModel(formId, access);
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
                            <TableHeaderColumn colSpan="8" tooltip='Access' style={{textAlign: 'center'}}>
                                Access
                            </TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Rule Class' colSpan="5">Rule Class</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Access Level'>Access Level</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Clients'>Clients</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Roles'>Roles</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Users'>Users</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create UserId'>Create UserId</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create Date'>Create Date</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Update UserId'>Update UserId</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Update Date'>Update Date</TableHeaderColumn>
                        </TableRow>
                    </TableHeader>
                    <TableBody
                        deselectOnClickaway={false}
                        showRowHover={true}
                        stripedRows={true}>

                        {this.state.accesses.map((access, index) => {
                            let boundDelete = this._onDeleteAccess.bind(this, access);
                            let boundUpdate = this._onUpdateAccess.bind(this, access);
                            return (
                                <TableRow key={index}>
                                    <TableRowColumn><a onClick={boundDelete}>Delete</a></TableRowColumn>
                                    <TableRowColumn colSpan="5"><a onClick={boundUpdate}>{access.ruleClass}</a></TableRowColumn>
                                    <TableRowColumn>{access.accessLevel}</TableRowColumn>
                                    <TableRowColumn>{access.clients}</TableRowColumn>
                                    <TableRowColumn>{access.roles}</TableRowColumn>
                                    <TableRowColumn>{access.users}</TableRowColumn>
                                    <TableRowColumn>{access.createUserId}</TableRowColumn>
                                    <TableRowColumn>{access.createDate}</TableRowColumn>
                                    <TableRowColumn>{access.updateUserId}</TableRowColumn>
                                    <TableRowColumn>{access.updateDate}</TableRowColumn>
                                </TableRow>
                            );
                        })}

                    </TableBody>

                    <TableFooter>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Rule Class' colSpan="5">Rule Class</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Access Level'>Access Level</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Clients'>Clients</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Roles'>Roles</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Users'>Users</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create UserId'>Create UserId</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create Date'>Create Date</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Update UserId'>Update UserId</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Update Date'>Update Date</TableHeaderColumn>
                        </TableRow>
                    </TableFooter>
                </Table>
            </span>
        );
    }
});

module.exports = AccessAdminHome;
