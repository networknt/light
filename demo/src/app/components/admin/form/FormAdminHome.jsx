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
import FormAdminStore from '../../../stores/FormAdminStore';
import FormActionCreators from '../../../actions/FormActionCreators';

var FormAdminHome = React.createClass({
    displayName: 'FormAdminHome',

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: function() {
        return {
            forms: []
        };
    },

    componentWillMount: function() {
        FormAdminStore.addChangeListener(this._onFormChange);
        FormActionCreators.getAllForm();
    },

    componentWillUnmount: function() {
        FormAdminStore.removeChangeListener(this._onFormChange);
    },

    _onFormChange: function() {
        console.log('FormAdminHome._onFormChange', FormAdminStore.getForms());
        this.setState({
            forms: FormAdminStore.getForms()
        });
    },

    _onDeleteForm: function(form) {
        FormActionCreators.delForm(form['@rid']);
    },

    _onUpdateForm: function(form) {
        let formId = 'com.networknt.light.form.update';
        FormActionCreators.setFormModel(formId, form);
        this.context.router.push('/form/' + formId);
    },

    _onAddForm: function() {
        let formId = 'com.networknt.light.form.add';
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
                            <TableHeaderColumn colSpan="11" tooltip='Forms' style={{textAlign: 'center'}}>
                                Forms
                            </TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Form Id' colSpan="3">Form Id</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Host' colSpan="3">Host</TableHeaderColumn>
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

                        {this.state.forms.map((form, index) => {
                            let boundDelete = this._onDeleteForm.bind(this, form);
                            let boundUpdate = this._onUpdateForm.bind(this, form);
                            return (
                                <TableRow key={index}>
                                    <TableRowColumn><a onClick={boundDelete}>Delete</a></TableRowColumn>
                                    <TableRowColumn colSpan="3"><a onClick={boundUpdate}>{form.formId}</a></TableRowColumn>
                                    <TableRowColumn colSpan="3">{form.host}</TableRowColumn>
                                    <TableRowColumn>{form.createUserId}</TableRowColumn>
                                    <TableRowColumn>{form.createDate}</TableRowColumn>
                                    <TableRowColumn>{form.updateUserId}</TableRowColumn>
                                    <TableRowColumn>{form.updateDate}</TableRowColumn>
                                </TableRow>
                            );
                        })}

                    </TableBody>

                    <TableFooter>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Form Id' colSpan="3">Form Id</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Host' colSpan="3">Host</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create UserId'>Create UserId</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create Date'>Create Date</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Update UserId'>Update UserId</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Update Date'>Update Date</TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableRowColumn colSpan="11" style={{textAlign: 'left'}}>
                                <RaisedButton label="Add Form" primary={true} onTouchTap={this._onAddForm} />
                            </TableRowColumn>
                        </TableRow>

                    </TableFooter>
                </Table>
            </span>
        );
    }
});

module.exports = FormAdminHome;
