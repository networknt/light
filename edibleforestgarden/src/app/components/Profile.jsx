import React from 'react';
import Table from 'material-ui/lib/table/table';
import TableHeaderColumn from 'material-ui/lib/table/table-header-column';
import TableRow from 'material-ui/lib/table/table-row';
import TableHeader from 'material-ui/lib/table/table-header';
import TableRowColumn from 'material-ui/lib/table/table-row-column';
import TableBody from 'material-ui/lib/table/table-body';
import UserStore from '../stores/UserStore';
import AuthStore from '../stores/AuthStore';
import UserActionCreators from '../actions/UserActionCreators';

let Profile = React.createClass({
    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: function() {
        return {
            user: {}
        };
    },

    componentWillMount: function() {
        UserStore.addChangeListener(this._onUserChange);
        // check if user exists in store.
        if(UserStore.getUser()) {
            this.setState({
                user: UserStore.getUser()
            })
        } else {
            UserActionCreators.getUser(AuthStore.getUserId());
        }
    },

    componentWillUnmount: function() {
        UserStore.removeChangeListener(this._onUserChange);
    },

    _onUserChange: function() {
        this.setState({
            user: UserStore.getUser()
        });
    },

    render() {
        console.log('shippingAddress', this.state.user.shippingAddress);
        let shippingAddress = '';
        if(this.state.user.shippingAddress) {
            let addressInfo = [];
            for(var key in this.state.user.shippingAddress) {
                if(this.state.user.shippingAddress.hasOwnProperty(key)) {
                    addressInfo.push(
                        <TableRow>
                            <TableRowColumn>{key}</TableRowColumn>
                            <TableRowColumn>{this.state.user.shippingAddress[key]}</TableRowColumn>
                        </TableRow>
                    )
                }
            }
            shippingAddress = (
                <Table selectable={false}>
                    <TableHeader displaySelectAll={false} adjustForCheckbox={false}>
                        <TableRow>
                            <TableHeaderColumn>Key</TableHeaderColumn>
                            <TableHeaderColumn>Value</TableHeaderColumn>
                        </TableRow>
                    </TableHeader>
                    <TableBody displayRowCheckbox={false}>
                        {addressInfo}
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
                <h2>Shipping Address</h2>
                {shippingAddress}
            </span>
        );
    }
});

module.exports = Profile;
