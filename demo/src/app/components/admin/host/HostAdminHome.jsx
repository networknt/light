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
import HostAdminStore from '../../../stores/HostAdminStore';
import HostActionCreators from '../../../actions/HostActionCreators';
import FormActionCreators from '../../../actions/FormActionCreators';

var HostAdminHome = React.createClass({
    displayName: 'HostAdminHome',

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: function() {
        return {
            hosts: [],
            filter: {}
        };
    },

    componentWillMount: function() {
        HostAdminStore.addChangeListener(this._onHostChange);
        HostActionCreators.getAllHost();
    },

    componentWillUnmount: function() {
        HostAdminStore.removeChangeListener(this._onHostChange);
    },

    _onHostChange: function() {
        //console.log('HostAdminHome._onHostChange', HostAdminStore.getHosts());
        this.setState({
            hosts: HostAdminStore.getHosts()
        });
    },

    _onDeleteHost: function(host) {
        HostActionCreators.delHost(host);
    },

    _onUpdateHost: function(host) {
        let formId = 'com.networknt.light.host.update';
        FormActionCreators.setFormModel(formId, host);
        this.context.router.push('/form/' + formId);
    },

    _onAddHost: function() {
        let formId = 'com.networknt.light.host.add';
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
                            <TableHeaderColumn colSpan="9" tooltip='hosts' style={{textAlign: 'center'}}>
                                Hosts
                            </TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Host' colSpan="3">Host</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Base' colSpan="3">Base</TableHeaderColumn>
                            <TableHeaderColumn tooltip='TransferMinSize'>TransferMinSize</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create UserId'>Create UserId</TableHeaderColumn>
                        </TableRow>
                    </TableHeader>
                    <TableBody
                        deselectOnClickaway={false}
                        showRowHover={true}
                        stripedRows={true}>

                        {this.state.hosts.map((host, index) => {
                            let boundDelete = this._onDeleteHost.bind(this, host);
                            let boundUpdate = this._onUpdateHost.bind(this, host);
                            return (
                                <TableRow key={index}>
                                    <TableRowColumn><a onClick={boundDelete}>Delete</a></TableRowColumn>
                                    <TableRowColumn colSpan="3"><a onClick={boundUpdate}>{host.hostId}</a></TableRowColumn>
                                    <TableRowColumn colSpan="3">{host.base}</TableRowColumn>
                                    <TableRowColumn>{host.transferMinSize}</TableRowColumn>
                                    <TableRowColumn>{host.createUserId}</TableRowColumn>
                                </TableRow>
                            );
                        })}

                    </TableBody>

                    <TableFooter>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Host' colSpan="3">Host</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Base' colSpan="3">Base</TableHeaderColumn>
                            <TableHeaderColumn tooltip='TransferMinSize'>TransferMinSize</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create UserId'>Create UserId</TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableRowColumn colSpan="9" style={{textAlign: 'left'}}>
                                <RaisedButton label="Add Host" primary={true} onTouchTap={this._onAddHost} />
                            </TableRowColumn>
                        </TableRow>

                    </TableFooter>
                </Table>
            </span>
        );
    }
});

module.exports = HostAdminHome;
