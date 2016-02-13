import React from 'react';
import Table from 'material-ui/lib/table/table';
import TableBody from 'material-ui/lib/table/table-body';
import TableFooter from 'material-ui/lib/table/table-footer';
import TableHeader from 'material-ui/lib/table/table-header';
import TableHeaderColumn from 'material-ui/lib/table/table-header-column';
import TableRow from 'material-ui/lib/table/table-row';
import TableRowColumn from 'material-ui/lib/table/table-row-column';
import RaisedButton from 'material-ui/lib/raised-button';
import CatalogAdminStore from '../../../stores/CatalogAdminStore';
import FormStore from '../../../stores/FormStore';
import CatalogActionCreators from '../../../actions/CatalogActionCreators';
import FormActionCreators from'../../../actions/FormActionCreators';

var CatalogAdminHome = React.createClass({
    displayName: 'CatalogAdminHome',

    contextTypes: {
        router: React.PropTypes.object.isRequired
    },

    getInitialState: function() {
        return {
            catalogs: []
        };
    },

    componentWillMount: function() {
        CatalogAdminStore.addChangeListener(this._onCatalogChange);
        CatalogActionCreators.getCatalog();
    },

    componentWillUnmount: function() {
        CatalogAdminStore.removeChangeListener(this._onCatalogChange);
    },

    _onCatalogChange: function() {
        this.setState({
            catalogs: CatalogAdminStore.getCatalogs()
        });
    },

    _onDeleteCatalog: function(catalog) {
        //console.log("_onDeleteCatalog", catalog);
        CatalogActionCreators.delCatalog(catalog['@rid']);
    },

    _onUpdateCatalog: function(catalog) {
        //console.log("_onUpdateCatalog", catalog);
        let formId = 'com.networknt.light.catalog.update';
        FormActionCreators.setFormModel(formId, catalog);
        this.context.router.push('/form/' + formId);
    },

    _onAddCatalog: function() {
        let formId = 'com.networknt.light.catalog.add';
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
                            <TableHeaderColumn colSpan="8" tooltip='Catalogs' style={{textAlign: 'center'}}>
                                Catalogs
                            </TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Host'>Host</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Category Id'>Category Id</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Description'>Description</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create Date'>Create Date</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Update Date'>Update Date</TableHeaderColumn>
                        </TableRow>
                    </TableHeader>
                    <TableBody
                        deselectOnClickaway={false}
                        showRowHover={true}
                        stripedRows={true}>

                        {this.state.catalogs.map((catalog, index) => {
                            let boundDelete = this._onDeleteCatalog.bind(this, catalog);
                            let boundUpdate = this._onUpdateCatalog.bind(this, catalog);
                            return (
                                <TableRow key={index}>
                                    <TableRowColumn><a onClick={boundDelete}>Delete</a></TableRowColumn>
                                    <TableRowColumn>{catalog.host}</TableRowColumn>
                                    <TableRowColumn><a onClick={boundUpdate}>{catalog.categoryId}</a></TableRowColumn>
                                    <TableRowColumn>{catalog.description}</TableRowColumn>
                                    <TableRowColumn>{catalog.createDate}</TableRowColumn>
                                    <TableRowColumn>{catalog.updateDate}</TableRowColumn>
                                </TableRow>
                            );
                        })}

                    </TableBody>

                    <TableFooter>
                        <TableRow>
                            <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Host'>Host</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Category Id'>Category Id</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Description'>Description</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Create Date'>Create Date</TableHeaderColumn>
                            <TableHeaderColumn tooltip='Update Date'>Update Date</TableHeaderColumn>
                        </TableRow>
                        <TableRow>
                            <TableRowColumn colSpan="6" style={{textAlign: 'left'}}>
                                <RaisedButton label="Add Catalog" primary={true} onTouchTap={this._onAddCatalog} />
                            </TableRowColumn>
                        </TableRow>
                    </TableFooter>
                </Table>
            </span>
        );
    }
});

module.exports = CatalogAdminHome;
