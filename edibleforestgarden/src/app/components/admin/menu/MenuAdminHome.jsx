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
import Tabs from 'material-ui/lib/tabs/tabs';
import Tab from 'material-ui/lib/tabs/tab';
import CircularProgress from 'material-ui/lib/circular-progress';
import MenuAdminStore from '../../../stores/MenuAdminStore';
import MenuActionCreators from '../../../actions/MenuActionCreators';
import FormActionCreators from '../../../actions/FormActionCreators';

var MenuAdminHome = React.createClass({
    displayName: 'MenuAdminHome',

    getInitialState: function() {
        return {
            menus: [],
            menuItems: []
        };
    },

    componentWillMount: function() {
        MenuAdminStore.addChangeListener(this._onMenuChange);
        MenuActionCreators.getAllMenu();
    },

    componentWillUnmount: function() {
        MenuAdminStore.removeChangeListener(this._onMenuChange);
    },

    _onMenuChange: function() {
        console.log('MenuAdminHome._onMenuChange', MenuAdminStore.getMenus(), MenuAdminStore.getMenuItems());
        this.setState({
            menus: MenuAdminStore.getMenus(),
            menuItems: MenuAdminStore.getMenuItems()
        });
    },

    _onDeleteMenu: function(menu) {
        MenuActionCreators.delMenu(menu['@rid']);
    },

    _onDeleteMenuItem: function(menuItem) {
        MenuActionCreators.delMenuItem(menuItem['@rid']);
    },

    _onUpdateMenu: function(menu) {
        let formId = 'com.networknt.light.menu.updateMenu';
        FormActionCreators.setFormModel(formId, menu);
        this.props.history.push('/form/' + formId);
    },

    _onUpdateMenuItem: function(menuItem) {
        let formId = 'com.networknt.light.menu.updateMenuItem';
        FormActionCreators.setFormModel(formId, menuItem);
        this.props.history.push('/form/' + formId);
    },

    _onAddMenu: function() {
        let formId = 'com.networknt.light.menu.addMenu';
        this.props.history.push('/form/' + formId);
    },

    _onAddMenuItem: function() {
        let formId = 'com.networknt.light.menu.addMenuItem';
        this.props.history.push('/form/' + formId);
    },

    render: function() {
        return (
            <Tabs>
                <Tab label="Menus">
                    <Table
                        height={'1080px'}
                        fixedHeader={true}
                        fixedFooter={true}
                        selectable={false}
                        multiSelectable={false}>
                        <TableHeader enableSelectAll={false}>
                            <TableRow>
                                <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                                <TableHeaderColumn tooltip='Host'>Host</TableHeaderColumn>
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

                            {this.state.menus.map((menu, index) => {
                                let boundDeleteMenu = this._onDeleteMenu.bind(this, menu);
                                let boundUpdateMenu = this._onUpdateMenu.bind(this, menu);
                                return (
                                    <TableRow key={index}>
                                        <TableRowColumn><a onClick={boundDeleteMenu}>Delete</a></TableRowColumn>
                                        <TableRowColumn><a onClick={boundUpdateMenu}>{menu.host}</a></TableRowColumn>
                                       <TableRowColumn>{menu.createUserId}</TableRowColumn>
                                        <TableRowColumn>{menu.createDate}</TableRowColumn>
                                        <TableRowColumn>{menu.updateUserId}</TableRowColumn>
                                        <TableRowColumn>{menu.updateDate}</TableRowColumn>
                                    </TableRow>
                                );
                            })}

                        </TableBody>

                        <TableFooter>
                            <TableRow>
                                <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                                <TableHeaderColumn tooltip='Host'>Host</TableHeaderColumn>
                                <TableHeaderColumn tooltip='Create UserId'>Create UserId</TableHeaderColumn>
                                <TableHeaderColumn tooltip='Create Date'>Create Date</TableHeaderColumn>
                                <TableHeaderColumn tooltip='Update UserId'>Update UserId</TableHeaderColumn>
                                <TableHeaderColumn tooltip='Update Date'>Update Date</TableHeaderColumn>
                            </TableRow>
                            <TableRow>
                                <TableRowColumn colSpan="6" style={{textAlign: 'left'}}>
                                    <RaisedButton label="Add Menu" primary={true} onTouchTap={this._onAddMenu} />
                                </TableRowColumn>
                            </TableRow>

                        </TableFooter>
                    </Table>
                </Tab>
                <Tab label="Menu Items">
                    <Table
                        height={'1080px'}
                        fixedHeader={true}
                        fixedFooter={true}
                        selectable={false}
                        multiSelectable={false}>
                        <TableHeader enableSelectAll={false}>
                            <TableRow>
                                <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                                <TableHeaderColumn tooltip='Menu Item Id'>Menu Item Id</TableHeaderColumn>
                                <TableHeaderColumn tooltip='Route'>Route</TableHeaderColumn>
                                <TableHeaderColumn tooltip='Text'>Text</TableHeaderColumn>
                                <TableHeaderColumn tooltip='Roles'>Roles</TableHeaderColumn>
                                <TableHeaderColumn tooltip='Create UserId'>Create UserId</TableHeaderColumn>
                                <TableHeaderColumn tooltip='Create Date'>Create Date</TableHeaderColumn>
                            </TableRow>
                        </TableHeader>
                        <TableBody
                            deselectOnClickaway={false}
                            showRowHover={true}
                            stripedRows={true}>

                            {this.state.menuItems.map((menuItem, index) => {
                                let boundDeleteItem = this._onDeleteMenuItem.bind(this, menuItem);
                                let boundUpdateItem = this._onUpdateMenuItem.bind(this, menuItem);
                                return (
                                    <TableRow key={index}>
                                        <TableRowColumn><a onClick={boundDeleteItem}>Delete</a></TableRowColumn>
                                        <TableRowColumn><a onClick={boundUpdateItem}>{menuItem.menuItemId}</a></TableRowColumn>
                                        <TableRowColumn>{menuItem.route}</TableRowColumn>
                                        <TableRowColumn>{menuItem.text}</TableRowColumn>
                                        <TableRowColumn>{menuItem.roles.toString()}</TableRowColumn>
                                        <TableRowColumn>{menuItem.createUserId}</TableRowColumn>
                                        <TableRowColumn>{menuItem.createDate}</TableRowColumn>
                                    </TableRow>
                                );
                            })}

                        </TableBody>

                        <TableFooter>
                            <TableRow>
                                <TableHeaderColumn tooltip='Delete'>Delete</TableHeaderColumn>
                                <TableHeaderColumn tooltip='Menu Item Id'>Menu Item Id</TableHeaderColumn>
                                <TableHeaderColumn tooltip='Route'>Route</TableHeaderColumn>
                                <TableHeaderColumn tooltip='Text'>Text</TableHeaderColumn>
                                <TableHeaderColumn tooltip='Roles'>Roles</TableHeaderColumn>
                                <TableHeaderColumn tooltip='Create UserId'>Create UserId</TableHeaderColumn>
                                <TableHeaderColumn tooltip='Create Date'>Create Date</TableHeaderColumn>
                            </TableRow>
                            <TableRow>
                                <TableRowColumn colSpan="6" style={{textAlign: 'left'}}>
                                    <RaisedButton label="Add MenuItem" primary={true} onTouchTap={this._onAddMenuItem} />
                                </TableRowColumn>
                            </TableRow>

                        </TableFooter>
                    </Table>
                </Tab>
            </Tabs>
        );
    }
});

module.exports = MenuAdminHome;
