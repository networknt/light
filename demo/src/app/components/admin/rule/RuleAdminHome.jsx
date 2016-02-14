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
import RuleAdminStore from '../../../stores/RuleAdminStore';
import FormStore from '../../../stores/FormStore';
import RuleActionCreators from '../../../actions/RuleActionCreators';
import FormActionCreators from'../../../actions/FormActionCreators';

var RuleAdminHome = React.createClass({
    displayName: 'RuleAdminHome',

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: function() {
        return {
            rules: [],
            filter: {}
        };
    },

    componentWillMount: function() {
        RuleAdminStore.addChangeListener(this._onRuleChange);
        RuleActionCreators.getRule();
    },

    componentWillUnmount: function() {
        RuleAdminStore.removeChangeListener(this._onRuleChange);
    },

    _onRuleChange: function() {
        this.setState({
            rules: RuleAdminStore.getRules()
        });
    },

    _onDeleteRule: function(rule) {
        //console.log("_onDeleteRule", rule);
        RuleActionCreators.delRule(rule);
    },

    _onUpdateRule: function(rule) {
        let formId = 'com.networknt.light.rule.update';
        FormActionCreators.setFormModel(formId, rule);
        this.context.router.push('/form/' + formId);
    },

    _onAddRule: function() {
        let formId = 'com.networknt.light.rule.add';
        this.context.router.push('/form/' + formId);
    },

    _onFilterChange: function (event) {
        let filter = {
            ruleClass: this.refs.ruleClass.value,
            createUserId: this.refs.createUserId.value
        };
        if(this._throttleTimeout) {
            clearTimeout(this._throttleTimeout);
        }
        this._throttleTimeout = setTimeout(() => this.setState({filter: filter}), 200);
    },


    render: function() {
        let content = this.state.rules.map((rule, index) => {
            let matched = true;
            for(var key in this.state.filter) {
                if(this.state.filter.hasOwnProperty(key) && this.state.filter[key].length > 0) {
                    let regex = new RegExp(this.state.filter[key], 'i');
                    if(rule[key].search(regex) == -1) {
                        matched = false;
                        break;
                    }
                }
            }
            if(matched) {
                let boundDelete = this._onDeleteRule.bind(this, rule);
                let boundUpdate = this._onUpdateRule.bind(this, rule);
                return (
                    <TableRow key={index}>
                        <TableRowColumn><a onClick={boundDelete}>Delete</a></TableRowColumn>
                        <TableRowColumn colSpan="3"><a onClick={boundUpdate}>{rule.ruleClass}</a></TableRowColumn>
                        <TableRowColumn>{rule.createUserId}</TableRowColumn>
                    </TableRow>
                );
            }
        });

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
                            <TableHeaderColumn colSpan="5" tooltip='Rules' style={{textAlign: 'center'}}>
                                Rules
                            </TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Rule Class' colSpan="3">Rule Class</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create UserId'>Create UserId</TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableHeaderColumn></TableHeaderColumn>
                            <TableHeaderColumn colSpan="3"><input type="text" ref="ruleClass" onChange={this._onFilterChange}/></TableHeaderColumn>
                            <TableHeaderColumn><input type="text" ref="createUserId" onChange={this._onFilterChange}/></TableHeaderColumn>
                        </TableRow>

                    </TableHeader>
                    <TableBody
                        deselectOnClickaway={false}
                        showRowHover={true}
                        stripedRows={true}>
                        {content}
                    </TableBody>

                    <TableFooter>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Rule Class' colSpan="3">Rule Class</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create UserId'>Create UserId</TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableRowColumn colSpan="5" style={{textAlign: 'left'}}>
                                <RaisedButton label="Add Rule" primary={true} onTouchTap={this._onAddRule} />
                            </TableRowColumn>
                        </TableRow>

                    </TableFooter>
                </Table>
            </span>
        );
    }
});

module.exports = RuleAdminHome;
