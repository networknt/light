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
import RoleAdminStore from '../../../stores/RoleAdminStore';
import RoleActionCreators from '../../../actions/RoleActionCreators';
import FormActionCreators from '../../../actions/FormActionCreators';

var RoleAdminHome = React.createClass({
    displayName: 'RoleAdminHome',

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: function() {
        return {
            roles: []
        };
    },

    componentWillMount: function() {
        RoleAdminStore.addChangeListener(this._onRoleChange);
        RoleActionCreators.getRole();
    },

    componentWillUnmount: function() {
        RoleAdminStore.removeChangeListener(this._onRoleChange);
    },

    _onRoleChange: function() {
        //console.log('RoleAdminHome._onRoleChange', RoleAdminStore.getRoles());
        this.setState({
            roles: RoleAdminStore.getRoles()
        });
    },

    _onDeleteRole: function(role) {
        RoleActionCreators.delRole(role['@rid']);
    },

    _onUpdateRole: function(role) {
        let formId = 'com.networknt.light.role.update';
        FormActionCreators.setFormModel(formId, role);
        this.context.router.push('/form/' + formId);
    },

    _onAddRole: function() {
        let formId = 'com.networknt.light.role.add';
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
                            <TableHeaderColumn colSpan="10" tooltip='Roles' style={{textAlign: 'center'}}>
                                Roles
                            </TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Role Id'>Role Id</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Host'>Host</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Description' colSpan="3">Description</TableHeaderColumn>
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

                        {this.state.roles.map((role, index) => {
                            let boundDelete = this._onDeleteRole.bind(this, role);
                            let boundUpdate = this._onUpdateRole.bind(this, role);
                            return (
                                <TableRow key={index}>
                                    <TableRowColumn><a onClick={boundDelete}>Delete</a></TableRowColumn>
                                    <TableRowColumn><a onClick={boundUpdate}>{role.roleId}</a></TableRowColumn>
                                    <TableRowColumn>{role.host}</TableRowColumn>
                                    <TableRowColumn colSpan="3">{role.description}</TableRowColumn>
                                    <TableRowColumn>{role.createUserId}</TableRowColumn>
                                    <TableRowColumn>{role.createDate}</TableRowColumn>
                                    <TableRowColumn>{role.updateUserId}</TableRowColumn>
                                    <TableRowColumn>{role.updateDate}</TableRowColumn>
                                </TableRow>
                            );
                        })}

                    </TableBody>

                    <TableFooter>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Role Id'>Role Id</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Host'>Host</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Description' colSpan="3">Description</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create UserId'>Create UserId</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create Date'>Create Date</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Update UserId'>Update UserId</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Update Date'>Update Date</TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableRowColumn colSpan="10" style={{textAlign: 'left'}}>
                                <RaisedButton label="Add Role" primary={true} onTouchTap={this._onAddRole} />
                            </TableRowColumn>
                        </TableRow>

                    </TableFooter>
                </Table>
            </span>
        );
    }
});

module.exports = RoleAdminHome;
