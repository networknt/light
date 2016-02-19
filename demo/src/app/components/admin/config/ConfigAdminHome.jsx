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
import ConfigAdminStore from '../../../stores/ConfigAdminStore';
import ConfigActionCreators from '../../../actions/ConfigActionCreators';
import FormActionCreators from '../../../actions/FormActionCreators';

var ConfigAdminHome = React.createClass({
    displayName: 'ConfigAdminHome',

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: function() {
        return {
            configs: []
        };
    },

    componentWillMount: function() {
        ConfigAdminStore.addChangeListener(this._onConfigChange);
        ConfigActionCreators.getAllConfig();
    },

    componentWillUnmount: function() {
        ConfigAdminStore.removeChangeListener(this._onConfigChange);
    },

    _onConfigChange: function() {
        console.log('ConfigAdminHome._onConfigChange', ConfigAdminStore.getConfigs());
        this.setState({
            configs: ConfigAdminStore.getConfigs()
        });
    },

    _onDeleteConfig: function(config) {
        ConfigActionCreators.delConfig(config['@rid']);
    },

    _onUpdateConfig: function(config) {
        let formId = 'com.networknt.light.config.update';
        if(config.properties) {
            config.properties = JSON.stringify(config.properties, undefined, 2);
        }
        FormActionCreators.setFormModel(formId, config);
        this.context.router.push('/form/' + formId);
    },

    _onAddConfig: function() {
        let formId = 'com.networknt.light.config.add';
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
                            <TableHeaderColumn colSpan="8" tooltip='Configs' style={{textAlign: 'center'}}>
                                Configs
                            </TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Config Id' colSpan="3">Config Id</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Host' colSpan="3">Host</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create UserId'>Create UserId</TableHeaderColumn>
                        </TableRow>
                    </TableHeader>
                    <TableBody
                        deselectOnClickaway={false}
                        showRowHover={true}
                        stripedRows={true}>

                        {this.state.configs.map((config, index) => {
                            let boundDelete = this._onDeleteConfig.bind(this, config);
                            let boundUpdate = this._onUpdateConfig.bind(this, config);
                            return (
                                <TableRow key={index}>
                                    <TableRowColumn><a onClick={boundDelete}>Delete</a></TableRowColumn>
                                    <TableRowColumn colSpan="3"><a onClick={boundUpdate}>{config.configId}</a></TableRowColumn>
                                    <TableRowColumn colSpan="3">{config.host}</TableRowColumn>
                                    <TableRowColumn>{config.createUserId}</TableRowColumn>
                                </TableRow>
                            );
                        })}

                    </TableBody>

                    <TableFooter>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Config Id' colSpan="3">Config Id</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Host' colSpan="3">Host</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create UserId'>Create UserId</TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableRowColumn colSpan="8" style={{textAlign: 'left'}}>
                                <RaisedButton label="Add Config" primary={true} onTouchTap={this._onAddConfig} />
                            </TableRowColumn>
                        </TableRow>

                    </TableFooter>
                </Table>
            </span>
        );
    }
});

module.exports = ConfigAdminHome;
