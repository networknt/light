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
require('rc-pagination/assets/index.css');
import Pagination from 'rc-pagination';
import Locale from 'rc-pagination/lib/locale/en_US';
require('rc-select/assets/index.css');
import Select from 'rc-select';

import UserAdminStore from '../../../stores/UserAdminStore';
import UserActionCreators from '../../../actions/UserActionCreators';
import FormActionCreators from '../../../actions/FormActionCreators';

var UserAdminHome = React.createClass({
    displayName: 'UserAdminHome',

    getInitialState: function() {
        return {
            users: [],
            total: 0,
            pageSize: 10,
            pageNo: 1
        };
    },

    componentWillMount: function() {
        UserAdminStore.addChangeListener(this._onUserChange);
        UserActionCreators.getAllUser(this.state.pageNo, this.state.pageSize);
    },

    componentWillUnmount: function() {
        UserAdminStore.removeChangeListener(this._onUserChange);
    },

    _onUserChange: function() {
        console.log('UserAdminHome._onUserChange', UserAdminStore.getUsers(), UserAdminStore.getTotal());
        this.setState({
            users: UserAdminStore.getUsers(),
            total: UserAdminStore.getTotal()
        });
    },

    _onDeleteUser: function(user) {
        UserActionCreators.delUser(user['@rid']);
    },

    _onUpdateUser: function(user) {
        let formId = 'com.networknt.light.user.update.profile';
        FormActionCreators.setFormModel(formId, user);
        this.props.history.push('/form/' + formId);
    },

    _onPageNoChange: function (key) {
        this.setState({
            pageNo: key
        });
        // use key instead of this.state.pageNo as setState is async.
        UserActionCreators.getAllUser(key, this.state.pageSize);
    },

    _onPageSizeChange: function (current, pageSize) {
        this.setState({
            pageSize: pageSize
        });
        UserActionCreators.getAllUser(this.state.pageNo, pageSize);
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
                            <TableHeaderColumn colSpan="8" tooltip='Users' style={{textAlign: 'center'}}>
                                Users
                            </TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='User Id'>User Id</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Host'>Host</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Roles'>Roles</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Email' colSpan="2">Email</TableHeaderColumn>
                            <TableHeaderColumn tooltip='First Name'>First Name</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Last Name'>Last Name</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create Date'>Create Date</TableHeaderColumn>
                        </TableRow>
                    </TableHeader>
                    <TableBody
                        deselectOnClickaway={false}
                        showRowHover={true}
                        stripedRows={true}>

                        {this.state.users.map((user, index) => {
                            let boundDelete = this._onDeleteUser.bind(this, user);
                            let boundUpdate = this._onUpdateUser.bind(this, user);
                            return (
                                <TableRow key={index}>
                                    <TableRowColumn><a onClick={boundDelete}>Delete</a></TableRowColumn>
                                    <TableRowColumn><a onClick={boundUpdate}>{user.userId}</a></TableRowColumn>
                                    <TableRowColumn>{user.host}</TableRowColumn>
                                    <TableRowColumn>{user.roles.toString()}</TableRowColumn>
                                    <TableRowColumn colSpan="2">{user.email}</TableRowColumn>
                                    <TableRowColumn>{user.firstName}</TableRowColumn>
                                    <TableRowColumn>{user.lastName}</TableRowColumn>
                                    <TableRowColumn>{user.createDate}</TableRowColumn>
                                </TableRow>
                            );
                        })}

                    </TableBody>

                    <TableFooter>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='User Id'>User Id</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Host'>Host</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Roles'>Roles</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Email' colSpan="2">Email</TableHeaderColumn>
                            <TableHeaderColumn tooltip='First Name'>First Name</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Last Name'>Last Name</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create Date'>Create Date</TableHeaderColumn>
                        </TableRow>
                    </TableFooter>
                </Table>
                <Pagination locale={Locale} selectComponentClass={Select} showSizeChanger={true} pageSizeOptions={['10', '25', '50', '100']} onShowSizeChange={this._onPageSizeChange} onChange={this._onPageNoChange} current={this.state.pageNo} pageSize={this.state.pageSize} total={this.state.total}/>
            </span>
        );
    }
});

module.exports = UserAdminHome;
