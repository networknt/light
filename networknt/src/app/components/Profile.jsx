import React from 'react';
import Table from 'material-ui/lib/table/table';
import TableHeaderColumn from 'material-ui/lib/table/table-header-column';
import TableRow from 'material-ui/lib/table/table-row';
import TableHeader from 'material-ui/lib/table/table-header';
import TableRowColumn from 'material-ui/lib/table/table-row-column';
import TableBody from 'material-ui/lib/table/table-body';
import AuthStore from '../stores/AuthStore';

class Profile extends React.Component {
    render() {
        console.log('shippingAddress', AuthStore.getShippingAddress());
        let shippingAddress = '';
        if(AuthStore.getShippingAddress()) {
            shippingAddress = (
                <Table selectable={false}>
                    <TableHeader displaySelectAll={false} adjustForCheckbox={false}>
                        <TableRow>
                            <TableHeaderColumn>Key</TableHeaderColumn>
                            <TableHeaderColumn>Value</TableHeaderColumn>
                        </TableRow>
                    </TableHeader>
                    <TableBody displayRowCheckbox={false}>
                        <TableRow>
                            <TableRowColumn>User Id</TableRowColumn>
                            <TableRowColumn>{AuthStore.getUserId()}</TableRowColumn>
                        </TableRow>
                        <TableRow>
                            <TableRowColumn>Roles</TableRowColumn>
                            <TableRowColumn>{AuthStore.getRoles()}</TableRowColumn>
                        </TableRow>
                    </TableBody>
                </Table>
            )
        }
        return (
            <span>
                <h2>Profile</h2>
                <Table selectable={false}>
                    <TableHeader displaySelectAll={false} adjustForCheckbox={false}>
                        <TableRow>
                            <TableHeaderColumn>Key</TableHeaderColumn>
                            <TableHeaderColumn>Value</TableHeaderColumn>
                        </TableRow>
                    </TableHeader>
                    <TableBody displayRowCheckbox={false}>
                        <TableRow>
                            <TableRowColumn>User Id</TableRowColumn>
                            <TableRowColumn>{AuthStore.getUserId()}</TableRowColumn>
                        </TableRow>
                        <TableRow>
                            <TableRowColumn>Roles</TableRowColumn>
                            <TableRowColumn>{AuthStore.getRoles().toString()}</TableRowColumn>
                        </TableRow>
                    </TableBody>
                </Table>
                {shippingAddress}
            </span>
        );
    }
}

export default Profile;
