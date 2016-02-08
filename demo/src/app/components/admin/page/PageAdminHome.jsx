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
import PageAdminStore from '../../../stores/PageAdminStore';
import PageActionCreators from '../../../actions/PageActionCreators';

var PageAdminHome = React.createClass({
    displayName: 'PageAdminHome',

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: function() {
        return {
            pages: []
        };
    },

    componentWillMount: function() {
        PageAdminStore.addChangeListener(this._onPageChange);
        PageActionCreators.getAllPage();
    },

    componentWillUnmount: function() {
        PageAdminStore.removeChangeListener(this._onPageChange);
    },

    _onPageChange: function() {
        console.log('PageAdminHome._onPageChange', PageAdminStore.getPages());
        this.setState({
            pages: PageAdminStore.getPages()
        });
    },

    _onDeletePage: function(page) {
        PageActionCreators.delPage(page['@rid']);
    },

    _onUpdatePage: function(page) {
        let formId = 'com.networknt.light.page.update';
        FormActionCreators.setFormModel(formId, page);
        this.context.router.push('/form/' + formId);
    },

    _onAddPage: function() {
        let formId = 'com.networknt.light.page.add';
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
                            <TableHeaderColumn colSpan="11" tooltip='Pages' style={{textAlign: 'center'}}>
                                Pages
                            </TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Page Id' colSpan="3">Page Id</TableHeaderColumn>
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

                        {this.state.pages.map((page, index) => {
                            let boundDelete = this._onDeletePage.bind(this, page);
                            let boundUpdate = this._onUpdatePage.bind(this, page);
                            return (
                                <TableRow key={index}>
                                    <TableRowColumn><a onClick={boundDelete}>Delete</a></TableRowColumn>
                                    <TableRowColumn colSpan="3"><a onClick={boundUpdate}>{page.pageId}</a></TableRowColumn>
                                    <TableRowColumn colSpan="3">{page.host}</TableRowColumn>
                                    <TableRowColumn>{page.createUserId}</TableRowColumn>
                                    <TableRowColumn>{page.createDate}</TableRowColumn>
                                    <TableRowColumn>{page.updateUserId}</TableRowColumn>
                                    <TableRowColumn>{page.updateDate}</TableRowColumn>
                                </TableRow>
                            );
                        })}

                    </TableBody>

                    <TableFooter>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Page Id' colSpan="3">Page Id</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Host' colSpan="3">Host</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create UserId'>Create UserId</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create Date'>Create Date</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Update UserId'>Update UserId</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Update Date'>Update Date</TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableRowColumn colSpan="11" style={{textAlign: 'left'}}>
                                <RaisedButton label="Add Page" primary={true} onTouchTap={this._onAddPage} />
                            </TableRowColumn>
                        </TableRow>

                    </TableFooter>
                </Table>
            </span>
        );
    }
});

module.exports = PageAdminHome;
