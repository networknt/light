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

    _onUpdateRule: function() {
        let formId = 'com.networknt.light.rule.update';
        let rule = this.state.rules[this.selectIndex];
        console.log('rule=', rule);
        FormActionCreators.setFormModel(formId, rule);
        this.context.router.push('/form/' + formId);
    },

    _onAddRule: function() {
        let formId = 'com.networknt.light.rule.add';
        this.context.router.push('/form/' + formId);
    },

    _onReqTransform: function() {
        let formId = 'com.networknt.light.rule.req.transform_d';
        let rule = this.state.rules[this.selectIndex];
        if(rule.reqTransforms) {
            rule.reqTransforms.forEach(function(transform) {
                if(transform.transformData) {
                    transform.transformData = JSON.stringify(transform.transformData, undefined, 2);
                }
            });
        }
        FormActionCreators.setFormModel(formId, rule);
        this.context.router.push('/form/' + formId);
    },

    _onResTransform: function() {
        let formId = 'com.networknt.light.rule.res.transform_d';
        let rule = this.state.rules[this.selectIndex];
        if(rule.resTransforms) {
            rule.resTransforms.forEach(function(transform) {
                if(transform.transformData) {
                    transform.transformData = JSON.stringify(transform.transformData, undefined, 2);
                }
            });
        }
        FormActionCreators.setFormModel(formId, rule);
        this.context.router.push('/form/' + formId);
    },

    _onPublisher: function() {
        let formId = 'com.networknt.light.rule.publisher.update';
        let rule = this.state.rules[this.selectIndex];
        FormActionCreators.setFormModel(formId, rule);
        this.context.router.push('/form/' + formId);
    },

    _onSubscriber: function() {
        let formId = 'com.networknt.light.rule.subscriber.update';
        let rule = this.state.rules[this.selectIndex];
        FormActionCreators.setFormModel(formId, rule);
        this.context.router.push('/form/' + formId);
    },

    _onCORS: function() {
        let formId = 'com.networknt.light.rule.cors.update';
        let rule = this.state.rules[this.selectIndex];
        FormActionCreators.setFormModel(formId, rule);
        this.context.router.push('/form/' + formId);
    },

    _onETag: function() {
        let formId = 'com.networknt.light.rule.etag.update';
        let rule = this.state.rules[this.selectIndex];
        FormActionCreators.setFormModel(formId, rule);
        this.context.router.push('/form/' + formId);
    },

    _onSchema: function() {
        let formId = 'com.networknt.light.rule.schema.update';
        let rule = this.state.rules[this.selectIndex];
        rule.schema = JSON.stringify(rule.schema, undefined, 2);
        FormActionCreators.setFormModel(formId, rule);
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

    _onRowSelection: function(selectedRows) {
        console.log('RuleAdminHome._onRowSelection', selectedRows);
        this.selectIndex = selectedRows[0];
        console.log('selectIndex', this.selectIndex);
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
                    selectable={true}
                    onRowSelection={this._onRowSelection}
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
                                <RaisedButton label="Add" primary={true} onTouchTap={this._onAddRule} />
                                <RaisedButton label="Update" primary={true} onTouchTap={this._onUpdateRule} />
                                <RaisedButton label="ReqTransform" primary={true} onTouchTap={this._onReqTransform} />
                                <RaisedButton label="ResTransform" primary={true} onTouchTap={this._onResTransform} />
                                <RaisedButton label="Publisher" primary={true} onTouchTap={this._onPublisher} />
                                <RaisedButton label="Subscriber" primary={true} onTouchTap={this._onSubscriber} />
                                <RaisedButton label="CORS" primary={true} onTouchTap={this._onCORS} />
                                <RaisedButton label="ETag" primary={true} onTouchTap={this._onETag} />
                                <RaisedButton label="Schema" primary={true} onTouchTap={this._onSchema} />
                            </TableRowColumn>
                        </TableRow>

                    </TableFooter>
                </Table>
            </span>
        );
    }
});

module.exports = RuleAdminHome;
